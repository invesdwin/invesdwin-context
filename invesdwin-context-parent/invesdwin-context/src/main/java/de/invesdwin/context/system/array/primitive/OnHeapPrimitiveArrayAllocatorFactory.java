package de.invesdwin.context.system.array.primitive;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;

@Immutable
public final class OnHeapPrimitiveArrayAllocatorFactory implements IPrimitiveArrayAllocatorFactory {

    public static final OnHeapPrimitiveArrayAllocatorFactory INSTANCE = new OnHeapPrimitiveArrayAllocatorFactory();

    private OnHeapPrimitiveArrayAllocatorFactory() {}

    @Override
    public IPrimitiveArrayAllocator newInstance() {
        return new OnHeapPrimitiveArrayAllocator();
    }

    @Override
    public String toString() {
        return OnHeapPrimitiveArrayAllocatorFactory.class.getSimpleName();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(OnHeapPrimitiveArrayAllocatorFactory.class);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof OnHeapPrimitiveArrayAllocatorFactory;
    }

}
