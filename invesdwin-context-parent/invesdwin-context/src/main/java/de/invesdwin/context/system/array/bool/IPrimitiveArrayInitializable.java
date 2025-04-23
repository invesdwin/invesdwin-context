package de.invesdwin.context.system.array.bool;

import de.invesdwin.context.system.array.IPrimitiveArrayAllocator;

public interface IPrimitiveArrayInitializable {

    default boolean isInitialized() {
        return isInitializedShared() && isInitializedLocal();
    }

    /**
     * WARNING: for internal use only
     */
    @Deprecated
    default boolean isInitializedShared() {
        return getArrayAllocator().getProperties().getBooleanOptional(getName() + "_initialized", Boolean.FALSE);
    }

    /**
     * WARNING: for internal use only
     */
    @Deprecated
    boolean isInitializedLocal();

    String getName();

    IPrimitiveArrayAllocator getArrayAllocator();

    default void setInitialized(final boolean initialized) {
        setInitializedShared(initialized);
        setInitializedLocal(initialized);
    }

    /**
     * WARNING: for internal use only
     */
    @Deprecated
    default void setInitializedShared(final boolean initialized) {
        getArrayAllocator().getProperties().setBoolean(getName() + "_initialized", initialized);
    }

    /**
     * WARNING: for internal use only
     */
    @Deprecated
    void setInitializedLocal(boolean initialized);

}
