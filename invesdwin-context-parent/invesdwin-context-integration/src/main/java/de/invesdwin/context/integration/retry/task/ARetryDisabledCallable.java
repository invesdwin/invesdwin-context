package de.invesdwin.context.integration.retry.task;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.RetryThreads;

@ThreadSafe
public abstract class ARetryDisabledCallable<E> implements Callable<E> {

    @Override
    public final E call() {
        final Boolean registerThreadRetryDisabled = RetryThreads.registerThreadRetryDisabled();
        try {
            return callRetryDisabled();
        } finally {
            RetryThreads.unregisterThreadRetryDisabled(registerThreadRetryDisabled);
        }
    }

    protected abstract E callRetryDisabled();

}
