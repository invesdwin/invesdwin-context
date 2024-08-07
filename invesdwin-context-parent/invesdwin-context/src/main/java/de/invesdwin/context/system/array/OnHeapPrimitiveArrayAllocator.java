package de.invesdwin.context.system.array;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.context.system.properties.MapProperties;
import de.invesdwin.util.collections.array.IBooleanArray;
import de.invesdwin.util.collections.array.IDoubleArray;
import de.invesdwin.util.collections.array.IIntegerArray;
import de.invesdwin.util.collections.array.ILongArray;
import de.invesdwin.util.collections.attributes.AttributesMap;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@ThreadSafe
public final class OnHeapPrimitiveArrayAllocator implements IPrimitiveArrayAllocator {

    private AttributesMap attributes;
    private MapProperties properties;

    public OnHeapPrimitiveArrayAllocator() {}

    @Override
    public IByteBuffer getByteBuffer(final String id) {
        return null;
    }

    @Override
    public IDoubleArray getDoubleArray(final String id) {
        return null;
    }

    @Override
    public IIntegerArray getIntegerArray(final String id) {
        return null;
    }

    @Override
    public IBooleanArray getBooleanArray(final String id) {
        return null;
    }

    @Override
    public IBitSet getBitSet(final String id) {
        return null;
    }

    @Override
    public ILongArray getLongArray(final String id) {
        return null;
    }

    @Override
    public IByteBuffer newByteBuffer(final String id, final int size) {
        return ByteBuffers.allocate(size);
    }

    @Override
    public IDoubleArray newDoubleArray(final String id, final int size) {
        return IDoubleArray.newInstance(size);
    }

    @Override
    public IIntegerArray newIntegerArray(final String id, final int size) {
        return IIntegerArray.newInstance(size);
    }

    @Override
    public IBooleanArray newBooleanArray(final String id, final int size) {
        return IBooleanArray.newInstance(ILockCollectionFactory.getInstance(false).newBitSet(size));
    }

    @Override
    public IBitSet newBitSet(final String id, final int size) {
        return ILockCollectionFactory.getInstance(false).newBitSet(size);
    }

    @Override
    public ILongArray newLongArray(final String id, final int size) {
        return ILongArray.newInstance(size);
    }

    @Override
    public String toString() {
        return OnHeapPrimitiveArrayAllocator.class.getSimpleName();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(OnHeapPrimitiveArrayAllocator.class);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof OnHeapPrimitiveArrayAllocator;
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

}
