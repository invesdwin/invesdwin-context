package de.invesdwin.context.system.array.bool;

import de.invesdwin.context.system.array.IPrimitiveArrayAllocator;
import de.invesdwin.util.collections.array.IPrimitiveArrayId;

public interface IPrimitiveArrayInitializable extends IPrimitiveArrayId {

    default boolean isInitialized() {
        if (isInitializedLocal()) {
            //even if a shared instance gets reset, the local one (maybe an orphan) stays initialized
            return true;
        }
        return isInitializedShared();
    }

    /**
     * WARNING: for internal use only
     */
    @Deprecated
    default boolean isInitializedShared() {
        final Integer initializedId = getArrayAllocator().getProperties().getIntegerOptional(newInitializedSharedKey());
        return initializedId != null && initializedId.intValue() == getId();
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
        final String key = newInitializedSharedKey();
        if (initialized) {
            getArrayAllocator().getProperties().setInteger(key, getId());
        } else {
            getArrayAllocator().getProperties().remove(key);
        }
    }

    /**
     * WARNING: for internal use only
     */
    @Deprecated
    default String newInitializedSharedKey() {
        return getName() + "_initialized";
    }

    /**
     * WARNING: for internal use only
     */
    @Deprecated
    void setInitializedLocal(boolean initialized);

}
