package de.invesdwin.context.integration.retry.hook;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.ext.XLogger.Level;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.integration.retry.fast.FastRetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.task.RetryOriginator;
import de.invesdwin.context.log.Log;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;
import io.netty.util.concurrent.FastThreadLocal;

@ThreadSafe
public class LoggingRetryHook implements IRetryHook {

    private static final FastThreadLocal<PreviousCause> PREVIOUS_CAUSE = new FastThreadLocal<PreviousCause>();
    private final Log log = new Log(this);

    @Override
    public void onBeforeRetry(final RetryOriginator originator, final int retryCount, final Throwable cause) {
        final PreviousCause previousCause = PREVIOUS_CAUSE.get();
        if (previousCause == null) {
            logRetry(originator, retryCount, cause, LogReason.INITIAL, null);
            PREVIOUS_CAUSE.set(new PreviousCause(cause));
        } else {
            final LogReason reason = previousCause.shouldLog(cause);
            if (reason != null) {
                logRetry(originator, retryCount, cause, reason, previousCause.getStart());
            }
        }
    }

    public static boolean isRetrying() {
        return PREVIOUS_CAUSE.get() != null;
    }

    private void logRetry(final RetryOriginator originator, final int retryCount, final Throwable cause,
            final LogReason reason, final Instant waitingSince) {
        log.catching(Level.ERROR, new FastRetryLaterRuntimeException(
                createFailureMessage(originator, retryCount, reason, waitingSince), cause));
    }

    @Override
    public void onRetryAborted(final RetryOriginator originator, final int retryCount, final Throwable cause) {
        if (retryCount > 0) {
            log.warn(createAbortedMessage(originator, retryCount, cause));
        }
        PREVIOUS_CAUSE.remove();
    }

    @Override
    public void onRetrySucceeded(final RetryOriginator originator, final int retryCount) {
        log.warn(createSuccessMessage(originator, retryCount));
        PREVIOUS_CAUSE.remove();
    }

    /**
     * Message should only be logged when this exception gets logged because a retry happened.
     */
    public static String createFailureMessage(final RetryOriginator originator, final int retryCount,
            final LogReason reason, final Instant waitingSince) {
        final StringBuilder sb = new StringBuilder();
        if (retryCount > 0) {
            sb.append("(x) ");
            if (reason == LogReason.NEW_CAUSE) {
                sb.append("On ");
                sb.append(retryCount);
                sb.append(". retry a new error occured. ");
            } else if (reason == LogReason.TIME) {
                sb.append("On ");
                sb.append(retryCount);
                sb.append(". retry we are waiting since ");
                sb.append(new Duration(waitingSince));
                sb.append(". ");
            }
        } else {
            sb.append("(+) ");
        }
        sb.append("Call of ");
        sb.append(originator);
        sb.append(
                " will be retried until success or another decision has been made. Maybe a destination is down or another transient exception occured.");
        return sb.toString();
    }

    public static String createAbortedMessage(final RetryOriginator originator, final int retryCount,
            final Throwable cause) {
        Assertions.assertThat(retryCount).isGreaterThan(0);
        final StringBuilder sb = new StringBuilder();
        sb.append("(-) Call of ");
        sb.append(originator);
        sb.append(" aborted after ");
        sb.append(retryCount);
        sb.append(" retries. Cause: "
                + Throwables.getShortStackTrace(cause, 3, ContextProperties.getBasePackagesArray()));
        return sb.toString();
    }

    public static String createSuccessMessage(final RetryOriginator originator, final int retryCount) {
        Assertions.assertThat(retryCount).isGreaterThan(0);
        final StringBuilder sb = new StringBuilder();
        sb.append("(-) Call of ");
        sb.append(originator);
        sb.append(" succeeded after ");
        sb.append(retryCount);
        sb.append(" retries.");
        return sb.toString();
    }

    private static final class PreviousCause {
        private Throwable previousCause;
        private final Instant start;
        private long lastLogNanos;

        @SuppressWarnings("deprecation")
        PreviousCause(final Throwable previousCause) {
            this.previousCause = previousCause;
            this.start = new Instant();
            lastLogNanos = start.nanosValue();
        }

        public Instant getStart() {
            return start;
        }

        public LogReason shouldLog(final Throwable newCause) {
            if (!Err.isSameMeaning(newCause, previousCause)) {
                previousCause = newCause;
                lastLogNanos = System.nanoTime();
                return LogReason.NEW_CAUSE;
            } else {
                final long curNanos = System.nanoTime();
                if (Duration.ONE_MINUTE.isLessThanOrEqualToNanos(curNanos - lastLogNanos)) {
                    previousCause = newCause;
                    lastLogNanos = curNanos;
                    return LogReason.TIME;
                } else {
                    return null;
                }
            }
        }
    }

    public enum LogReason {
        INITIAL,
        NEW_CAUSE,
        TIME;
    }

}
