package de.invesdwin.context.system.array;

import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;

@ThreadSafe
public abstract class ARememberingPrimitiveArrayAllocatorFactory implements IPrimitiveArrayAllocatorFactory {

    private final Set<IPrimitiveArrayAllocator> instances = ILockCollectionFactory.getInstance(true).newIdentitySet();

    @Override
    public IPrimitiveArrayAllocator newInstance() {
        final IPrimitiveArrayAllocator instance = innerNewInstance();
        return instance;
    }

    public Set<IPrimitiveArrayAllocator> getInstances() {
        return instances;
    }

    protected abstract IPrimitiveArrayAllocator innerNewInstance();

}
