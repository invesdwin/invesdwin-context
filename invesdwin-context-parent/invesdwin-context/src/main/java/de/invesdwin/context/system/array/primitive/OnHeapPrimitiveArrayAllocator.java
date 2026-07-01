package de.invesdwin.context.system.array.primitive;

import java.io.File;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.system.array.base.ArrayAllocators;
import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.context.system.properties.MapProperties;
import de.invesdwin.util.collections.array.primitive.IBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.attributes.AttributesMap;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.nested.INestedExecutor;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@ThreadSafe
public final class OnHeapPrimitiveArrayAllocator implements IPrimitiveArrayAllocator {

    private final OnHeapPrimitiveArrayAllocatorFinalizer finalizer;

    public OnHeapPrimitiveArrayAllocator() {
        this.finalizer = new OnHeapPrimitiveArrayAllocatorFinalizer();
        this.finalizer.register(this);
    }

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
    public IBooleanPrimitiveArray getBooleanArray(final String id) {
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
        return ByteBuffers.allocate(size);
    }

    @Override
    public IDoublePrimitiveArray newDoubleArray(final String id, final int size) {
        return IDoublePrimitiveArray.newInstance(size);
    }

    @Override
    public IIntegerPrimitiveArray newIntegerArray(final String id, final int size) {
        return IIntegerPrimitiveArray.newInstance(size);
    }

    @Override
    public IBooleanPrimitiveArray newBooleanArray(final String id, final int size) {
        return IBooleanPrimitiveArray.newInstance(ILockCollectionFactory.getInstance(false).newPrimitiveBitSet(size));
    }

    @Override
    public IPrimitiveBitSet newBitSet(final String id, final int size) {
        return ILockCollectionFactory.getInstance(false).newPrimitiveBitSet(size);
    }

    @Override
    public ILongPrimitiveArray newLongArray(final String id, final int size) {
        return ILongPrimitiveArray.newInstance(size);
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
        if (finalizer.attributes == null) {
            synchronized (this) {
                if (finalizer.attributes == null) {
                    finalizer.attributes = new AttributesMap();
                }
            }
        }
        return finalizer.attributes;
    }

    @Override
    public IProperties getProperties() {
        if (finalizer.properties == null) {
            synchronized (this) {
                if (finalizer.properties == null) {
                    finalizer.properties = new MapProperties();
                }
            }
        }
        return finalizer.properties;
    }

    @Override
    public void clear() {
        final AttributesMap attributesCopy = finalizer.attributes;
        if (attributesCopy != null) {
            attributesCopy.clear();
            finalizer.attributes = null;
        }
        finalizer.properties = null;
    }

    @Override
    public boolean isOnHeap(final int size) {
        return true;
    }

    @Override
    public File getDirectory() {
        return null;
    }

    @Override
    public void close() {
        finalizer.close();
    }

    @Override
    public ILock getLock(final String id) {
        return (ILock) getAttributes().computeIfAbsent(id, (k) -> Locks.newReentrantLock(k));
    }

    @Override
    public INestedExecutor getExecutor() {
        return finalizer.executor;
    }

    private static final class OnHeapPrimitiveArrayAllocatorFinalizer extends AFinalizer {

        private AttributesMap attributes;
        private MapProperties properties;
        private INestedExecutor executor;

        private OnHeapPrimitiveArrayAllocatorFinalizer() {
            this.executor = ArrayAllocators.newDefaultExecutor(OnHeapPrimitiveArrayAllocator.class.getSimpleName());
        }

        @Override
        protected void clean() {
            final INestedExecutor executorCopy = executor;
            if (executorCopy != null) {
                executorCopy.close();
                executor = null;
            }
            attributes = null;
            properties = null;
        }

        @Override
        protected boolean isCleaned() {
            return executor == null;
        }

        @Override
        public boolean isThreadLocal() {
            return false;
        }
    }

}
