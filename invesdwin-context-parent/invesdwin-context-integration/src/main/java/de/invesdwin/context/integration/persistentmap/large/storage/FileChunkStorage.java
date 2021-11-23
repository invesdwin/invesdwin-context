package de.invesdwin.context.integration.persistentmap.large.storage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummary;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.pool.buffered.BufferedFileDataInputStream;
import de.invesdwin.util.streams.pool.buffered.BufferedFileDataOutputStream;

@ThreadSafe
public class FileChunkStorage<V> implements IChunkStorage<V> {

    private long fileIndex = System.currentTimeMillis();

    private final File memoryDirectory;
    private final ISerde<V> valueSerde;

    public FileChunkStorage(final File memoryDirectory, final ISerde<V> valueSerde) {
        this.memoryDirectory = memoryDirectory;
        this.valueSerde = valueSerde;
    }

    @Override
    public V get(final ChunkSummary summary) {
        final File file = newChunkFile(summary);
        if (!file.exists()) {
            return null;
        }
        final IByteBuffer buffer = ByteBuffers.EXPANDABLE_POOL.borrowObject();
        try (BufferedFileDataInputStream in = new BufferedFileDataInputStream(file)) {
            buffer.putBytes(0, (DataInput) in);
            final V value = valueSerde.fromBuffer(buffer, buffer.capacity());
            return value;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            ByteBuffers.EXPANDABLE_POOL.returnObject(buffer);
        }
    }

    private File newChunkFile(final ChunkSummary summary) {
        final String name = summary.getMemoryResourceUri();
        return newChunkFile(name);
    }

    private File newChunkFile(final String name) {
        return new File(memoryDirectory, name);
    }

    @Override
    public void remove(final ChunkSummary summary) {
        Files.deleteQuietly(newChunkFile(summary));
    }

    @Override
    public boolean isRemovable() {
        return true;
    }

    @Override
    public void clear() {
        Files.deleteNative(memoryDirectory);
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
        try {
            final File file = createNewFile();
            final BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(memoryDirectory);
            final long addressOffset = memoryDirectory.length();
            out.seek(addressOffset);
            buffer.getBytesTo(0, (DataOutput) out, length);
            return new ChunkSummary(file.getName(), addressOffset, length);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized File createNewFile() {
        while (true) {
            fileIndex++;
            final File file = new File(memoryDirectory, fileIndex + ".bin");
            if (file.exists()) {
                fileIndex += 100;
                continue;
            }
            return file;
        }
    }

    @Override
    public void close() throws IOException {
        //noop
    }

}
