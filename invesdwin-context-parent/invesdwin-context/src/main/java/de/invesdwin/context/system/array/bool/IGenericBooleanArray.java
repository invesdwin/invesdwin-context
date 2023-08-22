package de.invesdwin.context.system.array.bool;

import de.invesdwin.context.system.array.IPrimitiveArrayAllocator;
import de.invesdwin.context.system.array.OnHeapPrimitiveArrayAllocator;
import de.invesdwin.util.collections.array.IGenericArray;

public interface IGenericBooleanArray extends IGenericArray<Boolean>, IPrimitiveArrayInitializable {

    static IGenericBooleanArray getInstance(final IPrimitiveArrayAllocator arrayAllocator, final String name) {
        if (arrayAllocator.unwrap(OnHeapPrimitiveArrayAllocator.class) != null) {
            return OnHeapGenericBooleanArray.getInstance(arrayAllocator, name);
        } else {
            return OffHeapGenericBooleanArray.getInstance(arrayAllocator, name);
        }
    }

    static IGenericBooleanArray newInstance(final IPrimitiveArrayAllocator arrayAllocator, final String name,
            final int size) {
        if (arrayAllocator.unwrap(OnHeapPrimitiveArrayAllocator.class) != null) {
            return OnHeapGenericBooleanArray.newInstance(arrayAllocator, name, size);
        } else {
            return OffHeapGenericBooleanArray.newInstance(arrayAllocator, name, size);
        }
    }

}
