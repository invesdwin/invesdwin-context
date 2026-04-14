package de.invesdwin.context.integration.persistentmap.large.storage;

import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummary;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.string.Charsets;
import de.invesdwin.util.marshallers.serde.large.ILargeSerde;
import de.invesdwin.util.marshallers.serde.large.ILargeSerdeLengthProvider;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.extend.MappedExpandableMemoryBuffer;
import net.openhft.chronicle.core.OS;

/**
 * Removed values are not reclaimed, they only get removed from the index. We could implement a compaction process for
 * this sometime (could be done async).
 * 
 * This implementation uses Chronicle's expandable MappedFile which handles Windows limitations internally. It is not
 * prone to files being smaller than the maximum chunk size.
 * 
 * TODO: Even with "mappedFile.retain=true", the overhead of
 * "net.openhft.chronicle.bytes.internal.ChunkedMappedFile.acquireByteStore(...)" is still too high when writing
 * individual values. This could be optimized by directly going against the stores instead of retrieving them before
 * each write inside MappedBytes via direct store indexing similar to SegmentedRoaringLargeBitSet.
 */
@ThreadSafe
public class ChronicleLargeMappedFileChunkStorage<V> implements IChunkStorage<V> {

    /**
     * 1 GB
     */
    public static final long DEFAULT_CHUNK_SIZE = 1024 * 1024 * 1024;

    private final File memoryDirectory;
    private final File memoryFile;
    private final File positionFile;
    private final ILargeSerde<V> valueSerde;
    private final ILargeSerdeLengthProvider<V> valueSerdeLengthProvider;
    private final IReadWriteLock lock = ILockCollectionFactory.getInstance(true)
            .newReadWriteLock(ChronicleLargeMappedFileChunkStorage.class.getSimpleName() + "_lock");
    @GuardedBy("lock")
    private long position;
    private volatile MappedExpandableMemoryBuffer mappedFile;
    private final boolean readOnly;
    private final boolean closeAllowed;
    private final ChunkStorageMetadata metadata;

    @SuppressWarnings("unchecked")
    public ChronicleLargeMappedFileChunkStorage(final File memoryDirectory, final ILargeSerde<V> valueSerde,
            final boolean readOnly, final boolean closeAllowed) {
        this.memoryDirectory = memoryDirectory;
        this.memoryFile = new File(memoryDirectory, "memory.bin");
        this.positionFile = new File(memoryDirectory, "memory.pos");
        this.valueSerde = valueSerde;
        if (valueSerde instanceof ILargeSerdeLengthProvider) {
            this.valueSerdeLengthProvider = (ILargeSerdeLengthProvider<V>) valueSerde;
        } else {
            this.valueSerdeLengthProvider = null;
        }
        this.readOnly = readOnly;
        this.closeAllowed = closeAllowed;
        this.metadata = new ChunkStorageMetadata(memoryDirectory);
        if (memoryFile.exists()) {
            position = readPosition();
        }
    }

    private long readPosition() {
        return Long.parseLong(Files.readFileToStringNoThrow(positionFile, Charsets.DEFAULT));
    }

    private void writePosition(final long position) {
        try {
            Files.writeStringToFile(positionFile, Long.toString(position), Charsets.DEFAULT);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MappedExpandableMemoryBuffer getMappedFile() {
        if (mappedFile == null) {
            lock.readLock().lock();
            try {
                synchronized (this) {
                    if (mappedFile == null) {
                        if (!memoryFile.getParentFile().exists()) {
                            Files.forceMkdir(memoryFile.getParentFile());
                        }
                        mappedFile = new MappedExpandableMemoryBuffer(newChunkSize(), memoryFile, false,
                                newOverlapSize(), readOnly, closeAllowed);
                    }
                }
            } catch (final IOException e) {
                throw new RuntimeException(e);
            } finally {
                lock.readLock().unlock();
            }
        }
        return mappedFile;
    }

    protected long newChunkSize() {
        return DEFAULT_CHUNK_SIZE;
    }

    protected long newOverlapSize() {
        return OS.pageSize();
    }

    @Override
    public V get(final ChunkSummary summary) {
        lock.readLock().lock();
        try {
            final MappedExpandableMemoryBuffer reader = getMappedFile();
            if (reader == null) {
                return null;
            }
            final long length = summary.getMemoryLength();
            final long memoryOffset = summary.getPrecedingMemoryOffset() + summary.getMemoryOffset();
            final IMemoryBuffer buffer = reader.newSlice(memoryOffset, length);
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
            closeMappedFileWriteLocked();
            Files.deleteQuietly(memoryDirectory);
            position = 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void closeMappedFileWriteLocked() {
        if (mappedFile != null) {
            mappedFile.close();
            mappedFile = null;
        }
    }

    @Override
    public ChunkSummary put(final V value) {
        if (valueSerdeLengthProvider != null) {
            final long length = valueSerdeLengthProvider.getLength(value);
            return writeValueNonBlocking(value, length);
        } else {
            return writeValueBlocking(value);
        }
    }

    private ChunkSummary writeValueBlocking(final V value) {
        final long addressOffset;
        final long length;
        lock.writeLock().lock();
        try {
            //we have to do sequential writes since we don't know the length up front
            addressOffset = position;
            final MappedExpandableMemoryBuffer mf = getMappedFile();
            length = valueSerde.toBuffer(mf.sliceFrom(addressOffset), value);
            position += length;
            writePosition(position);
        } finally {
            lock.writeLock().unlock();
        }

        lock.readLock().lock();
        try {
            final ChunkSummary summary = new ChunkSummary(memoryFile.getName(), 0L, addressOffset, length);
            metadata.putSummary(summary);
            return summary;
        } finally {
            lock.readLock().unlock();
        }
    }

    private ChunkSummary writeValueNonBlocking(final V value, final long length) {
        final long addressOffset;
        lock.writeLock().lock();
        try {
            //support parallel writes by directly jumping to the resulting position and writing afterwards
            addressOffset = position;
            position += length;
            writePosition(position);
        } finally {
            lock.writeLock().unlock();
        }

        lock.readLock().lock();
        try {
            final MappedExpandableMemoryBuffer mf = getMappedFile();
            final long writtenLength = valueSerde.toBuffer(mf.sliceFrom(addressOffset), value);
            Assertions.checkEquals(length, writtenLength);
            final ChunkSummary summary = new ChunkSummary(memoryFile.getName(), 0L, addressOffset, length);
            metadata.putSummary(summary);
            return summary;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void close() {
        lock.writeLock().lock();
        try {
            closeMappedFileWriteLocked();
        } finally {
            lock.writeLock().unlock();
        }
    }

}
