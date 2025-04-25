package de.invesdwin.context.integration.persistentmap.large.storage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummary;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.ICloseableByteBuffer;
import de.invesdwin.util.streams.pool.buffered.BufferedFileDataInputStream;
import de.invesdwin.util.streams.pool.buffered.BufferedFileDataOutputStream;

/**
 * Values are deleted on the file system, so no problems with deletion.
 */
@ThreadSafe
public class ParallelFileChunkStorage<V> implements IChunkStorage<V> {

    private long fileIndex = System.currentTimeMillis();

    private final File memoryDirectory;
    private final ISerde<V> valueSerde;
    private final ChunkStorageMetadata metadata;

    public ParallelFileChunkStorage(final File memoryDirectory, final ISerde<V> valueSerde) {
        this.memoryDirectory = memoryDirectory;
        this.valueSerde = valueSerde;
        this.metadata = new ChunkStorageMetadata(memoryDirectory);
    }

    @Override
    public V get(final ChunkSummary summary) {
        final File file = newChunkFile(summary);
        if (!file.exists()) {
            return null;
        }
        try (ICloseableByteBuffer buffer = ByteBuffers.EXPANDABLE_POOL.borrowObject()) {
            try (BufferedFileDataInputStream in = new BufferedFileDataInputStream(file)) {
                buffer.putBytesTo(0, (DataInput) in, Integers.checkedCast(summary.getMemoryLength()));
                final V value = valueSerde.fromBuffer(buffer);
                return value;
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
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
        try (ICloseableByteBuffer buffer = ByteBuffers.EXPANDABLE_POOL.borrowObject()) {
            final int length = valueSerde.toBuffer(buffer, value);
            return write(buffer, length);
        }
    }

    private ChunkSummary write(final IByteBuffer buffer, final int length) {
        try {
            final File file = createNewFile();
            try (BufferedFileDataOutputStream out = new BufferedFileDataOutputStream(file)) {
                buffer.getBytesTo(0, (DataOutput) out, length);
                final ChunkSummary summary = new ChunkSummary(file.getName(), -1, 0, length);
                metadata.setSummary(summary);
                return summary;
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized File createNewFile() throws IOException {
        Files.forceMkdir(memoryDirectory);
        while (true) {
            fileIndex++;
            final File file = new File(memoryDirectory, fileIndex + ".bin");
            if (file.exists()) {
                fileIndex += 1000;
                continue;
            }
            Files.touchQuietly(file);
            return file;
        }
    }

    @Override
    public void close() {
        //noop
    }

}
