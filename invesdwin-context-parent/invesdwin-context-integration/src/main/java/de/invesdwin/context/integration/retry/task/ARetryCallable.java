package de.invesdwin.context.integration.retry.task;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.retry.hook.RetryHookManager;
import de.invesdwin.util.error.Throwables;

@ThreadSafe
public abstract class ARetryCallable<E> implements Callable<E> {

    private final RetryOriginator originator;

    public ARetryCallable(final RetryOriginator originator) {
        this.originator = originator;
    }

    @Override
    public final E call() {
        final de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryCallback<E> retryCallback = new de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryCallback<E>(
                new Callable<E>() {
                    @Override
                    public E call() throws Exception {
                        return callRetry();
                    }
                }, originator);
        try {
            return de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryTemplate.INSTANCE
                    .execute(retryCallback);
        } catch (final Throwable e) {
            final Throwable cause = Throwables.ignoreType(e,
                    de.invesdwin.context.integration.retry.internal.WrappedRetryException.class);
            RetryHookManager.getEventTrigger()
                    .onRetryAborted(retryCallback.getOriginator(), retryCallback.getRetryCount(), cause);
            throw Throwables.propagate(cause);
        }
    }

    protected abstract E callRetry() throws Exception;

}
