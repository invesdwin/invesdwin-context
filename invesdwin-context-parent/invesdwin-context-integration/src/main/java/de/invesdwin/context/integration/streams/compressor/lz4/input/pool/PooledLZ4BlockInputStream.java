package de.invesdwin.context.integration.streams.compressor.lz4.input.pool;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.streams.compressor.lz4.input.ReusableLZ4BlockInputStream;
import net.jpountz.lz4.LZ4SafeDecompressor;
import net.jpountz.xxhash.XXHash32;

@NotThreadSafe
public class PooledLZ4BlockInputStream extends ReusableLZ4BlockInputStream {

    private final PooledLZ4BlockInputStreamObjectPool pool;

    public PooledLZ4BlockInputStream(final LZ4SafeDecompressor decompressor, final XXHash32 checksum,
            final PooledLZ4BlockInputStreamObjectPool pool) {
        super(decompressor, checksum);
        this.pool = pool;
    }

    @Override
    public void close() {
        super.close();
        pool.returnObject(this);
    }

}
