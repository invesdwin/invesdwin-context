package de.invesdwin.context.integration.retry;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.retry.hook.RetryHookManager;
import de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryCallback;
import de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryTemplate;
import de.invesdwin.context.integration.retry.internal.WrappedRetryException;
import de.invesdwin.util.error.Throwables;

@ThreadSafe
public abstract class ARetryingCallable<E> implements Callable<E> {

    private final RetryOriginator originator;

    public ARetryingCallable(final RetryOriginator originator) {
        this.originator = originator;
    }

    @Override
    public final E call() throws Exception {
        final ExceptionCauseRetryCallback<E> retryCallback = new ExceptionCauseRetryCallback<E>(new Callable<E>() {
            @Override
            public E call() throws Exception {
                return callRetryable();
            }
        }, originator);
        try {
            return ExceptionCauseRetryTemplate.INSTANCE.execute(retryCallback);
        } catch (final Throwable e) {
            final Throwable cause = Throwables.ignoreType(e, WrappedRetryException.class);
            RetryHookManager.getEventTrigger().onRetryAborted(retryCallback.getOriginator(),
                    retryCallback.getRetryCount(), cause);
            throw Throwables.propagate(cause);
        }
    }

    protected abstract E callRetryable();

}
