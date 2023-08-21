package de.invesdwin.context.system.array.bool;

import de.invesdwin.context.system.array.IPrimitiveArrayAllocator;
import de.invesdwin.context.system.array.OnHeapPrimitiveArrayAllocator;
import de.invesdwin.util.collections.array.IGenericArray;

public interface IGenericBooleanArray extends IGenericArray<Boolean> {

    boolean isInitialized();

    void setInitialized(boolean initialized);

    static IGenericBooleanArray newInstance(final IPrimitiveArrayAllocator arrayAllocator, final String name,
            final int size) {
        if (arrayAllocator.unwrap(OnHeapPrimitiveArrayAllocator.class) != null) {
            return new OnHeapGenericBooleanArray(IGenericArray.newInstance(Boolean.class, size));
        } else {
            return new OffHeapGenericBooleanArray(arrayAllocator, name, size);
        }
    }

}
