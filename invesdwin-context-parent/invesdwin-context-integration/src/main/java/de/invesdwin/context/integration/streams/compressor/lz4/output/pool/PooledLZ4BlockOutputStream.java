package de.invesdwin.context.integration.streams.compressor.lz4.output.pool;

import java.io.IOException;
import java.util.zip.Checksum;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.streams.compressor.lz4.output.ReusableLZ4BlockOutputStream;
import de.invesdwin.util.concurrent.pool.IObjectPool;
import net.jpountz.lz4.LZ4Compressor;

@NotThreadSafe
public class PooledLZ4BlockOutputStream extends ReusableLZ4BlockOutputStream {

    private final IObjectPool<PooledLZ4BlockOutputStream> pool;
    private PooledLZ4BlockOutputStream next;

    public PooledLZ4BlockOutputStream(final int blockSize, final LZ4Compressor compressor, final Checksum checksum,
            final IObjectPool<PooledLZ4BlockOutputStream> pool) {
        super(blockSize, compressor, checksum);
        this.pool = pool;
    }

    @Override
    public void close() throws IOException {
        super.close();
        pool.returnObject(this);
    }

}
