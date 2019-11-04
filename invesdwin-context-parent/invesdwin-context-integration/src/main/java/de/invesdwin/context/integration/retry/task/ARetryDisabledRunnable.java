package de.invesdwin.context.integration.retry.task;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class ARetryDisabledRunnable implements Runnable {

    @Override
    public final void run() {
        final Boolean retryDisabledBefore = de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryPolicy.RETRY_DISABLED
                .get();
        de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryPolicy.RETRY_DISABLED.set(true);
        try {
            runRetryDisabled();
        } finally {
            if (retryDisabledBefore == null) {
                de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryPolicy.RETRY_DISABLED.remove();
            } else {
                de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryPolicy.RETRY_DISABLED
                        .set(retryDisabledBefore);
            }
        }
    }

    protected abstract void runRetryDisabled();

}
