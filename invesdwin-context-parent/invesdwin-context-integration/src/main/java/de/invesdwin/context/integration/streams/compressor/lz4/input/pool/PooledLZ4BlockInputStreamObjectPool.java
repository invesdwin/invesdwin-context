package de.invesdwin.context.integration.streams.compressor.lz4.input.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.zip.Checksum;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.concurrent.OneToOneConcurrentArrayQueue;

import de.invesdwin.util.concurrent.pool.AObjectPool;
import net.jpountz.lz4.LZ4FastDecompressor;

@NotThreadSafe
public final class PooledLZ4BlockInputStreamObjectPool extends AObjectPool<PooledLZ4BlockInputStream> {

    private final int maxPoolSize;
    private final Queue<PooledLZ4BlockInputStream> pooledLZ4BlockInputStreamRotation;

    public PooledLZ4BlockInputStreamObjectPool(final LZ4FastDecompressor decompressor, final Checksum checksum,
            final int maxPoolSize) {
        super(null);
        setFactory(new PooledLZ4BlockInputStreamObjectFactory(this, decompressor, checksum));
        this.maxPoolSize = maxPoolSize;
        pooledLZ4BlockInputStreamRotation = new OneToOneConcurrentArrayQueue<PooledLZ4BlockInputStream>(maxPoolSize);
    }

    @Override
    protected PooledLZ4BlockInputStream internalBorrowObject() {
        if (pooledLZ4BlockInputStreamRotation.isEmpty()) {
            return factory.makeObject();
        }
        final PooledLZ4BlockInputStream poledLZ4BlockInputStream = pooledLZ4BlockInputStreamRotation.poll();
        if (poledLZ4BlockInputStream != null) {
            return poledLZ4BlockInputStream;
        } else {
            return factory.makeObject();
        }
    }

    @Override
    public int getNumIdle() {
        return pooledLZ4BlockInputStreamRotation.size();
    }

    @Override
    public Collection<PooledLZ4BlockInputStream> internalClear() {
        final Collection<PooledLZ4BlockInputStream> removed = new ArrayList<PooledLZ4BlockInputStream>();
        while (!pooledLZ4BlockInputStreamRotation.isEmpty()) {
            removed.add(pooledLZ4BlockInputStreamRotation.poll());
        }
        return removed;
    }

    @Override
    protected PooledLZ4BlockInputStream internalAddObject() {
        final PooledLZ4BlockInputStream pooled = factory.makeObject();
        pooledLZ4BlockInputStreamRotation.offer(factory.makeObject());
        return pooled;
    }

    @Override
    protected void internalReturnObject(final PooledLZ4BlockInputStream obj) {
        if (pooledLZ4BlockInputStreamRotation.size() < maxPoolSize) {
            pooledLZ4BlockInputStreamRotation.offer(obj);
        }
    }

    @Override
    protected void internalInvalidateObject(final PooledLZ4BlockInputStream obj) {
        //Nothing happens
    }

    @Override
    protected void internalRemoveObject(final PooledLZ4BlockInputStream obj) {
        pooledLZ4BlockInputStreamRotation.poll();
    }

}
