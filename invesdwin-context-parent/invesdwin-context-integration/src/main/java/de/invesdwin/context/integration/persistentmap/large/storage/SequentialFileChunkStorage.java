package de.invesdwin.context.integration.persistentmap.large.storage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummary;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.file.IMemoryMappedFile;
import de.invesdwin.util.streams.pool.buffered.BufferedFileDataInputStream;
import de.invesdwin.util.streams.pool.buffered.BufferedFileDataOutputStream;

/**
 * Removed values are not reclaimed, they only get removed from the index. We could implement a compaction process for
 * this sometime (could be done async).
 */
@ThreadSafe
public class SequentialFileChunkStorage<V> implements IChunkStorage<V> {

    private final File memoryDirectory;
    private final List<File> memoryFiles;
    private final ISerde<V> valueSerde;
    private final IReadWriteLock lock = ILockCollectionFactory.getInstance(true)
            .newReadWriteLock(SequentialFileChunkStorage.class.getSimpleName() + "_lock");
    @GuardedBy("lock")
    private long precedingPosition;
    @GuardedBy("lock")
    private long position;
    private final ChunkStorageMetadata metadata;

    public SequentialFileChunkStorage(final File memoryDirectory, final ISerde<V> valueSerde) {
        this.memoryDirectory = memoryDirectory;
        this.memoryFiles = new ArrayList<>();
        this.valueSerde = valueSerde;
        this.metadata = new ChunkStorageMetadata(memoryDirectory);
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

    @Override
    public V get(final ChunkSummary summary) {
        lock.readLock().lock();
        try {
            final File file = new File(memoryDirectory, summary.getMemoryResourceUri());
            if (!file.exists()) {
                return null;
            }
            try (ICloseableByteBuffer buffer = ByteBuffers.EXPANDABLE_POOL.borrowObject()) {
                try (BufferedFileDataInputStream in = new BufferedFileDataInputStream(file)) {
                    in.position(summary.getMemoryOffset());
                    buffer.putBytesTo(0, (DataInput) in, Integers.checkedCast(summary.getMemoryLength()));
                    final V value = valueSerde.fromBuffer(buffer);
                    return value;
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
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
            Files.deleteQuietly(memoryDirectory);
            precedingPosition = 0;
            position = 0;
            memoryFiles.clear();
        } finally {
            lock.writeLock().unlock();
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
        final long precedingAddressOffset;
        final long addressOffset;
        lock.writeLock().lock();
        try {
            if (memoryFiles.isEmpty() || position > 0 && IMemoryMappedFile.isSegmentSizeExceeded(position + length)) {
                precedingPosition += position;
                position = 0;
                memoryFiles.add(nextMemoryFile());
            }
            //support parallel writes from this instance (we expect exclusive access to the file)
            precedingAddressOffset = precedingPosition;
            addressOffset = position;
            position += length;
        } finally {
            lock.writeLock().unlock();
        }
        lock.readLock().lock();
        try {
            if (!memoryDirectory.exists()) {
                Files.forceMkdir(memoryDirectory);
            }
            final int lastMemoryFileIndex = memoryFiles.size() - 1;
            final File lastMemoryFile = memoryFiles.get(lastMemoryFileIndex);
            try (BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(lastMemoryFile)) {
                out.seek(addressOffset);
                buffer.getBytesTo(0, (DataOutput) out, length);
                final ChunkSummary summary = new ChunkSummary(lastMemoryFile.getName(), precedingAddressOffset,
                        addressOffset, length);
                metadata.setSummary(summary);
                return summary;
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void close() {
        //noop
    }

}
