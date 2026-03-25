package de.invesdwin.context.system.array.primitive;

import java.io.File;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.context.system.properties.MapProperties;
import de.invesdwin.util.collections.array.primitive.IBooleanPrimtiveArray;
import de.invesdwin.util.collections.array.primitive.IDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.accessor.IPrimitiveArrayAccessor;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.buffer.BufferBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.buffer.BufferDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.buffer.BufferIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.buffer.BufferLongPrimitiveArray;
import de.invesdwin.util.collections.attributes.AttributesMap;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.UninitializedDirectByteBuffers;

@ThreadSafe
public final class OffHeapPrimitiveArrayAllocator implements IPrimitiveArrayAllocator {

    private AttributesMap attributes;
    private MapProperties properties;

    public OffHeapPrimitiveArrayAllocator() {}

    @Override
    public IByteBuffer getByteBuffer(final String id) {
        return null;
    }

    @Override
    public IDoublePrimitiveArray getDoubleArray(final String id) {
        return null;
    }

    @Override
    public IIntegerPrimitiveArray getIntegerArray(final String id) {
        return null;
    }

    @Override
    public IBooleanPrimtiveArray getBooleanArray(final String id) {
        return null;
    }

    @Override
    public IPrimitiveBitSet getBitSet(final String id) {
        return null;
    }

    @Override
    public ILongPrimitiveArray getLongArray(final String id) {
        return null;
    }

    @Override
    public IByteBuffer newByteBuffer(final String id, final int size) {
        return ByteBuffers.allocateDirect(size);
    }

    @Override
    public IDoublePrimitiveArray newDoubleArray(final String id, final int size) {
        final BufferDoublePrimitiveArray array = new BufferDoublePrimitiveArray(ByteBuffers.allocateDirect(size * Double.BYTES));
        clearBeforeUsage(array);
        return array;
    }

    @Override
    public IIntegerPrimitiveArray newIntegerArray(final String id, final int size) {
        final BufferIntegerPrimitiveArray array = new BufferIntegerPrimitiveArray(ByteBuffers.allocateDirect(size * Integer.BYTES));
        clearBeforeUsage(array);
        return array;
    }

    @Override
    public IBooleanPrimtiveArray newBooleanArray(final String id, final int size) {
        final BufferBooleanPrimitiveArray array = new BufferBooleanPrimitiveArray(
                ByteBuffers.allocateDirect((BitSets.wordIndex(size - 1) + 1) * Long.BYTES), size);
        clearBeforeUsage(array);
        return array;
    }

    @Override
    public IPrimitiveBitSet newBitSet(final String id, final int size) {
        final BufferBooleanPrimitiveArray booleanArray = (BufferBooleanPrimitiveArray) newBooleanArray(id, size);
        return booleanArray.getDelegate().getBitSet();
    }

    @Override
    public ILongPrimitiveArray newLongArray(final String id, final int size) {
        final BufferLongPrimitiveArray array = new BufferLongPrimitiveArray(ByteBuffers.allocateDirect(size * Long.BYTES));
        clearBeforeUsage(array);
        return array;
    }

    protected void clearBeforeUsage(final IPrimitiveArrayAccessor array) {
        if (UninitializedDirectByteBuffers.isDirectByteBufferNoCleanerSupported()) {
            //make sure everything is clear since usage might sparsely fill
            array.clear();
        }
    }

    @Override
    public String toString() {
        return OffHeapPrimitiveArrayAllocator.class.getSimpleName();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(OffHeapPrimitiveArrayAllocator.class);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof OffHeapPrimitiveArrayAllocator;
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
    public boolean isOnHeap(final int size) {
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
