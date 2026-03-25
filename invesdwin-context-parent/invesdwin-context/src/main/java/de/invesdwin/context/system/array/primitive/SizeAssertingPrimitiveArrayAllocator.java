package de.invesdwin.context.system.array.primitive;

import java.io.File;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.util.collections.array.primitive.IBooleanPrimtiveArray;
import de.invesdwin.util.collections.array.primitive.IDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.lang.Objects;
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
    public IDoublePrimitiveArray getDoubleArray(final String id) {
        return delegate.getDoubleArray(id);
    }

    @Override
    public IIntegerPrimitiveArray getIntegerArray(final String id) {
        return delegate.getIntegerArray(id);
    }

    @Override
    public IBooleanPrimtiveArray getBooleanArray(final String id) {
        return delegate.getBooleanArray(id);
    }

    @Override
    public IPrimitiveBitSet getBitSet(final String id) {
        return delegate.getBitSet(id);
    }

    @Override
    public ILongPrimitiveArray getLongArray(final String id) {
        return delegate.getLongArray(id);
    }

    public static void assertSize(final Object parent, final String id, final int size, final IPrimitiveArray array) {
        if (size != array.size()) {
            throw new IllegalStateException(
                    "Expected size [" + size + "] but got [" + array.size() + "] for id [" + id + "] in: " + parent);
        }
    }

    @Override
    public IByteBuffer newByteBuffer(final String id, final int size) {
        final IByteBuffer array = delegate.newByteBuffer(id, size);
        assertSize(this, id, size, array);
        return array;
    }

    @Override
    public IDoublePrimitiveArray newDoubleArray(final String id, final int size) {
        final IDoublePrimitiveArray array = delegate.newDoubleArray(id, size);
        assertSize(this, id, size, array);
        return array;
    }

    @Override
    public IIntegerPrimitiveArray newIntegerArray(final String id, final int size) {
        final IIntegerPrimitiveArray array = delegate.newIntegerArray(id, size);
        assertSize(this, id, size, array);
        return array;
    }

    @Override
    public IBooleanPrimtiveArray newBooleanArray(final String id, final int size) {
        final IBooleanPrimtiveArray array = delegate.newBooleanArray(id, size);
        assertSize(this, id, size, array);
        return array;
    }

    @Override
    public IPrimitiveBitSet newBitSet(final String id, final int size) {
        final IPrimitiveBitSet array = delegate.newBitSet(id, size);
        assertSize(this, id, size, array);
        return array;
    }

    @Override
    public ILongPrimitiveArray newLongArray(final String id, final int size) {
        final ILongPrimitiveArray array = delegate.newLongArray(id, size);
        assertSize(this, id, size, array);
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

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof SizeAssertingPrimitiveArrayAllocator) {
            final SizeAssertingPrimitiveArrayAllocator cObj = (SizeAssertingPrimitiveArrayAllocator) obj;
            return Objects.equals(delegate, cObj.delegate);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(SizeAssertingPrimitiveArrayAllocator.class, delegate);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(delegate).toString();
    }
}
