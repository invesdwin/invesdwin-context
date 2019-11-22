package de.invesdwin.context.integration.retry.task;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.retry.backoff.BackOffPolicy;

import de.invesdwin.context.integration.retry.hook.IRetryHook;
import de.invesdwin.context.integration.retry.hook.RetryHookManager;
import de.invesdwin.util.error.Throwables;

@ThreadSafe
public abstract class ARetryRunnable implements Runnable {

    private final RetryOriginator originator;

    public ARetryRunnable(final RetryOriginator originator) {
        this.originator = originator;
    }

    @Override
    public final void run() {
        final Callable<Void> callable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                runRetry();
                return null;
            }
        };
        final IRetryHook retryListener = getRetryListener();
        final de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryCallback<Void> retryCallback = new de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryCallback<Void>(
                callable, originator, getBackOffPolicyOverride(), retryListener);
        try {
            de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryTemplate.INSTANCE.execute(retryCallback);
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

    protected abstract void runRetry() throws Exception;

}
