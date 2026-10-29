package de.invesdwin.context.integration.retry.hook.log;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.retry.RetryLaterPendingRuntimeException;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.date.millis.FDateNanos;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public final class LoggingPreviousCause {
    private Throwable previousCause;
    private int previousCauseRetryCount;
    private final Instant start;
    private long lastLogNanos;
    private int currentRetryCount;
    private int pendingAttempts;
    private int nonPendingRetryCount;

    @SuppressWarnings("deprecation")
    public LoggingPreviousCause(final Throwable previousCause, final int retryCount) {
        this.previousCause = previousCause;
        this.start = new Instant();
        this.previousCauseRetryCount = retryCount;
        this.currentRetryCount = retryCount;
        this.pendingAttempts = RetryLaterPendingRuntimeException.isPending(previousCause) ? 1 : 0;
        this.nonPendingRetryCount = retryCount - pendingAttempts;
        this.lastLogNanos = start.nanosValue();
    }

    public Instant getStart() {
        return start;
    }

    public Throwable getPreviousCause() {
        return previousCause;
    }

    public int getPreviousCauseRetryCount() {
        return previousCauseRetryCount;
    }

    public int getCurrentRetryCount() {
        return currentRetryCount;
    }

    /**
     * Failed attempts whose cause did not prove pending work in flight (see
     * RetryLaterPendingRuntimeException.isPending) - budgets exhaust on this so that productive waits (initial
     * downloads, strategy startup, running updaters) are never punished by a raw attempt count.
     */
    public int getNonPendingRetryCount() {
        return nonPendingRetryCount;
    }

    public synchronized LoggingReason shouldLog(final Throwable newCause, final int retryCount) {
        currentRetryCount = retryCount;
        if (RetryLaterPendingRuntimeException.isPending(newCause)) {
            pendingAttempts++;
        }
        nonPendingRetryCount = currentRetryCount - pendingAttempts;
        if (!Err.isSameMeaning(newCause, previousCause)) {
            previousCause = newCause;
            previousCauseRetryCount = retryCount;
            lastLogNanos = FDateNanos.elapsedNanos();
            return LoggingReason.NEW_CAUSE;
        } else {
            final long curNanos = FDateNanos.elapsedNanos();
            if (Duration.ONE_MINUTE.isLessThanOrEqualToNanos(curNanos - lastLogNanos)) {
                previousCause = newCause;
                previousCauseRetryCount = retryCount;
                lastLogNanos = curNanos;
                return LoggingReason.TIME;
            } else {
                return null;
            }
        }
    }
}
