package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.retry.hook.log.LoggingRetryHook;
import de.invesdwin.context.integration.retry.internal.ExceptionCauseRetryPolicy;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

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

    /**
     * The timestamp of the first failure of the retry sequence running on the current thread, or null when the
     * thread is not inside a retry sequence; lets wait-based retry budgets (see MaxDurationRetriesHook) be enforced
     * on code
     * paths that throw the RetryLater themselves instead of registering a hook.
     */
    @SuppressWarnings("deprecation")
    public static Instant getCurrentRetryStart() {
        return LoggingRetryHook.getCurrentRetryStart();
    }

    public static Duration getCurrentRetryWaited() {
        final Instant start = getCurrentRetryStart();
        return start == null ? null : new Duration(start);
    }

    /**
     * Failed attempts on the current thread's retry sequence whose cause did not prove pending work in flight
     * (RetryLaterPending types / PENDING_TAG) - budgets that must stay patient for initial downloads, strategy
     * startup or running updaters exhaust on this instead of getCurrentRetryCount(); -1 outside a retry sequence.
     */
    @SuppressWarnings("deprecation")
    public static int getNonPendingRetryCount() {
        return LoggingRetryHook.getNonPendingRetryCount();
    }

    @SuppressWarnings("deprecation")
    public static boolean shouldRetry(final Throwable reason) {
        return ExceptionCauseRetryPolicy.shouldRetry(reason);
    }

}
