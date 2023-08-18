package de.invesdwin.context.integration.persistentmap.large.storage;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
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
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.buffer.file.IMemoryMappedFile;
import de.invesdwin.util.streams.pool.buffered.BufferedFileDataOutputStream;

/**
 * Removed values are not reclaimed, they only get removed from the index. We could implement a compaction process for
 * this sometime (could be done async).
 */
@ThreadSafe
public class MappedChunkStorage<V> implements IChunkStorage<V> {

    private final File memoryFile;
    private final ISerde<V> valueSerde;
    private final IReadWriteLock lock = ILockCollectionFactory.getInstance(true)
            .newReadWriteLock(MappedChunkStorage.class.getSimpleName() + "_lock");
    @GuardedBy("lock")
    private long position;
    private volatile IMemoryMappedFile reader;
    private final boolean readOnly;

    private final Set<ChunkSummaryByteBuffer> readerBuffers = Collections
            .newSetFromMap(Caffeine.newBuilder().weakKeys().<ChunkSummaryByteBuffer, Boolean> build().asMap());

    public MappedChunkStorage(final File memoryDirectory, final ISerde<V> valueSerde, final boolean readOnly) {
        this.memoryFile = new File(memoryDirectory.getAbsolutePath() + ".bin");
        if (memoryFile.exists()) {
            position = memoryFile.length();
        } else {
            position = 0;
        }
        this.valueSerde = valueSerde;
        this.readOnly = readOnly;
    }

    private IMemoryMappedFile getReader() {
        if (reader == null) {
            if (!memoryFile.exists()) {
                return null;
            }
            lock.readLock().lock();
            try {
                if (reader == null) {
                    if (!memoryFile.exists()) {
                        return null;
                    }
                    final long positionCopy = position;
                    try {
                        reader = IMemoryMappedFile.map(memoryFile.getAbsolutePath(), 0L, positionCopy, readOnly);
                    } catch (final IOException e) {
                        throw new RuntimeException("file=" + memoryFile.getAbsolutePath() + " position=" + positionCopy,
                                e);
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
            final ChunkSummaryByteBuffer buffer = new ChunkSummaryByteBuffer(summary);
            buffer.init(reader);
            readerBuffers.add(buffer);
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
            Files.deleteQuietly(memoryFile);
            position = 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void clearReaderBuffers() {
        if (!readerBuffers.isEmpty()) {
            for (final ChunkSummaryByteBuffer readerBuffer : readerBuffers) {
                readerBuffer.close();
            }
        }
        readerBuffers.clear();
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
            //support parallel writes from this instance (we expect exclusive access to the file)
            addressOffset = position;
            position += length;
            closeReaderWriteLocked();
            if (!readerBuffers.isEmpty()) {
                final IMemoryMappedFile newReader = getReader();
                for (final ChunkSummaryByteBuffer readerBuffer : readerBuffers) {
                    readerBuffer.init(newReader);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
        lock.readLock().lock();
        try {
            final boolean exists = memoryFile.exists();
            if (!exists) {
                Files.forceMkdirParent(memoryFile);
            }
            try (BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(memoryFile)) {
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
