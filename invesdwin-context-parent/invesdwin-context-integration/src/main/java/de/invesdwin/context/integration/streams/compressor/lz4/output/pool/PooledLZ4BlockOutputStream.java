package de.invesdwin.context.integration.streams.compressor.lz4.output.pool;

import java.io.IOException;
import java.util.zip.Checksum;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.streams.compressor.lz4.output.ReusableLZ4BlockOutputStream;
import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;
import net.jpountz.lz4.LZ4Compressor;

@NotThreadSafe
public class PooledLZ4BlockOutputStream extends ReusableLZ4BlockOutputStream
        implements INode<PooledLZ4BlockOutputStream> {

    private final PooledLZ4BlockOutputStreamObjectPool pool;
    private PooledLZ4BlockOutputStream next;

    public PooledLZ4BlockOutputStream(final int blockSize, final LZ4Compressor compressor, final Checksum checksum,
            final PooledLZ4BlockOutputStreamObjectPool pool) {
        super(blockSize, compressor, checksum);
        this.pool = pool;
    }

    @Override
    public void close() throws IOException {
        super.close();
        pool.returnObject(this);
    }

    @Override
    public PooledLZ4BlockOutputStream getNext() {
        return next;
    }

    @Override
    public void setNext(final PooledLZ4BlockOutputStream next) {
        this.next = next;
    }

}
