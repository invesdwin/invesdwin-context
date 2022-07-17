package de.invesdwin.context.integration.streams.compression.lz4.input.pool;

import java.util.function.Supplier;
import java.util.zip.Checksum;

import javax.annotation.concurrent.ThreadSafe;

import org.agrona.concurrent.ManyToManyConcurrentArrayQueue;

import de.invesdwin.util.concurrent.pool.AQueueObjectPool;
import net.jpountz.lz4.LZ4FastDecompressor;

@ThreadSafe
public final class PooledLZ4BlockInputStreamObjectPool extends AQueueObjectPool<PooledLZ4BlockInputStream> {

    private final LZ4FastDecompressor decompressor;
    private final Supplier<Checksum> checksumFactory;

    public PooledLZ4BlockInputStreamObjectPool(final LZ4FastDecompressor decompressor,
            final Supplier<Checksum> checksumFactory, final int maxPoolSize) {
        super(new ManyToManyConcurrentArrayQueue<PooledLZ4BlockInputStream>(maxPoolSize));
        this.decompressor = decompressor;
        this.checksumFactory = checksumFactory;
    }

    @Override
    protected PooledLZ4BlockInputStream newObject() {
        return new PooledLZ4BlockInputStream(decompressor, checksumFactory.get(), this);
    }

}
