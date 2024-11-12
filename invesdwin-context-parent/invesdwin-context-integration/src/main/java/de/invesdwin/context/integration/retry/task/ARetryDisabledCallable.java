package de.invesdwin.context.integration.retry.task;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.Threads;

@ThreadSafe
public abstract class ARetryDisabledCallable<E> implements Callable<E> {

    @Override
    public final E call() {
        final Boolean registerThreadRetryDisabled = Threads.registerThreadRetryDisabled();
        try {
            return callRetryDisabled();
        } finally {
            Threads.unregisterThreadRetryDisabled(registerThreadRetryDisabled);
        }
    }

    protected abstract E callRetryDisabled();

}
