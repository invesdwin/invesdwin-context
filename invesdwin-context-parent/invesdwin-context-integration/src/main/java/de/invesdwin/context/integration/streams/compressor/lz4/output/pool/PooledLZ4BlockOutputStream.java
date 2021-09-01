package de.invesdwin.context.integration.streams.compressor.lz4.output.pool;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.streams.compressor.lz4.output.ReusableLZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4FrameOutputStream.BLOCKSIZE;
import net.jpountz.xxhash.XXHash32;

@NotThreadSafe
public class PooledLZ4BlockOutputStream extends ReusableLZ4BlockOutputStream {

    private final PooledLZ4BlockOutputStreamObjectPool pool;

    public PooledLZ4BlockOutputStream(final BLOCKSIZE blockSize, final LZ4Compressor compressor,
            final XXHash32 checksum, final PooledLZ4BlockOutputStreamObjectPool pool) {
        super(blockSize, compressor, checksum);
        this.pool = pool;
    }

    @Override
    public void close() throws IOException {
        super.close();
        pool.returnObject(this);
    }

}
