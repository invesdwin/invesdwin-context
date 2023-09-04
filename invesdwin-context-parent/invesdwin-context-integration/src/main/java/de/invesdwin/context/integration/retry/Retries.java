package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.retry.hook.log.LoggingRetryHook;
import de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryPolicy;

@Immutable
public final class Retries {

    private Retries() {}

    @SuppressWarnings("deprecation")
    public static boolean isRetrying() {
        return LoggingRetryHook.isRetrying();
    }

    @SuppressWarnings("deprecation")
    public static int getCurrentRetryCount() {
        return LoggingRetryHook.getCurrentRetryCount();
    }

    @SuppressWarnings("deprecation")
    public static boolean shouldRetry(final Throwable reason) {
        return ExceptionCauseRetryPolicy.shouldRetry(reason);
    }

}
