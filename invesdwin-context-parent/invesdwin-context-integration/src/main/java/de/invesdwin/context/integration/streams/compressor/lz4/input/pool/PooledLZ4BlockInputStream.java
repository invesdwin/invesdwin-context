package de.invesdwin.context.integration.streams.compressor.lz4.input.pool;

import java.io.IOException;
import java.util.zip.Checksum;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.streams.compressor.lz4.input.ReusableLZ4BlockInputStream;
import net.jpountz.lz4.LZ4FastDecompressor;

@NotThreadSafe
public class PooledLZ4BlockInputStream extends ReusableLZ4BlockInputStream {

    private final PooledLZ4BlockInputStreamObjectPool pool;

    public PooledLZ4BlockInputStream(final LZ4FastDecompressor decompressor, final Checksum checksum,
            final PooledLZ4BlockInputStreamObjectPool pool) {
        super(decompressor, checksum);
        this.pool = pool;
    }

    @Override
    public void close() throws IOException {
        super.close();
        pool.returnObject(this);
    }

}
