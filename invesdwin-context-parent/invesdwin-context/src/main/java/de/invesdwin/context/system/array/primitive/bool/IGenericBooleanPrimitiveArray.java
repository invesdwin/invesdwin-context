package de.invesdwin.context.system.array.primitive.bool;

import de.invesdwin.context.system.array.primitive.IPrimitiveArrayAllocator;
import de.invesdwin.util.collections.array.primitive.IGenericPrimitiveArray;

public interface IGenericBooleanPrimitiveArray extends IGenericPrimitiveArray<Boolean>, IPrimitiveArrayInitializable {

    static IGenericBooleanPrimitiveArray getInstance(final IPrimitiveArrayAllocator arrayAllocator, final String name) {
        final OnHeapGenericBooleanPrimitiveArray onHeap = OnHeapGenericBooleanPrimitiveArray.getInstance(arrayAllocator, name);
        if (onHeap != null) {
            return onHeap;
        }
        return OffHeapGenericBooleanPrimitiveArray.getInstance(arrayAllocator, name);
    }

    static IGenericBooleanPrimitiveArray newInstance(final IPrimitiveArrayAllocator arrayAllocator, final String name,
            final int size) {
        if (arrayAllocator.isOnHeap(size)) {
            return OnHeapGenericBooleanPrimitiveArray.newInstance(arrayAllocator, name, size);
        } else {
            return OffHeapGenericBooleanPrimitiveArray.newInstance(arrayAllocator, name, size);
        }
    }

}
