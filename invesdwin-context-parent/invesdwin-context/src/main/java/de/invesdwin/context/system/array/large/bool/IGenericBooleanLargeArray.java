package de.invesdwin.context.system.array.large.bool;

import de.invesdwin.context.system.array.large.ILargeArrayAllocator;
import de.invesdwin.util.collections.array.large.IGenericLargeArray;

public interface IGenericBooleanLargeArray extends IGenericLargeArray<Boolean>, ILargeArrayInitializable {

    static IGenericBooleanLargeArray getInstance(final ILargeArrayAllocator arrayAllocator, final String name) {
        final OnHeapGenericBooleanLargeArray onHeap = OnHeapGenericBooleanLargeArray.getInstance(arrayAllocator, name);
        if (onHeap != null) {
            return onHeap;
        }
        return OffHeapGenericBooleanLargeArray.getInstance(arrayAllocator, name);
    }

    static IGenericBooleanLargeArray newInstance(final ILargeArrayAllocator arrayAllocator, final String name,
            final long size) {
        if (arrayAllocator.isOnHeap(size)) {
            return OnHeapGenericBooleanLargeArray.newInstance(arrayAllocator, name, size);
        } else {
            return OffHeapGenericBooleanLargeArray.newInstance(arrayAllocator, name, size);
        }
    }

}
