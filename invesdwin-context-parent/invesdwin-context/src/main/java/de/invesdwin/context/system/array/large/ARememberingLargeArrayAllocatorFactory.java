package de.invesdwin.context.system.array.large;

import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;

@ThreadSafe
public abstract class ARememberingLargeArrayAllocatorFactory implements ILargeArrayAllocatorFactory {

    private final Set<ILargeArrayAllocator> instances = ILockCollectionFactory.getInstance(true).newIdentitySet();

    @Override
    public ILargeArrayAllocator newInstance() {
        final ILargeArrayAllocator instance = innerNewInstance();
        return instance;
    }

    public Set<ILargeArrayAllocator> getInstances() {
        return instances;
    }

    protected abstract ILargeArrayAllocator innerNewInstance();

}
