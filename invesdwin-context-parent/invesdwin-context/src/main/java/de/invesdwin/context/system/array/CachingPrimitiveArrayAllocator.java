package de.invesdwin.context.system.array;

import java.io.File;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.util.collections.array.IBooleanArray;
import de.invesdwin.util.collections.array.IDoubleArray;
import de.invesdwin.util.collections.array.IIntegerArray;
import de.invesdwin.util.collections.array.ILongArray;
import de.invesdwin.util.collections.array.IPrimitiveArray;
import de.invesdwin.util.collections.attributes.AttributesMap;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.pool.MemoryLimit;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@ThreadSafe
public class CachingPrimitiveArrayAllocator implements IPrimitiveArrayAllocator {

    private final IPrimitiveArrayAllocator delegate;
    private final Map<String, IPrimitiveArray> map = newMap();
    private final Runnable maybeClearCache;
    private AttributesMap attributes;

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

    @Override
    public IByteBuffer newByteBuffer(final String id, final int size) {
        maybeClearCache();
        return (IByteBuffer) map.computeIfAbsent(id, (t) -> delegate.newByteBuffer(id, size));
    }

    @Override
    public IDoubleArray newDoubleArray(final String id, final int size) {
        maybeClearCache();
        return (IDoubleArray) map.computeIfAbsent(id, (t) -> delegate.newDoubleArray(id, size));
    }

    @Override
    public IIntegerArray newIntegerArray(final String id, final int size) {
        maybeClearCache();
        return (IIntegerArray) map.computeIfAbsent(id, (t) -> delegate.newIntegerArray(id, size));
    }

    @Override
    public IBooleanArray newBooleanArray(final String id, final int size) {
        maybeClearCache();
        return (IBooleanArray) map.computeIfAbsent(id, (t) -> delegate.newBooleanArray(id, size));
    }

    @Override
    public IBitSet newBitSet(final String id, final int size) {
        maybeClearCache();
        return (IBitSet) map.computeIfAbsent(id, (t) -> delegate.newBitSet(id, size));
    }

    @Override
    public ILongArray newLongArray(final String id, final int size) {
        maybeClearCache();
        return (ILongArray) map.computeIfAbsent(id, (t) -> delegate.newLongArray(id, size));
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
        if (attributes == null) {
            synchronized (this) {
                if (attributes == null) {
                    attributes = new AttributesMap();
                }
            }
        }
        return attributes;
    }

    @Override
    public IProperties getProperties() {
        return delegate.getProperties();
    }

    @Override
    public void clear() {
        map.clear();
        delegate.clear();
        final AttributesMap attributesCopy = attributes;
        if (attributesCopy != null) {
            attributesCopy.clear();
            attributes = null;
        }
    }

    @Override
    public boolean isOnHeap(final int size) {
        return delegate.isOnHeap(size);
    }

    @Override
    public File getDirectory() {
        return delegate.getDirectory();
    }

}
