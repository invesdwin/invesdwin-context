package de.invesdwin.context.integration.retry.task;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ARetryDisabledCallable<E> implements Callable<E> {

    @Override
    public final E call() throws Exception {
        final Boolean retryDisabledBefore = de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryPolicy.RETRY_DISABLED
                .get();
        de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryPolicy.RETRY_DISABLED.set(true);
        try {
            return callRetryDisabled();
        } finally {
            if (retryDisabledBefore == null) {
                de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryPolicy.RETRY_DISABLED.remove();
            } else {
                de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryPolicy.RETRY_DISABLED
                        .set(retryDisabledBefore);
            }
        }
    }

    protected abstract E callRetryDisabled() throws Exception;

}
