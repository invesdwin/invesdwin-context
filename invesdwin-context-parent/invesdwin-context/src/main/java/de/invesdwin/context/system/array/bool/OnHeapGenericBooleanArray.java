package de.invesdwin.context.system.array.bool;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.array.IGenericArray;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public class OnHeapGenericBooleanArray implements IGenericBooleanArray {

    private final IGenericArray<Boolean> values;
    private boolean initialized;

    public OnHeapGenericBooleanArray(final IGenericArray<Boolean> values) {
        this.values = values;
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
        return values.size();
    }

    @Override
    public int getBuffer(final IByteBuffer buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(final int index, final Boolean value) {
        values.set(index, value);
    }

    @Override
    public Boolean get(final int index) {
        return values.get(index);
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
