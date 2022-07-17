package de.invesdwin.context.integration.streams.compression.lz4.input.pool;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Checksum;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.streams.compression.lz4.input.ReusableLZ4BlockInputStream;
import de.invesdwin.util.concurrent.pool.IObjectPool;
import net.jpountz.lz4.LZ4FastDecompressor;

@NotThreadSafe
public class PooledLZ4BlockInputStream extends ReusableLZ4BlockInputStream {

    private final IObjectPool<PooledLZ4BlockInputStream> pool;

    public PooledLZ4BlockInputStream(final LZ4FastDecompressor decompressor, final Checksum checksum,
            final IObjectPool<PooledLZ4BlockInputStream> pool) {
        super(decompressor, checksum);
        this.pool = pool;
    }

    @Override
    public PooledLZ4BlockInputStream init(final InputStream in) {
        super.init(in);
        return this;
    }

    @Override
    public void close() throws IOException {
        if (!isClosed()) {
            super.close();
            pool.returnObject(this);
        }
    }

}
