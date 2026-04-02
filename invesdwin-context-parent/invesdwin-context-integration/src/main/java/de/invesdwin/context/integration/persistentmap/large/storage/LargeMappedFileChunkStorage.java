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
import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummaryMemoryBuffer;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.marshallers.serde.large.ILargeSerde;
import de.invesdwin.util.marshallers.serde.large.ILargeSerdeLengthProvider;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.file.IMemoryMappedFile;
import de.invesdwin.util.streams.buffer.file.ListMemoryMappedFile;
import de.invesdwin.util.streams.buffer.memory.DataOutputDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.ICloseableMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;
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
public class LargeMappedFileChunkStorage<V> implements IChunkStorage<V> {

    private final File memoryDirectory;
    private final List<File> memoryFiles;
    private final ILargeSerde<V> valueSerde;
    private final ILargeSerdeLengthProvider<V> valueSerdeLengthProvider;
    private final IReadWriteLock lock = ILockCollectionFactory.getInstance(true)
            .newReadWriteLock(LargeMappedFileChunkStorage.class.getSimpleName() + "_lock");
    @GuardedBy("lock")
    private long precedingPosition;
    @GuardedBy("lock")
    private long position;
    private volatile IMemoryMappedFile reader;
    private final boolean readOnly;
    private final boolean closeAllowed;
    private final ChunkStorageMetadata metadata;

    private final Set<ChunkSummaryMemoryBuffer> readerBuffers;

    @SuppressWarnings("unchecked")
    public LargeMappedFileChunkStorage(final File memoryDirectory, final ILargeSerde<V> valueSerde,
            final boolean readOnly, final boolean closeAllowed) {
        this.memoryDirectory = memoryDirectory;
        this.memoryFiles = new ArrayList<>();
        this.valueSerde = valueSerde;
        if (valueSerde instanceof ILargeSerdeLengthProvider) {
            this.valueSerdeLengthProvider = (ILargeSerdeLengthProvider<V>) valueSerde;
        } else {
            this.valueSerdeLengthProvider = null;
        }
        this.readOnly = readOnly;
        this.closeAllowed = closeAllowed;
        this.metadata = new ChunkStorageMetadata(memoryDirectory);
        if (readOnly) {
            readerBuffers = null;
        } else {
            readerBuffers = Collections.newSetFromMap(
                    Caffeine.newBuilder().weakKeys().<ChunkSummaryMemoryBuffer, Boolean> build().asMap());
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
                synchronized (this) {
                    if (reader == null) {
                        if (memoryFiles.isEmpty()) {
                            return null;
                        } else if (memoryFiles.size() == 1) {
                            final File memoryFile = memoryFiles.get(0);
                            final long positionCopy = position;
                            try {
                                reader = IMemoryMappedFile.map(memoryFile, 0L, positionCopy, readOnly, closeAllowed);
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
                                    mappedFiles.add(IMemoryMappedFile.map(memoryFile, 0L, positionCopy, readOnly,
                                            closeAllowed));
                                } catch (final IOException e) {
                                    throw new RuntimeException("directory=" + memoryDirectory.getAbsolutePath()
                                            + " fileIndex=" + mappedFiles.size() + " precedingPosition="
                                            + precedingPositionCopy + " position=" + positionCopy, e);
                                }
                            }
                            return new ListMemoryMappedFile(closeAllowed, mappedFiles);
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
            final IMemoryBuffer buffer;
            if (readerBuffers != null) {
                final ChunkSummaryMemoryBuffer chunkBuffer = new ChunkSummaryMemoryBuffer(summary);
                chunkBuffer.init(reader);
                readerBuffers.add(chunkBuffer);
                buffer = chunkBuffer;
            } else {
                final long length = summary.getMemoryLength();
                final long memoryOffset = summary.getPrecedingMemoryOffset() + summary.getMemoryOffset();
                buffer = reader.newMemoryBuffer(memoryOffset, length);
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
            for (final ChunkSummaryMemoryBuffer readerBuffer : readerBuffers) {
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
            final long length = valueSerdeLengthProvider.getLength(value);
            return writeValue(value, length);
        } else {
            try (ICloseableMemoryBuffer buffer = MemoryBuffers.MAPPED_EXPANDABLE_POOL.borrowObject()) {
                final long length = valueSerde.toBuffer(buffer, value);
                return writeBuffer(buffer, length);
            }
        }
    }

    private ChunkSummary writeBuffer(final IMemoryBuffer buffer, final long length) {
        if (length < 0) {
            throw new IllegalArgumentException("length must be non-negative: " + length);
        }

        // Check if this single value exceeds segment size and needs to be split
        if (IMemoryMappedFile.isSegmentSizeExceeded(length)) {
            return writeLargeBufferSplitAcrossSegments(buffer, length);
        }

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
            if (readerBuffers != null) {
                //finalize reader asynchronously so that other threads can evict it properly using GC
                reader = null;
                if (!readerBuffers.isEmpty()) {
                    final IMemoryMappedFile newReader = getReader();
                    for (final ChunkSummaryMemoryBuffer readerBuffer : readerBuffers) {
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
            final int lastMemoryFileIndex = memoryFiles.size() - 1;
            final File lastMemoryFile = memoryFiles.get(lastMemoryFileIndex);
            try (BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(lastMemoryFile)) {
                out.seek(addressOffset);
                buffer.getBytesTo(0, (DataOutput) out, length);
                final ChunkSummary summary = new ChunkSummary(lastMemoryFile.getName(), precedingAddressOffset,
                        addressOffset, length);
                metadata.putSummary(summary);
                return summary;
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
        }
    }

    private ChunkSummary writeValue(final V value, final long length) {
        if (length < 0) {
            throw new IllegalArgumentException("length must be non-negative: " + length);
        }

        // Check if this single value exceeds segment size and needs to be split
        if (IMemoryMappedFile.isSegmentSizeExceeded(length)) {
            return writeLargeValueSplitAcrossSegments(value, length);
        }

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
            if (readerBuffers != null) {
                //finalize reader asynchronously so that other threads can evict it properly using GC
                reader = null;
                if (!readerBuffers.isEmpty()) {
                    final IMemoryMappedFile newReader = getReader();
                    for (final ChunkSummaryMemoryBuffer readerBuffer : readerBuffers) {
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
            final int lastMemoryFileIndex = memoryFiles.size() - 1;
            final File lastMemoryFile = memoryFiles.get(lastMemoryFileIndex);
            try (BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(lastMemoryFile)) {
                out.seek(addressOffset);
                final long bufferLength = valueSerde.toBuffer(new DataOutputDelegateMemoryBuffer(out), value);
                Assertions.checkEquals(length, bufferLength);
                final ChunkSummary summary = new ChunkSummary(lastMemoryFile.getName(), precedingAddressOffset,
                        addressOffset, length);
                metadata.putSummary(summary);
                return summary;
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
        }
    }

    private ChunkSummary writeLargeBufferSplitAcrossSegments(final IMemoryBuffer buffer, final long length) {
        lock.writeLock().lock();
        try {
            // Ensure we start with a fresh segment for large values
            if (position > 0) {
                precedingPosition += position;
                position = 0;
                memoryFiles.add(nextMemoryFile());
            }

            final long startPrecedingOffset = precedingPosition;
            final long startOffset = position;

            // Calculate how many segments we need
            final long maxSegmentSize = ListMemoryMappedFile.WINDOWS_MAX_LENGTH_PER_SEGMENT_MAPPED;
            long remainingLength = length;
            long bufferOffset = 0;
            int segmentCount = 0;

            while (remainingLength > 0) {
                final long segmentLength = Longs.min(remainingLength, maxSegmentSize);

                // Write this segment
                try {
                    writeBufferSegment(buffer, bufferOffset, segmentLength);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }

                remainingLength -= segmentLength;
                bufferOffset += segmentLength;
                segmentCount++;

                // Prepare next segment if needed
                if (remainingLength > 0) {
                    precedingPosition += position;
                    position = 0;
                    memoryFiles.add(nextMemoryFile());
                }
            }

            // Create a special ChunkSummary that indicates multi-segment storage
            // We use first file's name and total length
            final ChunkSummary summary = new ChunkSummary(memoryFiles.get(memoryFiles.size() - segmentCount).getName(),
                    startPrecedingOffset, startOffset, length);
            metadata.putSummary(summary);
            return summary;

        } finally {
            lock.writeLock().unlock();
        }
    }

    private void writeBufferSegment(final IMemoryBuffer buffer, final long bufferOffset, final long segmentLength)
            throws IOException {
        lock.readLock().lock();
        try {
            if (!memoryDirectory.exists()) {
                try {
                    Files.forceMkdir(memoryDirectory);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
            final int lastMemoryFileIndex = memoryFiles.size() - 1;
            final File lastMemoryFile = memoryFiles.get(lastMemoryFileIndex);
            try (BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(lastMemoryFile)) {
                out.seek(position);
                buffer.getBytesTo(bufferOffset, (DataOutput) out, segmentLength);
                position += segmentLength;
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    private ChunkSummary writeLargeValueSplitAcrossSegments(final V value, final long length) {
        // First serialize to a temporary buffer to get the bytes
        try (ICloseableMemoryBuffer tempBuffer = MemoryBuffers.MAPPED_EXPANDABLE_POOL.borrowObject()) {
            final long bufferLength = valueSerde.toBuffer(tempBuffer, value);
            Assertions.checkEquals(length, bufferLength);
            return writeLargeBufferSplitAcrossSegments(tempBuffer, length);
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
