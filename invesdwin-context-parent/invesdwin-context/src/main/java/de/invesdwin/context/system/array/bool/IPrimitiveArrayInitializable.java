package de.invesdwin.context.system.array.bool;

import de.invesdwin.context.system.array.IPrimitiveArrayAllocator;

public interface IPrimitiveArrayInitializable {

    default boolean isInitialized() {
        return getArrayAllocator().getProperties().getBooleanOptional(getName() + "_initialized", Boolean.FALSE);
    }

    String getName();

    IPrimitiveArrayAllocator getArrayAllocator();

    default void setInitialized(final boolean initialized) {
        getArrayAllocator().getProperties().setBoolean(getName() + "_initialized", initialized);
    }

}
