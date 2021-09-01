package de.invesdwin.context.integration.streams.compressor.lz4.input.pool;

import java.util.zip.Checksum;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.pool.IPoolableObjectFactory;
import net.jpountz.lz4.LZ4FastDecompressor;

@Immutable
public final class PooledLZ4BlockInputStreamObjectFactory implements IPoolableObjectFactory<PooledLZ4BlockInputStream> {

    private final PooledLZ4BlockInputStreamObjectPool pool;
    private final LZ4FastDecompressor decompressor;
    private final Checksum checksum;

    public PooledLZ4BlockInputStreamObjectFactory(final PooledLZ4BlockInputStreamObjectPool pool,
            final LZ4FastDecompressor decompressor, final Checksum checksum) {
        this.pool = pool;
        this.decompressor = decompressor;
        this.checksum = checksum;
    }

    @Override
    public PooledLZ4BlockInputStream makeObject() {
        return new PooledLZ4BlockInputStream(decompressor, checksum, pool);
    }

    @Override
    public void destroyObject(final PooledLZ4BlockInputStream obj) {
    }

    @Override
    public boolean validateObject(final PooledLZ4BlockInputStream obj) {
        return true;
    }

    @Override
    public void activateObject(final PooledLZ4BlockInputStream obj) {
    }

    @Override
    public void passivateObject(final PooledLZ4BlockInputStream obj) {

    }

}
