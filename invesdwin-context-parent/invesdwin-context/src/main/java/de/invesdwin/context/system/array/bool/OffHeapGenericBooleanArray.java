package de.invesdwin.context.system.array.bool;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.system.array.IPrimitiveArrayAllocator;
import de.invesdwin.util.collections.array.IGenericArray;
import de.invesdwin.util.collections.bitset.IBitSet;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class OffHeapGenericBooleanArray implements IGenericBooleanArray {

    private final IBitSet trueValues;
    private final IBitSet falseValues;
    private boolean initialized;

    public OffHeapGenericBooleanArray(final IPrimitiveArrayAllocator arrayAllocator, final String name,
            final int size) {
        final String trueValuesId = "trueValues_" + name;
        final String falseValuesId = "falseValues_" + name;
        final IBitSet trueValuesInitialized = arrayAllocator.getBitSet(trueValuesId);
        if (trueValuesInitialized != null) {
            trueValues = trueValuesInitialized;
            falseValues = arrayAllocator.getBitSet(falseValuesId);
            initialized = true;
        } else {
            trueValues = arrayAllocator.newBitSet(trueValuesId, size);
            falseValues = arrayAllocator.newBitSet(falseValuesId, size);
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void setInitialized(final boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public int size() {
        return trueValues.size();
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
