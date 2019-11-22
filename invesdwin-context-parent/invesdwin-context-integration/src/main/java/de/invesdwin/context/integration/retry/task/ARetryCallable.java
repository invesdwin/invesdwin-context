package de.invesdwin.context.integration.retry.task;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.retry.backoff.BackOffPolicy;

import de.invesdwin.context.integration.retry.hook.IRetryHook;
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
        final Callable<E> callable = new Callable<E>() {
            @Override
            public E call() throws Exception {
                return callRetry();
            }
        };
        final IRetryHook retryListener = getRetryListener();
        final de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryCallback<E> retryCallback = new de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryCallback<E>(
                callable, originator, getBackOffPolicyOverride(), retryListener);
        try {
            return de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryTemplate.INSTANCE
                    .execute(retryCallback);
        } catch (final Throwable e) {
            final Throwable cause = Throwables.ignoreType(e,
                    de.invesdwin.context.integration.retry.internal.WrappedRetryException.class);
            final int retryCount = retryCallback.getRetryCount();
            RetryHookManager.getEventTrigger().onRetryAborted(originator, retryCount, cause);
            if (retryListener != null) {
                retryListener.onRetryAborted(originator, retryCount, cause);
            }
            throw Throwables.propagate(cause);
        }
    }

    protected IRetryHook getRetryListener() {
        return null;
    }

    protected BackOffPolicy getBackOffPolicyOverride() {
        return null;
    }

    protected abstract E callRetry() throws Exception;

}
