package de.invesdwin.context.system.array.primitive.bool;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.system.array.primitive.IPrimitiveArrayAllocator;
import de.invesdwin.util.collections.array.base.IBaseArrayId;
import de.invesdwin.util.collections.array.primitive.IGenericPrimitiveArray;
import de.invesdwin.util.collections.array.primitive.bitset.IPrimitiveBitSet;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.concurrent.lock.ICloseableLock;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public final class OffHeapGenericBooleanPrimitiveArray implements IGenericBooleanPrimitiveArray {

    private final IPrimitiveArrayAllocator arrayAllocator;
    private final String name;
    private final IPrimitiveBitSet trueValues;
    private final IPrimitiveBitSet falseValues;
    private volatile boolean initialized = false;

    @SuppressWarnings("deprecation")
    private OffHeapGenericBooleanPrimitiveArray(final IPrimitiveArrayAllocator arrayAllocator, final String name,
            final int size) {
        this.arrayAllocator = arrayAllocator;
        this.name = name;
        final String trueValuesId = name + "_trueValues";
        final String falseValuesId = name + "_falseValues";
        try (ICloseableLock lock = arrayAllocator.getLock(trueValuesId).locked()) {
            final IPrimitiveBitSet trueValuesCached = arrayAllocator.getBitSet(trueValuesId);
            final IPrimitiveBitSet falseValuesCached = arrayAllocator.getBitSet(falseValuesId);
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
        return IBaseArrayId.newId(trueValues, falseValues);
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
    public IPrimitiveArrayAllocator getArrayAllocator() {
        return arrayAllocator;
    }

    @Override
    public String getName() {
        return name;
    }

    public static OffHeapGenericBooleanPrimitiveArray getInstance(final IPrimitiveArrayAllocator arrayAllocator,
            final String name) {
        return (OffHeapGenericBooleanPrimitiveArray) arrayAllocator.getAttributes().get(newKey(name));
    }

    private static String newKey(final String name) {
        return OffHeapGenericBooleanPrimitiveArray.class.getSimpleName() + "_" + name;
    }

    public static OffHeapGenericBooleanPrimitiveArray newInstance(final IPrimitiveArrayAllocator arrayAllocator,
            final String name, final int size) {
        final IAttributesMap attributes = arrayAllocator.getAttributes();
        synchronized (attributes) {
            final String key = newKey(name);
            return attributes.getOrCreate(key, () -> new OffHeapGenericBooleanPrimitiveArray(arrayAllocator, key, size));
        }
    }

    @Override
    public int size() {
        return trueValues.size();
    }

    @Override
    public boolean isEmpty() {
        return trueValues.isEmpty();
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBufferLength() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(final int index, final Boolean value) {
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
    public Boolean get(final int index) {
        if (trueValues.contains(index)) {
            return Boolean.TRUE;
        } else if (falseValues.contains(index)) {
            return Boolean.FALSE;
        } else {
            return null;
        }
    }

    @Override
    public IGenericPrimitiveArray<Boolean> slice(final int fromIndex, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean[] asArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean[] asArray(final int fromIndex, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean[] asArrayCopy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean[] asArrayCopy(final int fromIndex, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getGenerics(final int srcPos, final IGenericPrimitiveArray<Boolean> dest, final int destPos, final int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        trueValues.clear();
        falseValues.clear();
    }

}
