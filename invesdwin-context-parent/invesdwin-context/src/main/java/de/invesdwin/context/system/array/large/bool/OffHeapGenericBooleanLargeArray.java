package de.invesdwin.context.system.array.large.bool;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.system.array.large.ILargeArrayAllocator;
import de.invesdwin.util.collections.array.large.IGenericLargeArray;
import de.invesdwin.util.collections.array.large.ILargeArrayId;
import de.invesdwin.util.collections.array.large.bitset.ILargeBitSet;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@Immutable
public final class OffHeapGenericBooleanLargeArray implements IGenericBooleanLargeArray {

    private final ILargeArrayAllocator arrayAllocator;
    private final String name;
    private final ILargeBitSet trueValues;
    private final ILargeBitSet falseValues;
    private volatile boolean initialized = false;

    @SuppressWarnings("deprecation")
    private OffHeapGenericBooleanLargeArray(final ILargeArrayAllocator arrayAllocator, final String name,
            final long size) {
        this.arrayAllocator = arrayAllocator;
        this.name = name;
        final String trueValuesId = name + "_trueValues";
        final String falseValuesId = name + "_falseValues";
        try (ICloseableLock lock = arrayAllocator.getLock(trueValuesId).locked()) {
            final ILargeBitSet trueValuesCached = arrayAllocator.getBitSet(trueValuesId);
            final ILargeBitSet falseValuesCached = arrayAllocator.getBitSet(falseValuesId);
            if (trueValuesCached != null && falseValuesCached != null) {
                trueValues = trueValuesCached;
                falseValues = falseValuesCached;
                initialized = isInitializedShared();
            } else {
                trueValues = arrayAllocator.newBitSet(trueValuesId, size);
                falseValues = arrayAllocator.newBitSet(falseValuesId, size);
                setInitializedShared(false); //maybe reset flag if cache was cleared
            }
        }
    }

    @Override
    public int getId() {
        return ILargeArrayId.newId(trueValues, falseValues);
    }

    @Override
    public boolean isInitializedLocal() {
        return initialized;
    }

    @Override
    public void setInitializedLocal(final boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public ILargeArrayAllocator getArrayAllocator() {
        return arrayAllocator;
    }

    @Override
    public String getName() {
        return name;
    }

    public static OffHeapGenericBooleanLargeArray getInstance(final ILargeArrayAllocator arrayAllocator,
            final String name) {
        return (OffHeapGenericBooleanLargeArray) arrayAllocator.getAttributes().get(newKey(name));
    }

    private static String newKey(final String name) {
        return OffHeapGenericBooleanLargeArray.class.getSimpleName() + "_" + name;
    }

    public static OffHeapGenericBooleanLargeArray newInstance(final ILargeArrayAllocator arrayAllocator,
            final String name, final long size) {
        final IAttributesMap attributes = arrayAllocator.getAttributes();
        synchronized (attributes) {
            final String key = newKey(name);
            return attributes.getOrCreate(key, () -> new OffHeapGenericBooleanLargeArray(arrayAllocator, key, size));
        }
    }

    @Override
    public long size() {
        return trueValues.size();
    }

    @Override
    public boolean isEmpty() {
        return trueValues.isEmpty();
    }

    @Override
    public long getBuffer(final IMemoryBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getBufferLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(final long index, final Boolean value) {
        if (value == null) {
            trueValues.remove(index);
            falseValues.remove(index);
        } else if (value) {
            trueValues.add(index);
            falseValues.remove(index);
        } else {
            trueValues.remove(index);
            falseValues.add(index);
        }
    }

    @Override
    public Boolean get(final long index) {
        if (trueValues.contains(index)) {
            return Boolean.TRUE;
        } else if (falseValues.contains(index)) {
            return Boolean.FALSE;
        } else {
            return null;
        }
    }

    @Override
    public IGenericLargeArray<Boolean> slice(final long fromIndex, final long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean[] asArray(final long fromIndex, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean[] asArrayCopy(final long fromIndex, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getGenerics(final long srcPos, final IGenericLargeArray<Boolean> dest, final long destPos,
            final long length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        trueValues.clear();
        falseValues.clear();
    }

}
