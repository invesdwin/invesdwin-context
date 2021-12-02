package de.invesdwin.context.integration.persistentmap.large.storage;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummary;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.MemoryMappedFile;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.pool.buffered.BufferedFileDataOutputStream;

@ThreadSafe
public class MappedChunkStorage<V> implements IChunkStorage<V> {

    private final File memoryFile;
    private final ISerde<V> valueSerde;
    private final IReadWriteLock lock = ILockCollectionFactory.getInstance(true)
            .newReadWriteLock(MappedChunkStorage.class.getSimpleName() + "_lock");
    @GuardedBy("lock")
    private long position;
    private volatile MemoryMappedFile reader;

    public MappedChunkStorage(final File memoryDirectory, final ISerde<V> valueSerde) {
        this.memoryFile = new File(memoryDirectory.getAbsolutePath() + ".bin");
        if (memoryFile.exists()) {
            position = memoryFile.length();
        } else {
            position = 0;
        }
        this.valueSerde = valueSerde;
    }

    private MemoryMappedFile getReader() {
        if (reader == null) {
            if (!memoryFile.exists()) {
                return null;
            }
            synchronized (this) {
                if (reader == null) {
                    if (!memoryFile.exists()) {
                        return null;
                    }
                    final long positionCopy = position;
                    try {
                        reader = new MemoryMappedFile(memoryFile.getAbsolutePath(), positionCopy, true);
                    } catch (final IOException e) {
                        throw new RuntimeException("file=" + memoryFile.getAbsolutePath() + " position=" + positionCopy,
                                e);
                    }
                }
            }
        }
        return reader;
    }

    @Override
    public V get(final ChunkSummary summary) {
        lock.readLock().lock();
        try {
            final MemoryMappedFile reader = getReader();
            if (reader == null) {
                return null;
            }
            final IByteBuffer buffer = summary.newBuffer(reader);
            final V value = valueSerde.fromBuffer(buffer, buffer.capacity());
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
            reader = null;
            Files.deleteQuietly(memoryFile);
            position = 0;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public ChunkSummary put(final V value) {
        final IByteBuffer buffer = ByteBuffers.EXPANDABLE_POOL.borrowObject();
        final int length = valueSerde.toBuffer(buffer, value);
        try {
            return write(buffer, length);
        } finally {
            ByteBuffers.EXPANDABLE_POOL.returnObject(buffer);
        }
    }

    private ChunkSummary write(final IByteBuffer buffer, final int length) {
        final long addressOffset;
        lock.writeLock().lock();
        try {
            //support parallel writes from this instance (we expect exclusive access to the file)
            addressOffset = position;
            position += buffer.capacity();
            if (reader != null) {
                reader.close();
                reader = null;
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
            if (reader != null) {
                reader.close();
                reader = null;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

}
