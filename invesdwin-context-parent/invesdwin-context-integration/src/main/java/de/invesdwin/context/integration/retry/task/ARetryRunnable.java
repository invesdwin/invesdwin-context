package de.invesdwin.context.integration.retry.task;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

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
        final de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryCallback<Void> retryCallback = new de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryCallback<Void>(
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        runRetry();
                        return null;
                    }
                }, originator);
        try {
            de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryTemplate.INSTANCE.execute(retryCallback);
        } catch (final Throwable e) {
            final Throwable cause = Throwables.ignoreType(e,
                    de.invesdwin.context.integration.retry.internal.WrappedRetryException.class);
            RetryHookManager.getEventTrigger()
                    .onRetryAborted(retryCallback.getOriginator(), retryCallback.getRetryCount(), cause);
            throw Throwables.propagate(cause);
        }
    }

    protected abstract void runRetry() throws Exception;

}
