package de.invesdwin.context.integration.streams.compressor.lz4.output.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.Checksum;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator;
import de.invesdwin.util.concurrent.pool.AObjectPool;
import net.jpountz.lz4.LZ4Compressor;

@NotThreadSafe
public final class PooledLZ4BlockOutputStreamObjectPool extends AObjectPool<PooledLZ4BlockOutputStream> {

    private final int maxPoolSize;
    private final NodeBufferingIterator<PooledLZ4BlockOutputStream> pooledLZ4BlockOutputStreamRotation = new NodeBufferingIterator<PooledLZ4BlockOutputStream>();

    public PooledLZ4BlockOutputStreamObjectPool(final int blockSize, final LZ4Compressor compressor,
            final Checksum checksum, final int maxPoolSize) {
        super(null);
        setFactory(new PooledLZ4BlockOutputStreamObjectFactory(this, blockSize, compressor, checksum));
        this.maxPoolSize = maxPoolSize;
    }

    @Override
    protected synchronized PooledLZ4BlockOutputStream internalBorrowObject() {
        if (pooledLZ4BlockOutputStreamRotation.isEmpty()) {
            return factory.makeObject();
        }
        final PooledLZ4BlockOutputStream pooledLZ4BlockOutputStream = pooledLZ4BlockOutputStreamRotation.next();
        if (pooledLZ4BlockOutputStream != null) {
            return pooledLZ4BlockOutputStream;
        } else {
            return factory.makeObject();
        }
    }

    @Override
    public synchronized int getNumIdle() {
        return pooledLZ4BlockOutputStreamRotation.size();
    }

    @Override
    public synchronized Collection<PooledLZ4BlockOutputStream> internalClear() {
        final Collection<PooledLZ4BlockOutputStream> removed = new ArrayList<PooledLZ4BlockOutputStream>();
        while (!pooledLZ4BlockOutputStreamRotation.isEmpty()) {
            removed.add(pooledLZ4BlockOutputStreamRotation.next());
        }
        return removed;
    }

    @Override
    protected synchronized PooledLZ4BlockOutputStream internalAddObject() {
        final PooledLZ4BlockOutputStream pooled = factory.makeObject();
        pooledLZ4BlockOutputStreamRotation.add(factory.makeObject());
        return pooled;
    }

    @Override
    protected synchronized void internalReturnObject(final PooledLZ4BlockOutputStream obj) {
        if (pooledLZ4BlockOutputStreamRotation.size() < maxPoolSize) {
            pooledLZ4BlockOutputStreamRotation.add(obj);
        }
    }

    @Override
    protected void internalInvalidateObject(final PooledLZ4BlockOutputStream obj) {
        //Nothing happens
    }

    @Override
    protected synchronized void internalRemoveObject(final PooledLZ4BlockOutputStream obj) {
        pooledLZ4BlockOutputStreamRotation.next();
    }

}
