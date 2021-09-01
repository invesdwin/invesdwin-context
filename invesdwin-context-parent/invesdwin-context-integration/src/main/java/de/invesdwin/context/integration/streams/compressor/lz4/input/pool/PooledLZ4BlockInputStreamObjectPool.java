package de.invesdwin.context.integration.streams.compressor.lz4.input.pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.Checksum;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.pool.AObjectPool;
import net.jpountz.lz4.LZ4FastDecompressor;

@NotThreadSafe
public final class PooledLZ4BlockInputStreamObjectPool extends AObjectPool<PooledLZ4BlockInputStream> {

    private final int maxPoolSize;
    private final List<PooledLZ4BlockInputStream> pooledLZ4BlockInputStreamRotation = new ArrayList<PooledLZ4BlockInputStream>();

    public PooledLZ4BlockInputStreamObjectPool(final LZ4FastDecompressor decompressor, final Checksum checksum,
            final int maxPoolSize) {
        super(null);
        setFactory(new PooledLZ4BlockInputStreamObjectFactory(this, decompressor, checksum));
        this.maxPoolSize = maxPoolSize;
    }

    @Override
    protected synchronized PooledLZ4BlockInputStream internalBorrowObject() {
        if (pooledLZ4BlockInputStreamRotation.isEmpty()) {
            return factory.makeObject();
        }
        final PooledLZ4BlockInputStream poledLZ4BlockInputStream = pooledLZ4BlockInputStreamRotation.remove(0);
        if (poledLZ4BlockInputStream != null) {
            return poledLZ4BlockInputStream;
        } else {
            return factory.makeObject();
        }
    }

    @Override
    public synchronized int getNumIdle() {
        return pooledLZ4BlockInputStreamRotation.size();
    }

    @Override
    public synchronized Collection<PooledLZ4BlockInputStream> internalClear() {
        final Collection<PooledLZ4BlockInputStream> removed = new ArrayList<PooledLZ4BlockInputStream>();
        while (!pooledLZ4BlockInputStreamRotation.isEmpty()) {
            removed.add(pooledLZ4BlockInputStreamRotation.remove(0));
        }
        return removed;
    }

    @Override
    protected synchronized PooledLZ4BlockInputStream internalAddObject() {
        final PooledLZ4BlockInputStream pooled = factory.makeObject();
        pooledLZ4BlockInputStreamRotation.add(factory.makeObject());
        return pooled;
    }

    @Override
    protected synchronized void internalReturnObject(final PooledLZ4BlockInputStream obj) {
        if (pooledLZ4BlockInputStreamRotation.size() < maxPoolSize) {
            pooledLZ4BlockInputStreamRotation.add(obj);
        }
    }

    @Override
    protected void internalInvalidateObject(final PooledLZ4BlockInputStream obj) {
        //Nothing happens
    }

    @Override
    protected synchronized void internalRemoveObject(final PooledLZ4BlockInputStream obj) {
        pooledLZ4BlockInputStreamRotation.remove(obj);
    }

}
