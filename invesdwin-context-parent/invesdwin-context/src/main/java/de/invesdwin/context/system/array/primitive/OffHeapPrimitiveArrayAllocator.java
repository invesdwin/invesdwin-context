package de.invesdwin.context.system.array.primitive;

import java.io.File;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.context.system.properties.MapProperties;
import de.invesdwin.util.collections.array.primitive.IBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.IIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.ILongPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.accessor.IPrimitiveArrayAccessor;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.bitset.LongArrayPrimitiveBitSet;
import de.invesdwin.util.collections.array.primitive.buffer.BufferBooleanPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.buffer.BufferDoublePrimitiveArray;
import de.invesdwin.util.collections.array.primitive.buffer.BufferIntegerPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.buffer.BufferLongPrimitiveArray;
import de.invesdwin.util.collections.attributes.AttributesMap;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.nested.ANestedExecutor;
import de.invesdwin.util.concurrent.nested.INestedExecutor;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.math.BitSets;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.bytes.UninitializedDirectByteBuffers;

@ThreadSafe
public final class OffHeapPrimitiveArrayAllocator implements IPrimitiveArrayAllocator {

    private final OffHeapPrimitiveArrayAllocatorFinalizer finalizer;

    public OffHeapPrimitiveArrayAllocator() {
        this.finalizer = new OffHeapPrimitiveArrayAllocatorFinalizer();
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
        final IByteBuffer array = ByteBuffers.allocateDirect(size);
        clearBeforeUsage(array);
        return array;
    }

    @Override
    public IDoublePrimitiveArray newDoubleArray(final String id, final int size) {
        final BufferDoublePrimitiveArray array = new BufferDoublePrimitiveArray(
                ByteBuffers.allocateDirect(size * Double.BYTES));
        clearBeforeUsage(array);
        return array;
    }

    @Override
    public IIntegerPrimitiveArray newIntegerArray(final String id, final int size) {
        final BufferIntegerPrimitiveArray array = new BufferIntegerPrimitiveArray(
                ByteBuffers.allocateDirect(size * Integer.BYTES));
        clearBeforeUsage(array);
        return array;
    }

    @Override
    public IBooleanPrimitiveArray newBooleanArray(final String id, final int size) {
        final BufferBooleanPrimitiveArray array = new BufferBooleanPrimitiveArray(
                LongArrayPrimitiveBitSet.DEFAULT_COPY_FACTORY,
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
        final BufferLongPrimitiveArray array = new BufferLongPrimitiveArray(
                ByteBuffers.allocateDirect(size * Long.BYTES));
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
        return false;
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

    private static final class OffHeapPrimitiveArrayAllocatorFinalizer extends AFinalizer {

        private AttributesMap attributes;
        private MapProperties properties;
        private INestedExecutor executor;

        private OffHeapPrimitiveArrayAllocatorFinalizer() {
            this.executor = new ANestedExecutor(OffHeapPrimitiveArrayAllocator.class.getSimpleName()) {
                @Override
                protected WrappedExecutorService newNestedExecutor(final String nestedName) {
                    return Executors.newFixedCallerRunsThreadPool(nestedName, Executors.getCpuThreadPoolCount());
                }
            };
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
