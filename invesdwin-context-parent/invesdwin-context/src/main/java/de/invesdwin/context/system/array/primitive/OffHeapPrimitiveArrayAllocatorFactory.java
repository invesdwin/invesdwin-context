package de.invesdwin.context.system.array.primitive;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;

@Immutable
public final class OffHeapPrimitiveArrayAllocatorFactory implements IPrimitiveArrayAllocatorFactory {

    public static final OffHeapPrimitiveArrayAllocatorFactory INSTANCE = new OffHeapPrimitiveArrayAllocatorFactory();

    private OffHeapPrimitiveArrayAllocatorFactory() {}

    @Override
    public IPrimitiveArrayAllocator newInstance() {
        return new OffHeapPrimitiveArrayAllocator();
    }

    @Override
    public String toString() {
        return OffHeapPrimitiveArrayAllocatorFactory.class.getSimpleName();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(OffHeapPrimitiveArrayAllocatorFactory.class);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof OffHeapPrimitiveArrayAllocatorFactory;
    }

}
