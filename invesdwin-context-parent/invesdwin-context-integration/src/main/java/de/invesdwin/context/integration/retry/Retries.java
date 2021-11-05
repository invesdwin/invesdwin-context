package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.retry.hook.log.LoggingRetryHook;
import de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryPolicy;

@Immutable
public final class Retries {

    private Retries() {
    }

    public static boolean isRetrying() {
        return LoggingRetryHook.isRetrying();
    }

    public static boolean shouldRetry(final Throwable reason) {
        return ExceptionCauseRetryPolicy.shouldRetry(reason);
    }

}
