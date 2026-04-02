package de.invesdwin.context.system.array.large.bool;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.system.array.large.ILargeArrayAllocator;
import de.invesdwin.util.collections.array.large.IGenericLargeArray;
import de.invesdwin.util.collections.attributes.IAttributesMap;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@Immutable
public final class OnHeapGenericBooleanLargeArray implements IGenericBooleanLargeArray {

    private final ILargeArrayAllocator arrayAllocator;
    private final String name;
    private final IGenericLargeArray<Boolean> values;
    private volatile boolean initialized = false;

    @SuppressWarnings("deprecation")
    private OnHeapGenericBooleanLargeArray(final ILargeArrayAllocator arrayAllocator, final String name,
            final long size) {
        this.arrayAllocator = arrayAllocator;
        this.name = name;
        this.values = IGenericLargeArray.newInstance(Boolean.class, size);
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
    public ILargeArrayAllocator getArrayAllocator() {
        return arrayAllocator;
    }

    @Override
    public String getName() {
        return name;
    }

    public static OnHeapGenericBooleanLargeArray getInstance(final ILargeArrayAllocator arrayAllocator,
            final String name) {
        return (OnHeapGenericBooleanLargeArray) arrayAllocator.getAttributes().get(newKey(name));
    }

    private static String newKey(final String name) {
        return OnHeapGenericBooleanLargeArray.class.getSimpleName() + "_" + name;
    }

    public static OnHeapGenericBooleanLargeArray newInstance(final ILargeArrayAllocator arrayAllocator,
            final String name, final long size) {
        final IAttributesMap attributes = arrayAllocator.getAttributes();
        synchronized (attributes) {
            final String key = newKey(name);
            return attributes.getOrCreate(key, () -> new OnHeapGenericBooleanLargeArray(arrayAllocator, key, size));
        }
    }

    @Override
    public long size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
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
        values.set(index, value);
    }

    @Override
    public Boolean get(final long index) {
        return values.get(index);
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
        values.clear();
    }

}
