package de.invesdwin.context.system.array.primitive;

import java.io.File;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.log.Log;
import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.util.collections.array.primitive.IBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.pool.MemoryLimit;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@ThreadSafe
public class CachingPrimitiveArrayAllocator implements IPrimitiveArrayAllocator {

    private static final Log LOG = new Log(CachingPrimitiveArrayAllocator.class);

    private final IPrimitiveArrayAllocator delegate;
    private final Map<String, IPrimitiveArray> map = newMap();
    private final Runnable maybeClearCache;

    public CachingPrimitiveArrayAllocator(final IPrimitiveArrayAllocator delegate) {
        this.delegate = delegate;
        if (delegate.isOnHeap(1)) {
            this.maybeClearCache = () -> MemoryLimit.maybeClearCache(CachingPrimitiveArrayAllocator.class, "map", map);
        } else {
            //offHeap is not counted on the heap, so MemoryLimit cannot help us here
            this.maybeClearCache = () -> {
            };
        }
    }

    protected Map<String, IPrimitiveArray> newMap() {
        return ILockCollectionFactory.getInstance(true).newConcurrentMap();
    }

    protected void maybeClearCache() {
        maybeClearCache.run();
    }

    @Override
    public IByteBuffer getByteBuffer(final String id) {
        maybeClearCache();
        IByteBuffer cached = (IByteBuffer) map.get(id);
        if (cached != null) {
            return cached;
        }
        cached = delegate.getByteBuffer(id);
        if (cached == null) {
            return null;
        }
        map.put(id, cached);
        return cached;
    }

    @Override
    public IDoublePrimitiveArray getDoubleArray(final String id) {
        maybeClearCache();
        IDoublePrimitiveArray cached = (IDoublePrimitiveArray) map.get(id);
        if (cached != null) {
            return cached;
        }
        cached = delegate.getDoubleArray(id);
        if (cached == null) {
            return null;
        }
        map.put(id, cached);
        return cached;
    }

    @Override
    public IIntegerPrimitiveArray getIntegerArray(final String id) {
        maybeClearCache();
        IIntegerPrimitiveArray cached = (IIntegerPrimitiveArray) map.get(id);
        if (cached != null) {
            return cached;
        }
        cached = delegate.getIntegerArray(id);
        if (cached == null) {
            return null;
        }
        map.put(id, cached);
        return cached;
    }

    @Override
    public IBooleanPrimitiveArray getBooleanArray(final String id) {
        maybeClearCache();
        IBooleanPrimitiveArray cached = (IBooleanPrimitiveArray) map.get(id);
        if (cached != null) {
            return cached;
        }
        cached = delegate.getBooleanArray(id);
        if (cached == null) {
            return null;
        }
        map.put(id, cached);
        return cached;
    }

    @Override
    public IPrimitiveBitSet getBitSet(final String id) {
        maybeClearCache();
        IPrimitiveBitSet cached = (IPrimitiveBitSet) map.get(id);
        if (cached != null) {
            return cached;
        }
        cached = delegate.getBitSet(id);
        if (cached == null) {
            return null;
        }
        map.put(id, cached);
        return cached;
    }

    @Override
    public ILongPrimitiveArray getLongArray(final String id) {
        maybeClearCache();
        ILongPrimitiveArray cached = (ILongPrimitiveArray) map.get(id);
        if (cached != null) {
            return cached;
        }
        cached = delegate.getLongArray(id);
        if (cached == null) {
            return null;
        }
        map.put(id, cached);
        return cached;
    }

    @SuppressWarnings("unchecked")
    private <T extends IPrimitiveArray> T computeIfAbsentSized(final Class<T> type, final String id, final int size,
            final Function<String, T> mappingFunction) {
        maybeClearCache();
        ILock lock = null;
        try {
            for (int i = 0; i < 2; i++) {
                final T computed = (T) map.computeIfAbsent(id, mappingFunction);
                if (computed.size() == size) {
                    return computed;
                } else {
                    LOG.warn(
                            "%s: Removing from cache and trying again because [%s] is not expected size [%s] for id [%s] in: %s",
                            type.getSimpleName(), computed.size(), size, id, this);
                    lock = getLock(id);
                    //make the retry thread safe if too many threads are trying to create the same array with different sizes
                    lock.lock();
                    map.remove(id);
                }
            }
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
        throw new IllegalStateException(
                "Failed to create " + type.getSimpleName() + " of size [" + size + "] for id [" + id + "]");
    }

    @Override
    public IByteBuffer newByteBuffer(final String id, final int size) {
        return computeIfAbsentSized(IByteBuffer.class, id, size, (t) -> delegate.newByteBuffer(id, size));
    }

    @Override
    public IDoublePrimitiveArray newDoubleArray(final String id, final int size) {
        return computeIfAbsentSized(IDoublePrimitiveArray.class, id, size, (t) -> delegate.newDoubleArray(id, size));
    }

    @Override
    public IIntegerPrimitiveArray newIntegerArray(final String id, final int size) {
        return computeIfAbsentSized(IIntegerPrimitiveArray.class, id, size, (t) -> delegate.newIntegerArray(id, size));
    }

    @Override
    public IBooleanPrimitiveArray newBooleanArray(final String id, final int size) {
        return computeIfAbsentSized(IBooleanPrimitiveArray.class, id, size, (t) -> delegate.newBooleanArray(id, size));
    }

    @Override
    public IPrimitiveBitSet newBitSet(final String id, final int size) {
        return computeIfAbsentSized(IPrimitiveBitSet.class, id, size, (t) -> delegate.newBitSet(id, size));
    }

    @Override
    public ILongPrimitiveArray newLongArray(final String id, final int size) {
        return computeIfAbsentSized(ILongPrimitiveArray.class, id, size, (t) -> delegate.newLongArray(id, size));
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof CachingPrimitiveArrayAllocator) {
            final CachingPrimitiveArrayAllocator cObj = (CachingPrimitiveArrayAllocator) obj;
            return Objects.equals(delegate, cObj.delegate);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(CachingPrimitiveArrayAllocator.class, delegate);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(delegate).toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (type.isAssignableFrom(getClass())) {
            return (T) this;
        } else {
            return delegate.unwrap(type);
        }
    }

    @Override
    public IAttributesMap getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public IProperties getProperties() {
        return delegate.getProperties();
    }

    @Override
    public void clear() {
        map.clear();
        delegate.clear();
    }

    @Override
    public boolean isOnHeap(final int size) {
        return delegate.isOnHeap(size);
    }

    @Override
    public File getDirectory() {
        return delegate.getDirectory();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public ILock getLock(final String id) {
        return delegate.getLock(id);
    }

}
