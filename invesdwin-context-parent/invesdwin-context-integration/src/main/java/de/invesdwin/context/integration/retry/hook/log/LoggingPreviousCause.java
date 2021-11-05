package de.invesdwin.context.integration.retry.hook.log;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public final class LoggingPreviousCause {
    private Throwable previousCause;
    private final Instant start;
    private long lastLogNanos;

    @SuppressWarnings("deprecation")
    public LoggingPreviousCause(final Throwable previousCause) {
        this.previousCause = previousCause;
        this.start = new Instant();
        lastLogNanos = start.nanosValue();
    }

    public Instant getStart() {
        return start;
    }

    public synchronized LoggingReason shouldLog(final Throwable newCause) {
        if (!Err.isSameMeaning(newCause, previousCause)) {
            previousCause = newCause;
            lastLogNanos = System.nanoTime();
            return LoggingReason.NEW_CAUSE;
        } else {
            final long curNanos = System.nanoTime();
            if (Duration.ONE_MINUTE.isLessThanOrEqualToNanos(curNanos - lastLogNanos)) {
                previousCause = newCause;
                lastLogNanos = curNanos;
                return LoggingReason.TIME;
            } else {
                return null;
            }
        }
    }
}