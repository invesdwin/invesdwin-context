package de.invesdwin.context.system.array.large;

import java.io.File;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.context.system.properties.MapProperties;
import de.invesdwin.util.collections.array.large.IBooleanLargeArray;
import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.collections.array.large.IIntegerLargeArray;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.attributes.AttributesMap;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;

@ThreadSafe
public final class OnHeapLargeArrayAllocator implements ILargeArrayAllocator {

    private AttributesMap attributes;
    private MapProperties properties;

    public OnHeapLargeArrayAllocator() {}

    @Override
    public IMemoryBuffer getMemoryBuffer(final String id) {
        return null;
    }

    @Override
    public IDoubleLargeArray getDoubleArray(final String id) {
        return null;
    }

    @Override
    public IIntegerLargeArray getIntegerArray(final String id) {
        return null;
    }

    @Override
    public IBooleanLargeArray getBooleanArray(final String id) {
        return null;
    }

    @Override
    public ILargeBitSet getBitSet(final String id) {
        return null;
    }

    @Override
    public ILongLargeArray getLongArray(final String id) {
        return null;
    }

    @Override
    public IMemoryBuffer newMemoryBuffer(final String id, final long size) {
        return MemoryBuffers.allocate(size);
    }

    @Override
    public IDoubleLargeArray newDoubleArray(final String id, final long size) {
        return IDoubleLargeArray.newInstance(size);
    }

    @Override
    public IIntegerLargeArray newIntegerArray(final String id, final long size) {
        return IIntegerLargeArray.newInstance(size);
    }

    @Override
    public IBooleanLargeArray newBooleanArray(final String id, final long size) {
        return IBooleanLargeArray.newInstance(ILockCollectionFactory.getInstance(false).newLargeBitSet(size));
    }

    @Override
    public ILargeBitSet newBitSet(final String id, final long size) {
        return ILockCollectionFactory.getInstance(false).newLargeBitSet(size);
    }

    @Override
    public ILongLargeArray newLongArray(final String id, final long size) {
        return ILongLargeArray.newInstance(size);
    }

    @Override
    public String toString() {
        return OnHeapLargeArrayAllocator.class.getSimpleName();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(OnHeapLargeArrayAllocator.class);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof OnHeapLargeArrayAllocator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (type.isAssignableFrom(getClass())) {
            return (T) this;
        } else {
            return null;
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
        if (properties == null) {
            synchronized (this) {
                if (properties == null) {
                    properties = new MapProperties();
                }
            }
        }
        return properties;
    }

    @Override
    public void clear() {
        final AttributesMap attributesCopy = attributes;
        if (attributesCopy != null) {
            attributesCopy.clear();
            attributes = null;
        }
        properties = null;
    }

    @Override
    public boolean isOnHeap(final long size) {
        return true;
    }

    @Override
    public File getDirectory() {
        return null;
    }

    @Override
    public void close() {}

    @Override
    public ILock getLock(final String id) {
        return (ILock) getAttributes().computeIfAbsent(id, (k) -> Locks.newReentrantLock(k));
    }

}
