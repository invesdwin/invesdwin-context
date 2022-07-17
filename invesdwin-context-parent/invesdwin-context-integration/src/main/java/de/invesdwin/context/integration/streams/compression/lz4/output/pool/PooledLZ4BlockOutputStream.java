package de.invesdwin.context.integration.streams.compression.lz4.output.pool;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Checksum;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.streams.compression.lz4.output.ReusableLZ4BlockOutputStream;
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
    public PooledLZ4BlockOutputStream init(final OutputStream out) {
        super.init(out);
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
