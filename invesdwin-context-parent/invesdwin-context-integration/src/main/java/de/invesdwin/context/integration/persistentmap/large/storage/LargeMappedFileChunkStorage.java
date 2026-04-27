package de.invesdwin.context.integration.persistentmap.large.storage;

import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.github.benmanes.caffeine.cache.Caffeine;

import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummary;
import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummaryMemoryBuffer;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.factory.pool.list.ICloseableList;
import de.invesdwin.util.collections.factory.pool.list.PooledArrayList;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.marshallers.serde.large.ILargeSerde;
import de.invesdwin.util.marshallers.serde.large.ILargeSerdeLengthProvider;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.InputStreams;
import de.invesdwin.util.streams.buffer.file.IMemoryMappedFile;
import de.invesdwin.util.streams.buffer.file.MemoryMappedFile;
import de.invesdwin.util.streams.buffer.file.SegmentedMemoryMappedFile;
import de.invesdwin.util.streams.buffer.memory.DataOutputDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ListMemoryBuffer;
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

    public static final long SEGMENT_SIZE = IMemoryMappedFile.MAX_SEGMENT_SIZE;

    private final AtomicLong tempFileIndex = new AtomicLong();
    private final File memoryDirectory;
    private final AtomicBoolean memoryDirectoryCreated = new AtomicBoolean();
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
            memoryDirectoryCreated.set(false);
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
            return writeValue(value);
        }
    }

    private void prepareSegmentsWriteLocked(final long length) {
        if (memoryFiles.isEmpty()) {
            prepareMemoryDirectory();
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
                for (final ChunkSummaryMemoryBuffer readerBuffer : readerBuffers) {
                    readerBuffer.init(newReader);
                }
            }
        } else {
            closeReaderWriteLocked();
        }
    }

    private void prepareMemoryDirectory() {
        if (!memoryDirectoryCreated.get() && !memoryDirectory.exists()) {
            try {
                Files.forceMkdir(memoryDirectory);
                memoryDirectoryCreated.set(true);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ChunkSummary writeValue(final V value, final long length) {
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

    private ChunkSummary writeValuePrepared(final V value, final long length, final long startPrecedingPosition,
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
                final long bufferLength = valueSerde.toBuffer(new DataOutputDelegateMemoryBuffer(out), value);
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

    private ChunkSummary writeValuePreparedSegmented(final V value, final long length,
            final long startPrecedingPosition, final long startFilePosition, final int startFileIndex) {
        // Write the data, potentially across multiple segments
        long bufferRemainingLength = length;
        int curFileIndex = startFileIndex;
        long curFilePosition = startFilePosition;

        lock.readLock().lock();
        try (ICloseableList<DataOutputDelegateMemoryBuffer> outs = PooledArrayList.getInstance()) {
            while (bufferRemainingLength > 0) {
                final long curFileRemainingLength = SEGMENT_SIZE - curFilePosition;
                final long segmentLength = Longs.min(bufferRemainingLength, curFileRemainingLength);

                final File curMemoryFile = memoryFiles.get(curFileIndex);
                try {
                    final BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(curMemoryFile);
                    out.seek(curFilePosition);
                    outs.add(new DataOutputDelegateMemoryBuffer(out, segmentLength));
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

            final long bufferLength = valueSerde.toBuffer(new ListMemoryBuffer(outs), value);
            Assertions.checkEquals(length, bufferLength);

            for (int i = 0; i < outs.size(); i++) {
                final DataOutputDelegateMemoryBuffer out = outs.get(i);
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
        /*
         * there is no way we can convert to a nioByteBuffer for LZ4 compression; if necessary at some point we should
         * port LZ4 compression/decompression to IByteBuffer/IMemoryBuffer instead of using nioByteBuffers. For now we
         * have to write to a temp file and then copy from there, which is not ideal but at least works for large
         * values.
         */
        //      Caused by - java.lang.UnsupportedOperationException: Cannot read from output stream
        //        * at de.invesdwin.util.streams.buffer.bytes.delegate.DataOutputDelegateByteBuffer.newUnsupportedOperationException(DataOutputDelegateByteBuffer.java:57) *
        //        * at de.invesdwin.util.streams.buffer.bytes.delegate.DataOutputDelegateByteBuffer.asNioByteBuffer(DataOutputDelegateByteBuffer.java:506) *
        //        * at de.invesdwin.util.streams.buffer.bytes.IByteBuffer.asNioByteBufferTo(IByteBuffer.java:307) *
        //        * at de.invesdwin.util.streams.buffer.bytes.IByteBuffer.asNioByteBuffer(IByteBuffer.java:299) *
        //        * at de.invesdwin.context.integration.compression.lz4.LZ4Streams.compress(LZ4Streams.java:191) *
        //        * at de.invesdwin.context.integration.compression.lz4.FastLZ4CompressionFactory.compress(FastLZ4CompressionFactory.java:41) *
        //        * at de.invesdwin.context.integration.compression.CompressionDelegateSerde.toBuffer(CompressionDelegateSerde.java:65) *
        //        * at de.invesdwin.context.integration.persistentmap.large.storage.MappedFileChunkStorage.writeValue(MappedFileChunkStorage.java:426) *
        //        * at de.invesdwin.context.integration.persistentmap.large.storage.MappedFileChunkStorage.put(MappedFileChunkStorage.java:250) *
        //        * at de.invesdwin.context.integration.persistentmap.large.ALargePersistentMap.put(ALargePersistentMap.java:182) *
        //        * at de.invesdwin.context.integration.persistentmap.APersistentMap.put(APersistentMap.java:511) *
        //        * at de.invesdwin.trading.optimization.report.map.internal.PersistedInternalOptimizationReportResultMap. (PersistedInternalOptimizationReportResultMap.java:75) *
        //        * at de.invesdwin.trading.optimization.report.map.internal.PersistedInternalOptimizationReportResultMap.maybeReplace(PersistedInternalOptimizationReportResultMap.java:166) *
        //        * at de.invesdwin.trading.optimization.report.map.StrategyOptimizationReportResultMap$OptimizationReportResultMapCompressingSoftReference.toCompressed(StrategyOptimizationReportResultMap.java:59) *
        //        * at de.invesdwin.trading.optimization.report.map.StrategyOptimizationReportResultMap$OptimizationReportResultMapCompressingSoftReference.toCompressed(StrategyOptimizationReportResultMap.java:1) *
        //LZ4 compression does not work with DataOutputDelegateByteBuffer because it can not be converted into a nioByteBuffer
        prepareMemoryDirectory();
        final File tempFile = new File(memoryDirectory, "temp_" + tempFileIndex.incrementAndGet() + ".part");
        final long bufferLength;
        try (BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(tempFile)) {
            bufferLength = valueSerde.toBuffer(new DataOutputDelegateMemoryBuffer(out), value);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        final ChunkSummary result = writeFile(tempFile, bufferLength);
        Files.deleteQuietly(tempFile);
        return result;
        //memory mapped file though also does not work for large values, because it might consist of multiple segments on windows which can not be converted to a single nioByteBuffer
        //        try (ICloseableMemoryBuffer buffer = MemoryBuffers.MAPPED_EXPANDABLE_POOL.borrowObject()) {
        //            final long length = valueSerde.toBuffer(buffer, value);
        //            return writeBuffer(buffer, length);
        //        }
    }

    private ChunkSummary writeFile(final File file, final long length) {
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

    private ChunkSummary writeFilePrepared(final File file, final long length, final long startPrecedingPosition,
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

    private ChunkSummary writeBuffer(final IMemoryBuffer buffer, final long length) {
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

        return writeBufferPrepared(buffer, length, startPrecedingPosition, startFilePosition, startFileIndex);
    }

    private ChunkSummary writeBufferPrepared(final IMemoryBuffer buffer, final long length,
            final long startPrecedingPosition, final long startFilePosition, final int startFileIndex) {
        // Write the data, potentially across multiple segments
        long bufferRemainingLength = length;
        long bufferPosition = 0;
        int curFileIndex = startFileIndex;
        long curFilePosition = startFilePosition;

        lock.readLock().lock();
        try {
            while (bufferRemainingLength > 0) {
                final long curFileRemainingLength = SEGMENT_SIZE - curFilePosition;
                final long segmentLength = Longs.min(bufferRemainingLength, curFileRemainingLength);

                final File curMemoryFile = memoryFiles.get(curFileIndex);
                try (BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(curMemoryFile)) {
                    out.seek(curFilePosition);
                    buffer.getBytesTo(bufferPosition, (DataOutput) out, segmentLength);
                }

                bufferRemainingLength -= segmentLength;
                bufferPosition += segmentLength;

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
