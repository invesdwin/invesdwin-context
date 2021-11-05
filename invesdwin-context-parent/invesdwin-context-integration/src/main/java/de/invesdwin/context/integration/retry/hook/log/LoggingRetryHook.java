package de.invesdwin.context.integration.retry.hook.log;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.ext.XLogger.Level;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.integration.retry.fast.FastRetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.hook.IRetryHook;
import de.invesdwin.context.integration.retry.task.RetryOriginator;
import de.invesdwin.context.log.Log;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.reference.IMutableReference;
import de.invesdwin.util.concurrent.reference.ThreadLocalReference;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class LoggingRetryHook implements IRetryHook {

    private static final IMutableReference<LoggingPreviousCause> PREVIOUS_CAUSE_HOLDER = new ThreadLocalReference<LoggingPreviousCause>();
    private static final Log LOG = new Log(LoggingRetryHook.class);

    @Override
    public void onBeforeRetry(final RetryOriginator originator, final int retryCount, final Throwable cause) {
        onBeforeRetry(PREVIOUS_CAUSE_HOLDER, originator, retryCount, cause);
    }

    public static void onBeforeRetry(final IMutableReference<LoggingPreviousCause> previousCauseHolder,
            final RetryOriginator originator, final int retryCount, final Throwable cause) {
        final LoggingPreviousCause previousCause = previousCauseHolder.get();
        if (previousCause == null) {
            logRetry(originator, retryCount, cause, LoggingReason.INITIAL, null);
            previousCauseHolder.set(new LoggingPreviousCause(cause));
        } else {
            final LoggingReason reason = previousCause.shouldLog(cause);
            if (reason != null) {
                logRetry(originator, retryCount, cause, reason, previousCause.getStart());
            }
        }
    }

    public static boolean isRetrying() {
        return PREVIOUS_CAUSE_HOLDER.get() != null;
    }

    public static void logRetry(final RetryOriginator originator, final int retryCount, final Throwable cause,
            final LoggingReason reason, final Instant waitingSince) {
        LOG.catching(Level.ERROR, new FastRetryLaterRuntimeException(
                createFailureMessage(originator, retryCount, reason, waitingSince), cause));
    }

    @Override
    public void onRetryAborted(final RetryOriginator originator, final int retryCount, final Throwable cause) {
        onRetryAborted(PREVIOUS_CAUSE_HOLDER, originator, retryCount, cause);
    }

    public static void onRetryAborted(final IMutableReference<LoggingPreviousCause> previousCauseHolder,
            final RetryOriginator originator, final int retryCount, final Throwable cause) {
        if (retryCount > 0) {
            LOG.warn(createAbortedMessage(originator, retryCount, cause));
        }
        previousCauseHolder.set(null);
    }

    @Override
    public void onRetrySucceeded(final RetryOriginator originator, final int retryCount) {
        onRetrySucceeded(PREVIOUS_CAUSE_HOLDER, originator, retryCount);
    }

    private void onRetrySucceeded(final IMutableReference<LoggingPreviousCause> previousCauseHolder,
            final RetryOriginator originator, final int retryCount) {
        LOG.warn(createSuccessMessage(originator, retryCount));
        previousCauseHolder.set(null);
    }

    /**
     * Message should only be logged when this exception gets logged because a retry happened.
     */
    public static String createFailureMessage(final RetryOriginator originator, final int retryCount,
            final LoggingReason reason, final Instant waitingSince) {
        final StringBuilder sb = new StringBuilder();
        if (retryCount > 0) {
            sb.append("(x) ");
            if (reason == LoggingReason.NEW_CAUSE) {
                sb.append("On ");
                sb.append(retryCount);
                sb.append(". retry a new error occured. ");
            } else if (reason == LoggingReason.TIME) {
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

}
