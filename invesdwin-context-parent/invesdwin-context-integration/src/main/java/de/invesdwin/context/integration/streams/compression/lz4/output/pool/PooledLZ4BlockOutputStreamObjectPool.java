package de.invesdwin.context.integration.streams.compression.lz4.output.pool;

import java.util.function.Supplier;
import java.util.zip.Checksum;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;
import net.jpountz.lz4.LZ4Compressor;

@ThreadSafe
public final class PooledLZ4BlockOutputStreamObjectPool extends AAgronaObjectPool<PooledLZ4BlockOutputStream> {

    private final int blockSize;
    private final LZ4Compressor compressor;
    private final Supplier<Checksum> checksumFactory;

    public PooledLZ4BlockOutputStreamObjectPool(final int blockSize, final LZ4Compressor compressor,
            final Supplier<Checksum> checksumFactory, final int maxPoolSize) {
        super(maxPoolSize);
        this.blockSize = blockSize;
        this.compressor = compressor;
        this.checksumFactory = checksumFactory;
    }

    @Override
    protected PooledLZ4BlockOutputStream newObject() {
        return new PooledLZ4BlockOutputStream(blockSize, compressor, checksumFactory.get(), this);
    }

}
