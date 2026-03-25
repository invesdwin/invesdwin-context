package de.invesdwin.context.system.array.primitive.bool;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.system.array.primitive.IPrimitiveArrayAllocator;
import de.invesdwin.util.collections.array.primitive.IGenericPrimitiveArray;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

@Immutable
public final class OnHeapGenericBooleanPrimitiveArray implements IGenericBooleanPrimitiveArray {

    private final IPrimitiveArrayAllocator arrayAllocator;
    private final String name;
    private final IGenericPrimitiveArray<Boolean> values;
    private volatile boolean initialized = false;

    @SuppressWarnings("deprecation")
    private OnHeapGenericBooleanPrimitiveArray(final IPrimitiveArrayAllocator arrayAllocator, final String name,
            final int size) {
        this.arrayAllocator = arrayAllocator;
        this.name = name;
        this.values = IGenericPrimitiveArray.newInstance(Boolean.class, size);
        setInitializedShared(false); //maybe reset flag if cache was cleared
    }

    @Override
    public int getId() {
        return System.identityHashCode(values);
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

    public static OnHeapGenericBooleanPrimitiveArray getInstance(final IPrimitiveArrayAllocator arrayAllocator,
            final String name) {
        return (OnHeapGenericBooleanPrimitiveArray) arrayAllocator.getAttributes().get(newKey(name));
    }

    private static String newKey(final String name) {
        return OnHeapGenericBooleanPrimitiveArray.class.getSimpleName() + "_" + name;
    }

    public static OnHeapGenericBooleanPrimitiveArray newInstance(final IPrimitiveArrayAllocator arrayAllocator,
            final String name, final int size) {
        final IAttributesMap attributes = arrayAllocator.getAttributes();
        synchronized (attributes) {
            final String key = newKey(name);
            return attributes.getOrCreate(key, () -> new OnHeapGenericBooleanPrimitiveArray(arrayAllocator, key, size));
        }
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
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
        values.set(index, value);
    }

    @Override
    public Boolean get(final int index) {
        return values.get(index);
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
        values.clear();
    }

}
