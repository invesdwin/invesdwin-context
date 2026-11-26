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

    /**
     * Turns a failure out of a retried body into the verdict the retry machinery should see, instead of assuming
     * that everything is transient.
     * <p>
     * A verdict the producer already chose is kept as it is: wrapping it (say an {@code IRetryDisabledException}
     * coming out of a vendor refusal or a bounded upload ladder) in a retry-later exception would re-label a
     * permanent refusal as a wait. When nothing is declared, the same policy the retry engine uses decides, and a
     * failure it would not retry is marked retry-disabled rather than being turned into an unbounded loop. Measured
     * on a personal data source: an unreadable fixture (a schema mismatch on an immutable classpath file) was
     * wrapped in a retry-later exception, and the loop reached 26 re-reads of that file in 12 minutes, one minute of
     * back off per attempt.
     */
    @SuppressWarnings("deprecation")
    public static RuntimeException toRetryVerdict(final Throwable reason) {
        if (reason instanceof IRetryDisabledException || reason instanceof IRetryLaterException) {
            if (reason instanceof RuntimeException) {
                return (RuntimeException) reason;
            }
            //the checked members of these families (RetryLaterException, RetryDisabledException) cannot be
            //rethrown from here; the marker is what callers classify on, and the cause chain keeps it
            return new RetryLaterRuntimeException(reason);
        }
        if (shouldRetry(reason)) {
            return new RetryLaterRuntimeException(reason);
        }
        return new RetryDisabledRuntimeException(reason);
    }

}
