package de.invesdwin.context.integration.persistentmap.large.storage;

import java.io.DataOutput;
import java.io.File;
import java.io.IOException;

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
    private volatile MemoryMappedFile reader;
    private final IReadWriteLock lock = ILockCollectionFactory.getInstance(true)
            .newReadWriteLock(MappedChunkStorage.class.getSimpleName() + "_lock");

    public MappedChunkStorage(final File memoryDirectory, final ISerde<V> valueSerde) {
        this.memoryFile = new File(memoryDirectory.getAbsolutePath() + ".bin");
        this.valueSerde = valueSerde;
    }

    private MemoryMappedFile getReader() {
        if (reader == null) {
            if (!memoryFile.exists()) {
                return null;
            }
            try {
                reader = new MemoryMappedFile(memoryFile.getAbsolutePath(), memoryFile.length(), true);
            } catch (final IOException e) {
                throw new RuntimeException(e);
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
        lock.writeLock().lock();
        try {
            final boolean exists = memoryFile.exists();
            if (!exists) {
                Files.forceMkdirParent(memoryFile);
            }
            try (BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(memoryFile)) {
                final long addressOffset;
                if (exists) {
                    addressOffset = memoryFile.length();
                } else {
                    addressOffset = 0;
                }
                out.seek(addressOffset);
                buffer.getBytesTo(0, (DataOutput) out, length);
                reader = null;
                return new ChunkSummary("", addressOffset, length);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
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
