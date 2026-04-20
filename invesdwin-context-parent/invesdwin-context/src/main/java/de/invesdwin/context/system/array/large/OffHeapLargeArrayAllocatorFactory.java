package de.invesdwin.context.system.array.large;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;

@Immutable
public final class OffHeapLargeArrayAllocatorFactory implements ILargeArrayAllocatorFactory {

    public static final OffHeapLargeArrayAllocatorFactory INSTANCE = new OffHeapLargeArrayAllocatorFactory();

    private OffHeapLargeArrayAllocatorFactory() {}

    @Override
    public ILargeArrayAllocator newInstance() {
        return new OffHeapLargeArrayAllocator();
    }

    @Override
    public String toString() {
        return OffHeapLargeArrayAllocatorFactory.class.getSimpleName();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(OffHeapLargeArrayAllocatorFactory.class);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof OffHeapLargeArrayAllocatorFactory;
    }

}
