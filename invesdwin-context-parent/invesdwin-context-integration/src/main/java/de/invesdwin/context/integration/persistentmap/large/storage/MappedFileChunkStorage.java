package de.invesdwin.context.integration.persistentmap.large.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.Caffeine;

import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummary;
import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummaryByteBuffer;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.factory.pool.list.ICloseableList;
import de.invesdwin.util.collections.factory.pool.list.PooledArrayList;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.ISerdeLengthProvider;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.DataOutputDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.delegate.ListByteBuffer;
import de.invesdwin.util.streams.buffer.file.IMemoryMappedFile;
import de.invesdwin.util.streams.buffer.file.MemoryMappedFile;
import de.invesdwin.util.streams.buffer.file.SegmentedMemoryMappedFile;
import de.invesdwin.util.streams.pool.buffered.BufferedFileDataOutputStream;

/**
 * Removed values are not reclaimed, they only get removed from the index. We could implement a compaction process for
 * this sometime (could be done async).
 * 
 * Files can be smaller than the configured segment size, but never larger. If a value exceeds the segment size, it gets
 * split across multiple files and the summary contains the total length and the name of the first file. This allows us
 * to support values larger than the segment size.
 */
@ThreadSafe
public class MappedFileChunkStorage<V> implements IChunkStorage<V> {

    private static final int SEGMENT_SIZE = Integers
            .checkedCast(Longs.min(LargeMappedFileChunkStorage.SEGMENT_SIZE, Integer.MAX_VALUE));
    private final AtomicLong tempFileIndex = new AtomicLong();
    private final File memoryDirectory;
    private final List<File> memoryFiles;
    private final ISerde<V> valueSerde;
    private final ISerdeLengthProvider<V> valueSerdeLengthProvider;
    private final IReadWriteLock lock = ILockCollectionFactory.getInstance(true)
            .newReadWriteLock(MappedFileChunkStorage.class.getSimpleName() + "_lock");
    @GuardedBy("lock")
    private long precedingPosition;
    @GuardedBy("lock")
    private long position;
    private volatile IMemoryMappedFile reader;
    private final boolean readOnly;
    private final boolean closeAllowed;
    private final ChunkStorageMetadata metadata;

    private final Set<ChunkSummaryByteBuffer> readerBuffers;

    @SuppressWarnings("unchecked")
    public MappedFileChunkStorage(final File memoryDirectory, final ISerde<V> valueSerde, final boolean readOnly,
            final boolean closeAllowed) {
        this.memoryDirectory = memoryDirectory;
        this.memoryFiles = new ArrayList<>();
        this.valueSerde = valueSerde;
        if (valueSerde instanceof ISerdeLengthProvider) {
            this.valueSerdeLengthProvider = (ISerdeLengthProvider<V>) valueSerde;
        } else {
            this.valueSerdeLengthProvider = null;
        }
        this.readOnly = readOnly;
        this.closeAllowed = closeAllowed;
        this.metadata = new ChunkStorageMetadata(memoryDirectory);
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
        final int nextIndex = memoryFiles.size();
        return newMemoryFile(nextIndex);
    }

    private File newMemoryFile(final int index) {
        return new File(memoryDirectory, newMemoryFileName(index));
    }

    private String newMemoryFileName(final int index) {
        return "memory_" + index + ".bin";
    }

    private IMemoryMappedFile getReader() {
        if (reader == null) {
            if (memoryFiles.isEmpty()) {
                return null;
            }
            lock.readLock().lock();
            try {
                synchronized (this) {
                    if (reader == null) {
                        if (memoryFiles.isEmpty()) {
                            return null;
                        } else if (memoryFiles.size() == 1) {
                            final File memoryFile = memoryFiles.get(0);
                            final long positionCopy = position;
                            try {
                                reader = new MemoryMappedFile(closeAllowed, memoryFile, 0L, positionCopy, readOnly,
                                        false);
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
                                    mappedFiles.add(new MemoryMappedFile(closeAllowed, memoryFile, 0L, positionCopy,
                                            readOnly, false));
                                } catch (final IOException e) {
                                    throw new RuntimeException("directory=" + memoryDirectory.getAbsolutePath()
                                            + " fileIndex=" + mappedFiles.size() + " precedingPosition="
                                            + precedingPositionCopy + " position=" + positionCopy, e);
                                }
                            }
                            return new SegmentedMemoryMappedFile(SEGMENT_SIZE, closeAllowed, mappedFiles);
                        }
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
                final int length = ByteBuffers.checkedCast(summary.getMemoryLength());
                final long memoryOffset = summary.getPrecedingMemoryOffset() + summary.getMemoryOffset();
                buffer = reader.newByteBuffer(memoryOffset, length);
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
        if (valueSerdeLengthProvider != null) {
            final int length = valueSerdeLengthProvider.getLength(value);
            return writeValue(value, length);
        } else {
            return writeValue(value);
        }
    }

    private void prepareSegmentsWriteLocked(final long length) {
        if (memoryFiles.isEmpty()) {
            if (!memoryDirectory.exists()) {
                try {
                    Files.forceMkdir(memoryDirectory);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
            memoryFiles.add(nextMemoryFile());
        }

        long remainingLength = length;
        while (remainingLength > 0) {
            final long segmentRemainingLength = SEGMENT_SIZE - position;
            final long segmentLength = Longs.min(remainingLength, segmentRemainingLength);
            position += segmentLength;
            remainingLength -= segmentLength;

            // Prepare next segment if needed
            if (remainingLength > 0) {
                final File lastFile = memoryFiles.get(memoryFiles.size() - 1);
                if (lastFile.length() < position) {
                    //preallocate size so that mmap reader has correct capacity later
                    Files.setLength(lastFile, position);
                }
                precedingPosition += position;
                position = 0;
                memoryFiles.add(nextMemoryFile());
            }
        }
        if (position > 0) {
            final File lastFile = memoryFiles.get(memoryFiles.size() - 1);
            if (lastFile.length() < position) {
                //preallocate size so that mmap reader has correct capacity later
                Files.setLength(lastFile, position);
            }
        }

        if (readerBuffers != null) {
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
    }

    private ChunkSummary writeValue(final V value, final int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length must be non-negative: " + length);
        }

        long startPrecedingPosition;
        long startFilePosition;
        int startFileIndex;

        lock.writeLock().lock();
        try {
            startPrecedingPosition = precedingPosition;
            startFilePosition = position;
            startFileIndex = Integers.max(0, memoryFiles.size() - 1);
            final long startFileRemainingLength = SEGMENT_SIZE - startFilePosition;
            if (startFileRemainingLength <= 0) {
                //directly roll over to next segment, since last one is full
                startPrecedingPosition += startFilePosition;
                startFilePosition = 0;
                startFileIndex++;
            }
            prepareSegmentsWriteLocked(length);
        } finally {
            lock.writeLock().unlock();
        }

        return writeValuePrepared(value, length, startPrecedingPosition, startFilePosition, startFileIndex);
    }

    private ChunkSummary writeValuePrepared(final V value, final int length, final long startPrecedingPosition,
            final long startFilePosition, final int startFileIndex) {
        // Write the data, potentially across multiple segments
        final long startFileRemainingLength = SEGMENT_SIZE - startFilePosition;

        if (length >= startFileRemainingLength) {
            return writeValuePreparedSegmented(value, length, startPrecedingPosition, startFilePosition,
                    startFileIndex);
        }

        //zero-copy path is available
        lock.readLock().lock();
        try {
            final File curMemoryFile = memoryFiles.get(startFileIndex);
            try (BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(curMemoryFile)) {
                out.seek(startFilePosition);
                final long bufferLength = valueSerde.toBuffer(new DataOutputDelegateByteBuffer(out), value);
                Assertions.checkEquals(length, bufferLength);
            }

            // Create ChunkSummary using the first segment's info
            final ChunkSummary summary = new ChunkSummary(memoryFiles.get(startFileIndex).getName(),
                    startPrecedingPosition, startFilePosition, length);
            metadata.putSummary(summary);
            return summary;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
        }
    }

    private ChunkSummary writeValuePreparedSegmented(final V value, final int length, final long startPrecedingPosition,
            final long startFilePosition, final int startFileIndex) {
        // Write the data, potentially across multiple segments
        int bufferRemainingLength = length;
        int curFileIndex = startFileIndex;
        long curFilePosition = startFilePosition;

        lock.readLock().lock();
        try (ICloseableList<DataOutputDelegateByteBuffer> outs = PooledArrayList.getInstance()) {
            while (bufferRemainingLength > 0) {
                final int curFileRemainingLength = ByteBuffers.checkedCast(SEGMENT_SIZE - curFilePosition);
                final int segmentLength = Integers.min(bufferRemainingLength, curFileRemainingLength);

                final File curMemoryFile = memoryFiles.get(curFileIndex);
                try {
                    final BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(curMemoryFile);
                    out.seek(curFilePosition);
                    outs.add(new DataOutputDelegateByteBuffer(out, segmentLength));
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }

                bufferRemainingLength -= segmentLength;

                //move to next segment if needed
                if (bufferRemainingLength > 0) {
                    curFilePosition = 0;
                    curFileIndex++;
                }
            }

            final long bufferLength = valueSerde.toBuffer(new ListByteBuffer(outs), value);
            Assertions.checkEquals(length, bufferLength);

            for (int i = 0; i < outs.size(); i++) {
                final DataOutputDelegateByteBuffer out = outs.get(i);
                out.close();
            }

            // Create ChunkSummary using the first segment's info
            final ChunkSummary summary = new ChunkSummary(memoryFiles.get(startFileIndex).getName(),
                    startPrecedingPosition, startFilePosition, length);
            metadata.putSummary(summary);
            return summary;
        } finally {
            lock.readLock().unlock();
        }
    }

    private ChunkSummary writeValue(final V value) {
        final File tempFile = new File(memoryDirectory, "temp_" + tempFileIndex.incrementAndGet() + ".part");
        final int bufferLength;
        try (BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(tempFile)) {
            bufferLength = valueSerde.toBuffer(new DataOutputDelegateByteBuffer(out), value);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        final ChunkSummary result = writeFile(tempFile, bufferLength);
        Files.deleteQuietly(tempFile);
        return result;
    }

    private ChunkSummary writeFile(final File file, final int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length must be non-negative: " + length);
        }

        long startPrecedingPosition;
        long startFilePosition;
        int startFileIndex;

        lock.writeLock().lock();
        try {
            startPrecedingPosition = precedingPosition;
            startFilePosition = position;
            startFileIndex = Integers.max(0, memoryFiles.size() - 1);
            final long startFileRemainingLength = SEGMENT_SIZE - startFilePosition;
            if (startFileRemainingLength <= 0) {
                //directly roll over to next segment, since last one is full
                startPrecedingPosition += startFilePosition;
                startFilePosition = 0;
                startFileIndex++;
            }
            prepareSegmentsWriteLocked(length);
        } finally {
            lock.writeLock().unlock();
        }

        return writeFilePrepared(file, length, startPrecedingPosition, startFilePosition, startFileIndex);
    }

    private ChunkSummary writeFilePrepared(final File file, final int length, final long startPrecedingPosition,
            final long startFilePosition, final int startFileIndex) {
        // Write the data, potentially across multiple segments
        long bufferRemainingLength = length;
        int curFileIndex = startFileIndex;
        long curFilePosition = startFilePosition;

        lock.readLock().lock();
        try (FileInputStream fis = new FileInputStream(file)) {
            while (bufferRemainingLength > 0) {
                final long curFileRemainingLength = SEGMENT_SIZE - curFilePosition;
                final long segmentLength = Longs.min(bufferRemainingLength, curFileRemainingLength);

                final File curMemoryFile = memoryFiles.get(curFileIndex);
                try (BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(curMemoryFile)) {
                    out.seek(curFilePosition);
                    InputStreams.copyFullyNoTimeout(fis, out, segmentLength);
                }

                bufferRemainingLength -= segmentLength;

                //move to next segment if needed
                if (bufferRemainingLength > 0) {
                    curFilePosition = 0;
                    curFileIndex++;
                }
            }

            // Create ChunkSummary using the first segment's info
            final ChunkSummary summary = new ChunkSummary(memoryFiles.get(startFileIndex).getName(),
                    startPrecedingPosition, startFilePosition, length);
            metadata.putSummary(summary);
            return summary;
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
