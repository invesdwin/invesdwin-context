package de.invesdwin.context.system.array.large.bool;

import de.invesdwin.context.system.array.base.IBaseArrayInitializable;
import de.invesdwin.context.system.array.large.ILargeArrayAllocator;
import de.invesdwin.util.collections.array.large.ILargeArrayId;

public interface ILargeArrayInitializable extends IBaseArrayInitializable, ILargeArrayId {

    @Override
    ILargeArrayAllocator getArrayAllocator();

}
