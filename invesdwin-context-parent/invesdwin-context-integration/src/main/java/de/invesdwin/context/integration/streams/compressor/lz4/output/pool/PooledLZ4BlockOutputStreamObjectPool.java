package de.invesdwin.context.integration.streams.compressor.lz4.output.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.zip.Checksum;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.concurrent.ManyToManyConcurrentArrayQueue;

import de.invesdwin.util.concurrent.pool.commons.ACommonsObjectPool;
import net.jpountz.lz4.LZ4Compressor;

@NotThreadSafe
public final class PooledLZ4BlockOutputStreamObjectPool extends ACommonsObjectPool<PooledLZ4BlockOutputStream> {

    private final int maxPoolSize;
    private final Queue<PooledLZ4BlockOutputStream> pooledLZ4BlockOutputStreamRotation;

    public PooledLZ4BlockOutputStreamObjectPool(final int blockSize, final LZ4Compressor compressor,
            final Checksum checksum, final int maxPoolSize) {
        super(null);
        setFactory(new PooledLZ4BlockOutputStreamObjectFactory(this, blockSize, compressor, checksum));
        this.maxPoolSize = maxPoolSize;
        pooledLZ4BlockOutputStreamRotation = new ManyToManyConcurrentArrayQueue<PooledLZ4BlockOutputStream>(
                maxPoolSize);
    }

    @Override
    protected PooledLZ4BlockOutputStream internalBorrowObject() {
        if (pooledLZ4BlockOutputStreamRotation.isEmpty()) {
            return factory.makeObject();
        }
        final PooledLZ4BlockOutputStream pooledLZ4BlockOutputStream = pooledLZ4BlockOutputStreamRotation.poll();
        if (pooledLZ4BlockOutputStream != null) {
            return pooledLZ4BlockOutputStream;
        } else {
            return factory.makeObject();
        }
    }

    @Override
    public int getNumIdle() {
        return pooledLZ4BlockOutputStreamRotation.size();
    }

    @Override
    public Collection<PooledLZ4BlockOutputStream> internalClear() {
        final Collection<PooledLZ4BlockOutputStream> removed = new ArrayList<PooledLZ4BlockOutputStream>();
        while (!pooledLZ4BlockOutputStreamRotation.isEmpty()) {
            removed.add(pooledLZ4BlockOutputStreamRotation.poll());
        }
        return removed;
    }

    @Override
    protected PooledLZ4BlockOutputStream internalAddObject() {
        final PooledLZ4BlockOutputStream pooled = factory.makeObject();
        pooledLZ4BlockOutputStreamRotation.offer(factory.makeObject());
        return pooled;
    }

    @Override
    protected void internalReturnObject(final PooledLZ4BlockOutputStream obj) {
        if (pooledLZ4BlockOutputStreamRotation.size() < maxPoolSize) {
            pooledLZ4BlockOutputStreamRotation.offer(obj);
        }
    }

    @Override
    protected void internalInvalidateObject(final PooledLZ4BlockOutputStream obj) {
        //Nothing happens
    }

    @Override
    protected void internalRemoveObject(final PooledLZ4BlockOutputStream obj) {
        pooledLZ4BlockOutputStreamRotation.poll();
    }

}
