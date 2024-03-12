package de.invesdwin.context.integration.persistentmap.large.storage;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.Caffeine;

import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummary;
import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummaryByteBuffer;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.OperatingSystem;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.file.IMemoryMappedFile;
import de.invesdwin.util.streams.buffer.file.SegmentedMemoryMappedFile;
import de.invesdwin.util.streams.pool.buffered.BufferedFileDataOutputStream;

/**
 * Removed values are not reclaimed, they only get removed from the index. We could implement a compaction process for
 * this sometime (could be done async).
 */
@ThreadSafe
public class MappedChunkStorage<V> implements IChunkStorage<V> {

    private final File memoryDirectory;
    private final List<File> memoryFiles;
    private final ISerde<V> valueSerde;
    private final IReadWriteLock lock = ILockCollectionFactory.getInstance(true)
            .newReadWriteLock(MappedChunkStorage.class.getSimpleName() + "_lock");
    @GuardedBy("lock")
    private long precedingPosition;
    @GuardedBy("lock")
    private long position;
    private volatile IMemoryMappedFile reader;
    private final boolean readOnly;
    private final boolean closeAllowed;

    private final Set<ChunkSummaryByteBuffer> readerBuffers;

    public MappedChunkStorage(final File memoryDirectory, final ISerde<V> valueSerde, final boolean readOnly,
            final boolean closeAllowed) {
        this.memoryDirectory = memoryDirectory;
        this.memoryFiles = new ArrayList<>();
        this.valueSerde = valueSerde;
        this.readOnly = readOnly;
        this.closeAllowed = closeAllowed;
        if (readOnly) {
            readerBuffers = null;
        } else {
            readerBuffers = Collections
                    .newSetFromMap(Caffeine.newBuilder().weakKeys().<ChunkSummaryByteBuffer, Boolean> build().asMap());
        }
        initMemoryFiles();
    }

    public void initMemoryFiles() {
        precedingPosition = 0;
        position = 0;
        while (true) {
            final File curMemoryFile = nextMemoryFile();
            if (curMemoryFile.exists()) {
                precedingPosition += position;
                position = curMemoryFile.length();
                memoryFiles.add(curMemoryFile);
            } else {
                break;
            }
        }
    }

    public File nextMemoryFile() {
        return new File(memoryDirectory, "memory_" + memoryFiles.size() + ".bin");
    }

    private IMemoryMappedFile getReader() {
        if (reader == null) {
            if (memoryFiles.isEmpty()) {
                return null;
            }
            lock.readLock().lock();
            try {
                if (reader == null) {
                    if (memoryFiles.isEmpty()) {
                        return null;
                    } else if (memoryFiles.size() == 1) {
                        final File memoryFile = memoryFiles.get(0);
                        final long positionCopy = position;
                        try {
                            reader = IMemoryMappedFile.map(memoryFile.getAbsolutePath(), 0L, positionCopy, readOnly,
                                    closeAllowed);
                        } catch (final IOException e) {
                            throw new RuntimeException("directory=" + memoryDirectory.getAbsolutePath()
                                    + " fileIndex=0 position=" + positionCopy, e);
                        }
                    } else {
                        final List<IMemoryMappedFile> mappedFiles = new ArrayList<>();
                        final int lastMemoryFileIndex = memoryFiles.size() - 1;
                        for (int i = 0; i <= lastMemoryFileIndex; i++) {
                            final File memoryFile = memoryFiles.get(i);
                            final long precedingPositionCopy = precedingPosition;
                            final long positionCopy;
                            if (i == lastMemoryFileIndex) {
                                positionCopy = position;
                            } else {
                                positionCopy = memoryFile.length();
                            }
                            try {
                                mappedFiles.add(IMemoryMappedFile.map(memoryFile.getAbsolutePath(), 0L, positionCopy,
                                        readOnly, closeAllowed));
                            } catch (final IOException e) {
                                throw new RuntimeException("directory=" + memoryDirectory.getAbsolutePath()
                                        + " fileIndex=" + mappedFiles.size() + " precedingPosition="
                                        + precedingPositionCopy + " position=" + positionCopy, e);
                            }
                        }
                        return new SegmentedMemoryMappedFile(closeAllowed, mappedFiles);
                    }
                }
            } finally {
                lock.readLock().unlock();
            }
        }
        return reader;
    }

    @Override
    public V get(final ChunkSummary summary) {
        lock.readLock().lock();
        try {
            final IMemoryMappedFile reader = getReader();
            if (reader == null) {
                return null;
            }
            final IByteBuffer buffer;
            if (readerBuffers != null) {
                final ChunkSummaryByteBuffer chunkBuffer = new ChunkSummaryByteBuffer(summary);
                chunkBuffer.init(reader);
                readerBuffers.add(chunkBuffer);
                buffer = chunkBuffer;
            } else {
                final int length = Integers.checkedCast(summary.getMemoryLength());
                buffer = reader.newByteBuffer(summary.getMemoryOffset(), length);
            }
            final V value = valueSerde.fromBuffer(buffer);
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void remove(final ChunkSummary summary) {
        //noop
    }

    @Override
    public boolean isRemovable() {
        return false;
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            clearReaderBuffers();
            closeReaderWriteLocked();
            Files.deleteQuietly(memoryDirectory);
            precedingPosition = 0;
            position = 0;
            memoryFiles.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void clearReaderBuffers() {
        if (readerBuffers != null && !readerBuffers.isEmpty()) {
            for (final ChunkSummaryByteBuffer readerBuffer : readerBuffers) {
                readerBuffer.close();
            }
            readerBuffers.clear();
        }
    }

    private void closeReaderWriteLocked() {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }

    @Override
    public ChunkSummary put(final V value) {
        try (ICloseableByteBuffer buffer = ByteBuffers.EXPANDABLE_POOL.borrowObject()) {
            final int length = valueSerde.toBuffer(buffer, value);
            return write(buffer, length);
        }
    }

    private ChunkSummary write(final IByteBuffer buffer, final int length) {
        final long addressOffset;
        lock.writeLock().lock();
        try {
            if (memoryFiles.isEmpty() || OperatingSystem.isWindows() && position > 0
                    && position + length > SegmentedMemoryMappedFile.WINDOWS_MAX_LENGTH_PER_SEGMENT_DISK) {
                precedingPosition += position;
                position = 0;
                memoryFiles.add(nextMemoryFile());
            }
            //support parallel writes from this instance (we expect exclusive access to the file)
            addressOffset = position;
            position += length;
            if (readerBuffers != null) {
                //finalize reader asynchronously so that other threads can evict it properly using GC
                reader = null;
                if (!readerBuffers.isEmpty()) {
                    final IMemoryMappedFile newReader = getReader();
                    for (final ChunkSummaryByteBuffer readerBuffer : readerBuffers) {
                        readerBuffer.init(newReader);
                    }
                }
            } else {
                closeReaderWriteLocked();
            }
        } finally {
            lock.writeLock().unlock();
        }
        lock.readLock().lock();
        try {
            if (!memoryDirectory.exists()) {
                Files.forceMkdir(memoryDirectory);
            }
            final File lastMemoryFile = memoryFiles.get(memoryFiles.size() - 1);
            try (BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(lastMemoryFile)) {
                out.seek(addressOffset);
                buffer.getBytesTo(0, (DataOutput) out, length);
                return new ChunkSummary("", addressOffset, length);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void close() {
        lock.writeLock().lock();
        try {
            clearReaderBuffers();
            closeReaderWriteLocked();
        } finally {
            lock.writeLock().unlock();
        }
    }

}
