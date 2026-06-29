package de.invesdwin.context.integration.persistentmap.large.storage;

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
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.file.ClosedMemoryMappedFile;
import de.invesdwin.util.streams.buffer.file.IMemoryMappedFile;
import de.invesdwin.util.streams.buffer.file.MemoryMappedFile;
import de.invesdwin.util.streams.buffer.file.SegmentedMemoryMappedFile;
import de.invesdwin.util.streams.buffer.memory.DataOutputDelegateMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.delegate.ListMemoryBuffer;
import de.invesdwin.util.streams.pool.buffered.BufferedFileDataOutputStream;

/**
 * Avoids resizing memory mapped files on windows which can cause errors like "The requested operation cannot be
 * performed on a file with a user-mapped section open".
 */
@ThreadSafe
public class PreallocatedLargeSegmentedMappedFileChunkStorage<V> implements IChunkStorage<V> {

    public static final long SEGMENT_SIZE = IMemoryMappedFile.MAX_SEGMENT_SIZE_WINDOWS;

    private final AtomicLong tempFileIndex = new AtomicLong();
    private final File memoryDirectory;
    private final AtomicBoolean memoryDirectoryCreated = new AtomicBoolean();
    private final List<File> memoryFiles;
    private final List<IMemoryMappedFile> memoryMappedFiles = new ArrayList<>();
    private final ILargeSerde<V> valueSerde;
    private final ILargeSerdeLengthProvider<V> valueSerdeLengthProvider;
    private final IReadWriteLock lock = ILockCollectionFactory.getInstance(true)
            .newReadWriteLock(PreallocatedLargeSegmentedMappedFileChunkStorage.class.getSimpleName() + "_lock");

    @GuardedBy("lock")
    private long precedingPosition;
    @GuardedBy("lock")
    private long position;

    // Exact Logical Size Tracker
    private IMemoryMappedFile positionMappedFile;
    private IByteBuffer positionBuffer;

    private volatile IMemoryMappedFile reader;
    private final boolean readOnly;
    private final boolean closeAllowed;
    private final ChunkStorageMetadata metadata;

    private final Set<ChunkSummaryMemoryBuffer> readerBuffers;

    @SuppressWarnings("unchecked")
    public PreallocatedLargeSegmentedMappedFileChunkStorage(final File memoryDirectory, final ILargeSerde<V> valueSerde,
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
        if (!memoryDirectory.exists()) {
            return;
        }
        prepareMemoryDirectory();

        final long totalLogicalPosition = positionBuffer.getLong(0);
        precedingPosition = (totalLogicalPosition / SEGMENT_SIZE) * SEGMENT_SIZE;
        position = totalLogicalPosition % SEGMENT_SIZE;

        final int numFilesRequired = (int) (totalLogicalPosition / SEGMENT_SIZE) + 1;
        for (int i = 0; i < numFilesRequired; i++) {
            final File f = new File(memoryDirectory, newMemoryFileName(i));
            if (!f.exists() && !readOnly) {
                Files.setLength(f, SEGMENT_SIZE);
            }
            if (f.exists()) {
                memoryFiles.add(f);
            }
        }
    }

    public File nextMemoryFile() {
        final int nextIndex = memoryFiles.size();
        final File targetFile = new File(memoryDirectory, newMemoryFileName(nextIndex));
        if (!readOnly) {
            Files.setLength(targetFile, SEGMENT_SIZE);
        }
        return targetFile;
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
                            reader = getMemoryMappedFile(0);
                        } else {
                            final List<IMemoryMappedFile> mappedFiles = new ArrayList<>();
                            final int lastMemoryFileIndex = memoryFiles.size() - 1;
                            for (int i = 0; i <= lastMemoryFileIndex; i++) {
                                mappedFiles.add(getMemoryMappedFile(i));
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

    private IMemoryMappedFile getMemoryMappedFile(final int index) {
        // Maps the full 4GB capacity so writer can advance without invalidating reader
        final File memoryFile = memoryFiles.get(index);
        try {
            for (int i = memoryMappedFiles.size(); i <= index; i++) {
                memoryMappedFiles
                        .add(IMemoryMappedFile.map(closeAllowed, memoryFile, 0L, SEGMENT_SIZE, readOnly, false));
            }
            return memoryMappedFiles.get(index);
        } catch (final IOException e) {
            throw new RuntimeException("directory=" + memoryDirectory.getAbsolutePath() + " fileIndex=0", e);
        }
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
            return valueSerde.fromBuffer(buffer);
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
            positionBuffer.putLong(0, 0L);
            memoryFiles.clear();
            memoryMappedFiles.clear();
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

        boolean segmentAdded = false;
        long remainingLength = length;
        while (remainingLength > 0) {
            final long segmentRemainingLength = SEGMENT_SIZE - position;
            final long segmentLength = Longs.min(remainingLength, segmentRemainingLength);
            position += segmentLength;
            remainingLength -= segmentLength;

            // Roll to next segment if needed
            if (remainingLength > 0) {
                precedingPosition += position;
                position = 0;
                memoryFiles.add(nextMemoryFile());
                segmentAdded = true;
            }
        }

        // Persist exact logical byte counter mapped to disk natively
        positionBuffer.putLong(0, precedingPosition + position);

        // We ONLY invalidate the reader if a new file physically joined the array.
        // Because mappings are SEGMENT_SIZE natively, we do not need to refresh readers
        // for regular writes! This provides a massive lock/unmap performance gain.
        if (segmentAdded) {
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
    }

    private void prepareMemoryDirectory() {
        if (!memoryDirectoryCreated.get() && !memoryDirectory.exists()) {
            try {
                Files.forceMkdir(memoryDirectory);
                memoryDirectoryCreated.set(true);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            if (positionMappedFile != null) {
                positionMappedFile.close();
                positionMappedFile = null;
                positionBuffer = null;
            }
        }
        if (positionMappedFile == null) {
            this.positionMappedFile = newPositionMappedFile();
            this.positionBuffer = positionMappedFile.newByteBuffer(0, Long.BYTES);
        }
    }

    private IMemoryMappedFile newPositionMappedFile() {
        final File positionFile = new File(memoryDirectory, "memory.pos");
        if (!readOnly || positionFile.exists()) {
            try {
                return MemoryMappedFile.map(closeAllowed, positionFile, 0, Long.BYTES, readOnly, true);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return ClosedMemoryMappedFile.INSTANCE;
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

        final long startFileRemainingLength = SEGMENT_SIZE - startFilePosition;

        if (length >= startFileRemainingLength) {
            return writeValuePreparedSegmented(value, length, startPrecedingPosition, startFilePosition,
                    startFileIndex);
        }

        lock.readLock().lock();
        try {
            final File curMemoryFile = memoryFiles.get(startFileIndex);
            try (BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(curMemoryFile)) {
                out.seek(startFilePosition);
                final long bufferLength = valueSerde.toBuffer(new DataOutputDelegateMemoryBuffer(out), value);
                Assertions.checkEquals(length, bufferLength);
            }

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

                if (bufferRemainingLength > 0) {
                    curFilePosition = 0;
                    curFileIndex++;
                }
            }

            final long bufferLength = valueSerde.toBuffer(new ListMemoryBuffer(outs), value);
            Assertions.checkEquals(length, bufferLength);

            for (int i = 0; i < outs.size(); i++) {
                outs.get(i).close();
            }

            final ChunkSummary summary = new ChunkSummary(memoryFiles.get(startFileIndex).getName(),
                    startPrecedingPosition, startFilePosition, length);
            metadata.putSummary(summary);
            return summary;
        } finally {
            lock.readLock().unlock();
        }
    }

    private ChunkSummary writeValue(final V value) {
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

                if (bufferRemainingLength > 0) {
                    curFilePosition = 0;
                    curFileIndex++;
                }
            }

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
            positionMappedFile.close();
            clearReaderBuffers();
            closeReaderWriteLocked();
            memoryMappedFiles.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
}