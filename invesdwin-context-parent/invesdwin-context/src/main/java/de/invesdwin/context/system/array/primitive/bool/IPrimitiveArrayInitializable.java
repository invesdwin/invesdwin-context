package de.invesdwin.context.system.array.primitive.bool;

import de.invesdwin.context.system.array.base.IBaseArrayInitializable;
import de.invesdwin.context.system.array.primitive.IPrimitiveArrayAllocator;
import de.invesdwin.util.collections.array.primitive.IPrimitiveArrayId;

public interface IPrimitiveArrayInitializable extends IBaseArrayInitializable, IPrimitiveArrayId {

    @Override
    IPrimitiveArrayAllocator getArrayAllocator();

}
