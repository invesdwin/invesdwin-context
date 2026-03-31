package de.invesdwin.context.system.array.large;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.Objects;

@Immutable
public final class OnHeapLargeArrayAllocatorFactory implements ILargeArrayAllocatorFactory {

    public static final OnHeapLargeArrayAllocatorFactory INSTANCE = new OnHeapLargeArrayAllocatorFactory();

    private OnHeapLargeArrayAllocatorFactory() {}

    @Override
    public ILargeArrayAllocator newInstance() {
        return new OnHeapLargeArrayAllocator();
    }

    @Override
    public String toString() {
        return OnHeapLargeArrayAllocatorFactory.class.getSimpleName();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(OnHeapLargeArrayAllocatorFactory.class);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof OnHeapLargeArrayAllocatorFactory;
    }

}
