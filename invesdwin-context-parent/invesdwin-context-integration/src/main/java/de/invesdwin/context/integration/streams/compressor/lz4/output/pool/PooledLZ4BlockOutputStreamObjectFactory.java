package de.invesdwin.context.integration.streams.compressor.lz4.output.pool;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.pool.IPoolableObjectFactory;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4FrameOutputStream.BLOCKSIZE;
import net.jpountz.xxhash.XXHash32;

@Immutable
public final class PooledLZ4BlockOutputStreamObjectFactory
        implements IPoolableObjectFactory<PooledLZ4BlockOutputStream> {

    private final PooledLZ4BlockOutputStreamObjectPool pool;
    private final BLOCKSIZE blockSize;
    private final LZ4Compressor compressor;
    private final XXHash32 checksum;

    public PooledLZ4BlockOutputStreamObjectFactory(final PooledLZ4BlockOutputStreamObjectPool pool,
            final BLOCKSIZE blockSize, final LZ4Compressor compressor, final XXHash32 checksum) {
        this.pool = pool;
        this.blockSize = blockSize;
        this.compressor = compressor;
        this.checksum = checksum;
    }

    @Override
    public PooledLZ4BlockOutputStream makeObject() {
        return new PooledLZ4BlockOutputStream(blockSize, compressor, checksum, pool);
    }

    @Override
    public void destroyObject(final PooledLZ4BlockOutputStream obj) {
    }

    @Override
    public boolean validateObject(final PooledLZ4BlockOutputStream obj) {
        return true;
    }

    @Override
    public void activateObject(final PooledLZ4BlockOutputStream obj) {
    }

    @Override
    public void passivateObject(final PooledLZ4BlockOutputStream obj) {

    }

}
