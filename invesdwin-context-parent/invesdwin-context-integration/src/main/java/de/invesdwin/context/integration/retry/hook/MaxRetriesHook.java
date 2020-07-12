package de.invesdwin.context.integration.retry.hook;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.retry.Retry;
import de.invesdwin.context.integration.retry.RetryDisabledRuntimeException;
import de.invesdwin.context.integration.retry.task.RetryOriginator;

@NotThreadSafe
public class MaxRetriesHook implements IRetryHook {

    private final int maxRetries;

    public MaxRetriesHook(final int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public void onBeforeRetry(final RetryOriginator originator, final int retryCount, final Throwable cause) {
        if (retryCount > maxRetries) {
            throw new RetryDisabledRuntimeException("Max Retries exhausted: " + retryCount, cause);
        }
    }

    @Override
    public void onRetryAborted(final RetryOriginator originator, final int retryCount, final Throwable cause) {
    }

    @Override
    public void onRetrySucceeded(final RetryOriginator originator, final int retryCount) {
    }

    public static MaxRetriesHook of(final int maxRetries) {
        if (maxRetries > 0) {
            return new MaxRetriesHook(maxRetries);
        } else {
            return null;
        }
    }

    public static IRetryHook of(final Retry annotation) {
        if (annotation != null) {
            return of(annotation.maxRetries());
        } else {
            return null;
        }
    }

}
