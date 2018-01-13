package de.invesdwin.context.integration.retry;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.retry.hook.RetryHookManager;
import de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryCallback;
import de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryTemplate;
import de.invesdwin.context.integration.retry.internal.WrappedRetryException;
import de.invesdwin.util.error.Throwables;

@ThreadSafe
public abstract class ARetryingRunnable implements Runnable {

    private final RetryOriginator originator;

    public ARetryingRunnable(final RetryOriginator originator) {
        this.originator = originator;
    }

    @Override
    public final void run() {
        final ExceptionCauseRetryCallback<Void> retryCallback = new ExceptionCauseRetryCallback<Void>(
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        runRetryable();
                        return null;
                    }
                }, originator);
        try {
            ExceptionCauseRetryTemplate.INSTANCE.execute(retryCallback);
        } catch (final Throwable e) {
            final Throwable cause = Throwables.ignoreType(e, WrappedRetryException.class);
            RetryHookManager.getEventTrigger().onRetryAborted(retryCallback.getOriginator(),
                    retryCallback.getRetryCount(), cause);
            throw Throwables.propagate(cause);
        }
    }

    protected abstract void runRetryable() throws Exception;

}
