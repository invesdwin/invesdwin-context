package de.invesdwin.context.integration.streams.compressor.lz4.output.pool;

import java.util.zip.Checksum;

import javax.annotation.concurrent.ThreadSafe;

import org.agrona.concurrent.ManyToManyConcurrentArrayQueue;

import de.invesdwin.util.concurrent.pool.AQueueObjectPool;
import net.jpountz.lz4.LZ4Compressor;

@ThreadSafe
public final class PooledLZ4BlockOutputStreamObjectPool extends AQueueObjectPool<PooledLZ4BlockOutputStream> {

    private final int blockSize;
    private final LZ4Compressor compressor;
    private final Checksum checksum;

    public PooledLZ4BlockOutputStreamObjectPool(final int blockSize, final LZ4Compressor compressor,
            final Checksum checksum, final int maxPoolSize) {
        super(new ManyToManyConcurrentArrayQueue<PooledLZ4BlockOutputStream>(maxPoolSize));
        this.blockSize = blockSize;
        this.compressor = compressor;
        this.checksum = checksum;
    }

    @Override
    protected PooledLZ4BlockOutputStream newObject() {
        return new PooledLZ4BlockOutputStream(blockSize, compressor, checksum, this);
    }

}
