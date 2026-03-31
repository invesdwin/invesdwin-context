package de.invesdwin.context.system.array.large;

import java.io.File;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.context.system.properties.MapProperties;
import de.invesdwin.util.collections.array.large.IBooleanLargeArray;
import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.collections.array.large.IIntegerLargeArray;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.collections.array.large.accessor.ILargeArrayAccessor;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.array.large.bitset.LongArrayLargeBitSet;
import de.invesdwin.util.collections.array.large.buffer.BufferBooleanLargeArray;
import de.invesdwin.util.collections.array.large.buffer.BufferDoubleLargeArray;
import de.invesdwin.util.collections.array.large.buffer.BufferIntegerLargeArray;
import de.invesdwin.util.collections.array.large.buffer.BufferLongLargeArray;
import de.invesdwin.util.collections.attributes.AttributesMap;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.streams.buffer.bytes.UninitializedDirectByteBuffers;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;

@ThreadSafe
public final class OffHeapLargeArrayAllocator implements ILargeArrayAllocator {

    private AttributesMap attributes;
    private MapProperties properties;

    public OffHeapLargeArrayAllocator() {}

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
        final IMemoryBuffer array = MemoryBuffers.allocateDirect(size);
        clearBeforeUsage(array);
        return array;
    }

    @Override
    public IDoubleLargeArray newDoubleArray(final String id, final long size) {
        final BufferDoubleLargeArray array = new BufferDoubleLargeArray(
                MemoryBuffers.allocateDirect(size * Double.BYTES));
        clearBeforeUsage(array);
        return array;
    }

    @Override
    public IIntegerLargeArray newIntegerArray(final String id, final long size) {
        final BufferIntegerLargeArray array = new BufferIntegerLargeArray(
                MemoryBuffers.allocateDirect(size * Integer.BYTES));
        clearBeforeUsage(array);
        return array;
    }

    @Override
    public IBooleanLargeArray newBooleanArray(final String id, final long size) {
        final BufferBooleanLargeArray array = new BufferBooleanLargeArray(LongArrayLargeBitSet.DEFAULT_COPY_FACTORY,
                MemoryBuffers.allocateDirect((BitSets.wordIndex(size - 1) + 1) * Long.BYTES), size);
        clearBeforeUsage(array);
        return array;
    }

    @Override
    public ILargeBitSet newBitSet(final String id, final long size) {
        final BufferBooleanLargeArray booleanArray = (BufferBooleanLargeArray) newBooleanArray(id, size);
        return booleanArray.getDelegate().getBitSet();
    }

    @Override
    public ILongLargeArray newLongArray(final String id, final long size) {
        final BufferLongLargeArray array = new BufferLongLargeArray(MemoryBuffers.allocateDirect(size * Long.BYTES));
        clearBeforeUsage(array);
        return array;
    }

    protected void clearBeforeUsage(final ILargeArrayAccessor array) {
        if (UninitializedDirectByteBuffers.isDirectByteBufferNoCleanerSupported()) {
            //make sure everything is clear since usage might sparsely fill
            array.clear();
        }
    }

    @Override
    public String toString() {
        return OffHeapLargeArrayAllocator.class.getSimpleName();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(OffHeapLargeArrayAllocator.class);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof OffHeapLargeArrayAllocator;
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
        return false;
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
