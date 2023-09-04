package de.invesdwin.context.integration.retry.hook.log;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public final class LoggingPreviousCause {
    private Throwable previousCause;
    private int previousCauseRetryCount;
    private final Instant start;
    private long lastLogNanos;
    private int currentRetryCount;

    @SuppressWarnings("deprecation")
    public LoggingPreviousCause(final Throwable previousCause, final int retryCount) {
        this.previousCause = previousCause;
        this.start = new Instant();
        this.previousCauseRetryCount = retryCount;
        this.currentRetryCount = retryCount;
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

    public synchronized LoggingReason shouldLog(final Throwable newCause, final int retryCount) {
        currentRetryCount = retryCount;
        if (!Err.isSameMeaning(newCause, previousCause)) {
            previousCause = newCause;
            previousCauseRetryCount = retryCount;
            lastLogNanos = System.nanoTime();
            return LoggingReason.NEW_CAUSE;
        } else {
            final long curNanos = System.nanoTime();
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