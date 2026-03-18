package de.invesdwin.context.system.array;

import java.io.File;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.log.Log;
import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.util.collections.array.IBooleanArray;
import de.invesdwin.util.collections.array.IDoubleArray;
import de.invesdwin.util.collections.array.IIntegerArray;
import de.invesdwin.util.collections.array.ILongArray;
import de.invesdwin.util.collections.array.IPrimitiveArray;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.collections.bitset.IBitSet;
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
    public IDoubleArray getDoubleArray(final String id) {
        maybeClearCache();
        IDoubleArray cached = (IDoubleArray) map.get(id);
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
    public IIntegerArray getIntegerArray(final String id) {
        maybeClearCache();
        IIntegerArray cached = (IIntegerArray) map.get(id);
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
    public IBooleanArray getBooleanArray(final String id) {
        maybeClearCache();
        IBooleanArray cached = (IBooleanArray) map.get(id);
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
    public IBitSet getBitSet(final String id) {
        maybeClearCache();
        IBitSet cached = (IBitSet) map.get(id);
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
    public ILongArray getLongArray(final String id) {
        maybeClearCache();
        ILongArray cached = (ILongArray) map.get(id);
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
        for (int i = 0; i < 2; i++) {
            final T computed = (T) map.computeIfAbsent(id, mappingFunction);
            if (computed.size() == size) {
                return computed;
            } else {
                LOG.warn(
                        "%s: Removing from cache and trying again because [%s] is not expected size [%s] for id [%s] in: %s",
                        type.getSimpleName(), computed.size(), size, id, this);
                map.remove(id);
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
    public IDoubleArray newDoubleArray(final String id, final int size) {
        return computeIfAbsentSized(IDoubleArray.class, id, size, (t) -> delegate.newDoubleArray(id, size));
    }

    @Override
    public IIntegerArray newIntegerArray(final String id, final int size) {
        return computeIfAbsentSized(IIntegerArray.class, id, size, (t) -> delegate.newIntegerArray(id, size));
    }

    @Override
    public IBooleanArray newBooleanArray(final String id, final int size) {
        return computeIfAbsentSized(IBooleanArray.class, id, size, (t) -> delegate.newBooleanArray(id, size));
    }

    @Override
    public IBitSet newBitSet(final String id, final int size) {
        return computeIfAbsentSized(IBitSet.class, id, size, (t) -> delegate.newBitSet(id, size));
    }

    @Override
    public ILongArray newLongArray(final String id, final int size) {
        return computeIfAbsentSized(ILongArray.class, id, size, (t) -> delegate.newLongArray(id, size));
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
