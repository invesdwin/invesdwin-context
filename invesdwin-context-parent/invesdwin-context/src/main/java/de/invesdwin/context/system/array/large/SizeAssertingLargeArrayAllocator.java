package de.invesdwin.context.system.array.large;

import java.io.File;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.util.collections.array.large.IBooleanLargeArray;
import de.invesdwin.util.collections.array.large.IDoubleLargeArray;
import de.invesdwin.util.collections.array.large.IIntegerLargeArray;
import de.invesdwin.util.collections.array.large.ILargeArray;
import de.invesdwin.util.collections.array.large.ILongLargeArray;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@Immutable
public class SizeAssertingLargeArrayAllocator implements ILargeArrayAllocator {

    private final ILargeArrayAllocator delegate;

    public SizeAssertingLargeArrayAllocator(final ILargeArrayAllocator delegate) {
        this.delegate = delegate;
    }

    @Override
    public IMemoryBuffer getMemoryBuffer(final String id) {
        return delegate.getMemoryBuffer(id);
    }

    @Override
    public IDoubleLargeArray getDoubleArray(final String id) {
        return delegate.getDoubleArray(id);
    }

    @Override
    public IIntegerLargeArray getIntegerArray(final String id) {
        return delegate.getIntegerArray(id);
    }

    @Override
    public IBooleanLargeArray getBooleanArray(final String id) {
        return delegate.getBooleanArray(id);
    }

    @Override
    public ILargeBitSet getBitSet(final String id) {
        return delegate.getBitSet(id);
    }

    @Override
    public ILongLargeArray getLongArray(final String id) {
        return delegate.getLongArray(id);
    }

    public static void assertSize(final Object parent, final String id, final long size, final ILargeArray array) {
        if (size != array.size()) {
            throw new IllegalStateException(
                    "Expected size [" + size + "] but got [" + array.size() + "] for id [" + id + "] in: " + parent);
        }
    }

    @Override
    public IMemoryBuffer newMemoryBuffer(final String id, final long size) {
        final IMemoryBuffer array = delegate.newMemoryBuffer(id, size);
        assertSize(this, id, size, array);
        return array;
    }

    @Override
    public IDoubleLargeArray newDoubleArray(final String id, final long size) {
        final IDoubleLargeArray array = delegate.newDoubleArray(id, size);
        assertSize(this, id, size, array);
        return array;
    }

    @Override
    public IIntegerLargeArray newIntegerArray(final String id, final long size) {
        final IIntegerLargeArray array = delegate.newIntegerArray(id, size);
        assertSize(this, id, size, array);
        return array;
    }

    @Override
    public IBooleanLargeArray newBooleanArray(final String id, final long size) {
        final IBooleanLargeArray array = delegate.newBooleanArray(id, size);
        assertSize(this, id, size, array);
        return array;
    }

    @Override
    public ILargeBitSet newBitSet(final String id, final long size) {
        final ILargeBitSet array = delegate.newBitSet(id, size);
        assertSize(this, id, size, array);
        return array;
    }

    @Override
    public ILongLargeArray newLongArray(final String id, final long size) {
        final ILongLargeArray array = delegate.newLongArray(id, size);
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
    public boolean isOnHeap(final long size) {
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
        if (obj instanceof SizeAssertingLargeArrayAllocator) {
            final SizeAssertingLargeArrayAllocator cObj = (SizeAssertingLargeArrayAllocator) obj;
            return Objects.equals(delegate, cObj.delegate);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(SizeAssertingLargeArrayAllocator.class, delegate);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(delegate).toString();
    }
}
