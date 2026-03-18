package de.invesdwin.context.system.array;

import java.io.File;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.util.collections.array.IBooleanArray;
import de.invesdwin.util.collections.array.IDoubleArray;
import de.invesdwin.util.collections.array.IIntegerArray;
import de.invesdwin.util.collections.array.ILongArray;
import de.invesdwin.util.collections.array.IPrimitiveArray;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class SizeAssertingPrimitiveArrayAllocator implements IPrimitiveArrayAllocator {

    private final IPrimitiveArrayAllocator delegate;

    public SizeAssertingPrimitiveArrayAllocator(final IPrimitiveArrayAllocator delegate) {
        this.delegate = delegate;
    }

    @Override
    public IByteBuffer getByteBuffer(final String id) {
        return delegate.getByteBuffer(id);
    }

    @Override
    public IDoubleArray getDoubleArray(final String id) {
        return delegate.getDoubleArray(id);
    }

    @Override
    public IIntegerArray getIntegerArray(final String id) {
        return delegate.getIntegerArray(id);
    }

    @Override
    public IBooleanArray getBooleanArray(final String id) {
        return delegate.getBooleanArray(id);
    }

    @Override
    public IBitSet getBitSet(final String id) {
        return delegate.getBitSet(id);
    }

    @Override
    public ILongArray getLongArray(final String id) {
        return delegate.getLongArray(id);
    }

    @Override
    public IByteBuffer newByteBuffer(final String id, final int size) {
        final IByteBuffer array = delegate.newByteBuffer(id, size);
        assertSize(id, size, array);
        return array;
    }

    public static void assertSize(final String id, final int size, final IPrimitiveArray array) {
        if (size != array.size()) {
            throw new IllegalStateException(
                    "Expected size [" + size + "] but got [" + array.size() + "] for id: " + id);
        }
    }

    @Override
    public IDoubleArray newDoubleArray(final String id, final int size) {
        final IDoubleArray array = delegate.newDoubleArray(id, size);
        assertSize(id, size, array);
        return array;
    }

    @Override
    public IIntegerArray newIntegerArray(final String id, final int size) {
        final IIntegerArray array = delegate.newIntegerArray(id, size);
        assertSize(id, size, array);
        return array;
    }

    @Override
    public IBooleanArray newBooleanArray(final String id, final int size) {
        final IBooleanArray array = delegate.newBooleanArray(id, size);
        assertSize(id, size, array);
        return array;
    }

    @Override
    public IBitSet newBitSet(final String id, final int size) {
        final IBitSet array = delegate.newBitSet(id, size);
        assertSize(id, size, array);
        return array;
    }

    @Override
    public ILongArray newLongArray(final String id, final int size) {
        final ILongArray array = delegate.newLongArray(id, size);
        assertSize(id, size, array);
        return array;
    }

    @Override
    public IAttributesMap getAttributes() {
        return delegate.getAttributes();
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
    public IProperties getProperties() {
        return delegate.getProperties();
    }

    @Override
    public void clear() {
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
