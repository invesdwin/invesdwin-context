package de.invesdwin.context.pool;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.pool2.ObjectPool;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Threads;

/**
 * Implements the lifecycle of pooled objects
 */
@Immutable
public abstract class AObjectPool<E> implements ObjectPool<E> {

    protected IPoolableObjectFactory<E> factory;
    private final AtomicInteger activeCount = new AtomicInteger();
    private volatile boolean closed;

    public AObjectPool(final IPoolableObjectFactory<E> factory) {
        this.factory = factory;
    }

    @Override
    public final E borrowObject() throws Exception {
        throwIfClosed();
        E obj;
        while (true) {
            Threads.throwIfInterrupted();
            obj = internalBorrowObject();
            if (obj != null) {
                factory.activateObject(obj);
                if (factory.validateObject(obj)) {
                    break;
                } else {
                    factory.destroyObject(obj);
                }
            } else {
                //CHECKSTYLE:OFF we explicitly want the stacktrace here
                throw new NoSuchElementException("internalBorrow() returned null");
                //CHECKSTYLE:ON
            }
        }
        activeCount.incrementAndGet();
        return obj;
    }

    @Override
    public final void addObject() throws Exception {
        throwIfClosed();
        final E obj = internalAddObject();
        if (factory.validateObject(obj)) {
            factory.passivateObject(obj);
        } else {
            removeObject(obj);
        }
    }

    protected void removeObject(final E obj) throws Exception {
        internalRemoveObject(obj);
        factory.destroyObject(obj);
    }

    @Override
    public final void returnObject(final E obj) throws Exception {
        if (obj != null) {
            activeCount.decrementAndGet();
            if (factory.validateObject(obj)) {
                factory.passivateObject(obj);
                internalReturnObject(obj);
            } else {
                factory.destroyObject(obj);
            }
        }
    }

    @Override
    public final void invalidateObject(final E obj) throws Exception {
        if (obj != null) {
            activeCount.decrementAndGet();
            factory.destroyObject(obj);
            internalInvalidateObject(obj);
        }
    }

    @Override
    public final void clear() throws Exception {
        for (final E obj : internalClear()) {
            factory.destroyObject(obj);
        }
    }

    /**
     * Counts all active and idle objects.
     */
    public final int size() {
        return getNumIdle() + getNumActive();
    }

    /*************************** templates ******************************************/

    protected abstract E internalBorrowObject() throws Exception;

    /**
     * Returns the added object.
     */
    protected abstract E internalAddObject() throws Exception;

    /**
     * If during the add the validation failed and destroy was called, the instance must be removed afterall.
     */
    protected abstract void internalRemoveObject(E obj) throws Exception;

    protected abstract void internalReturnObject(E obj) throws Exception;

    protected abstract void internalInvalidateObject(E obj) throws Exception;

    @Override
    public abstract int getNumIdle();

    /**
     * Returns the removed idle objects.
     */
    public abstract Collection<E> internalClear() throws Exception;

    /**************************** unchangeable impl ***************************/

    @Override
    public final int getNumActive() {
        return activeCount.get();
    }

    @Override
    public void close() {
        closed = true;
    }

    public final boolean isClosed() {
        return closed;
    }

    protected final void throwIfClosed() {
        Assertions.assertThat(closed).as("Instance already closed!").isFalse();
    }

    public final void setFactory(final IPoolableObjectFactory<E> factory) {
        this.factory = factory;
    }

}
