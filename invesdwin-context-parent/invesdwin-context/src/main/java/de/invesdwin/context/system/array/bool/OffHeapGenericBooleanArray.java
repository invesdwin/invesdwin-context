package de.invesdwin.context.system.array.bool;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.system.array.IPrimitiveArrayAllocator;
import de.invesdwin.util.collections.array.IGenericArray;
import de.invesdwin.util.collections.array.IPrimitiveArrayId;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public final class OffHeapGenericBooleanArray implements IGenericBooleanArray {

    private final IPrimitiveArrayAllocator arrayAllocator;
    private final String name;
    private final IBitSet trueValues;
    private final IBitSet falseValues;
    private volatile boolean initialized = false;

    @SuppressWarnings("deprecation")
    private OffHeapGenericBooleanArray(final IPrimitiveArrayAllocator arrayAllocator, final String name,
            final int size) {
        this.arrayAllocator = arrayAllocator;
        this.name = name;
        final String trueValuesId = name + "_trueValues";
        final String falseValuesId = name + "_falseValues";
        final IBitSet trueValuesCached = arrayAllocator.getBitSet(trueValuesId);
        final IBitSet falseValuesCached = arrayAllocator.getBitSet(falseValuesId);
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

    @Override
    public int getId() {
        return IPrimitiveArrayId.newId(trueValues, falseValues);
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

    public static OffHeapGenericBooleanArray getInstance(final IPrimitiveArrayAllocator arrayAllocator,
            final String name) {
        return (OffHeapGenericBooleanArray) arrayAllocator.getAttributes().get(newKey(name));
    }

    private static String newKey(final String name) {
        return OffHeapGenericBooleanArray.class.getSimpleName() + "_" + name;
    }

    public static OffHeapGenericBooleanArray newInstance(final IPrimitiveArrayAllocator arrayAllocator,
            final String name, final int size) {
        final IAttributesMap attributes = arrayAllocator.getAttributes();
        synchronized (attributes) {
            final String key = newKey(name);
            return attributes.getOrCreate(key, () -> new OffHeapGenericBooleanArray(arrayAllocator, key, size));
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
    public IGenericArray<Boolean> slice(final int fromIndex, final int length) {
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
    public void getGenerics(final int srcPos, final IGenericArray<Boolean> dest, final int destPos, final int length) {
        throw new UnsupportedOperationException();
    }

}
