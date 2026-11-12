package de.invesdwin.context.integration.retry.hook.log;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.integration.retry.hook.IRetryHook;
import de.invesdwin.context.integration.retry.task.RetryOriginator;
import de.invesdwin.context.log.Log;
import de.invesdwin.context.log.error.IHiddenException;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.reference.IMutableReference;
import de.invesdwin.util.concurrent.reference.ThreadLocalReference;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.log.LogLevel;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class LoggingRetryHook implements IRetryHook {

    private static final IMutableReference<LoggingPreviousCause> PREVIOUS_CAUSE_HOLDER = new ThreadLocalReference<LoggingPreviousCause>();
    private static final IMutableReference<Boolean> SKIP_RETRY_LOG = new ThreadLocalReference<Boolean>();
    private static final Log LOG = new Log(LoggingRetryHook.class);

    @Override
    public void onBeforeRetry(final RetryOriginator originator, final int retryCount, final Throwable cause) {
        if (isSkipRetryLog()) {
            return;
        }
        onBeforeRetry(PREVIOUS_CAUSE_HOLDER, originator, retryCount, cause);
    }

    public static void onBeforeRetry(final IMutableReference<LoggingPreviousCause> previousCauseHolder,
            final RetryOriginator originator, final int retryCount, final Throwable cause) {
        final LoggingPreviousCause previousCause = previousCauseHolder.get();
        if (previousCause == null) {
            logRetry(originator, retryCount, cause, LoggingReason.INITIAL, null);
            previousCauseHolder.set(new LoggingPreviousCause(cause, retryCount));
        } else {
            final LoggingReason reason = previousCause.shouldLog(cause, retryCount);
            if (reason != null) {
                logRetry(originator, retryCount, cause, reason, previousCause.getStart());
            }
        }
    }

    /**
     * WARNING: use Retries.isRetrying() instead
     */
    @Deprecated
    public static boolean isRetrying() {
        return PREVIOUS_CAUSE_HOLDER.get() != null;
    }

    /**
     * WARNING: use Retries.getCurrentRetryCount() instead
     */
    @Deprecated
    public static int getCurrentRetryCount() {
        final LoggingPreviousCause previousCause = PREVIOUS_CAUSE_HOLDER.get();
        if (previousCause == null) {
            return -1;
        } else {
            return previousCause.getCurrentRetryCount();
        }
    }

    /**
     * WARNING: use Retries.getCurrentRetryStart() instead
     */
    @Deprecated
    public static Instant getCurrentRetryStart() {
        final LoggingPreviousCause previousCause = PREVIOUS_CAUSE_HOLDER.get();
        if (previousCause == null) {
            return null;
        } else {
            return previousCause.getStart();
        }
    }

    /**
     * WARNING: use Retries.getNonPendingRetryCount() instead
     */
    @Deprecated
    public static int getNonPendingRetryCount() {
        final LoggingPreviousCause previousCause = PREVIOUS_CAUSE_HOLDER.get();
        if (previousCause == null) {
            return -1;
        } else {
            return previousCause.getNonPendingRetryCount();
        }
    }

    public static boolean isSkipRetryLog() {
        return Booleans.isTrue(SKIP_RETRY_LOG.get());
    }

    public static boolean setSkipRetryLog(final boolean skipRetryLog) {
        final boolean before = isSkipRetryLog();
        if (skipRetryLog) {
            SKIP_RETRY_LOG.set(Boolean.TRUE);
        } else {
            SKIP_RETRY_LOG.set(null);
        }
        return before;
    }

    public static void logRetry(final RetryOriginator originator, final int retryCount, final Throwable cause,
            final LoggingReason reason, final Instant waitingSince) {
        final String message = createFailureMessage(originator, retryCount, reason, waitingSince);
        if (isHiddenVerdict(cause)) {
            //the producer declared this failure not a fault: it gets one line, and the stack only with
            //Throwables.DEBUG_STACK_TRACE_ENABLED - the same rule the sources already apply through
            //FallbackLiveExceptionLog and SubscriptionFailedLog, and the same marker ThrowableConverter honours for
            //the Err lane. Before this every logged attempt wrote an ERROR record with a ~40-line stack: the
            //ptstaging iqfeed run on the 19:09 build wrote 547 such chains (546 resolved after exactly one retry),
            //which filled 18,496 of error.log's 21,903 lines and 23,392 of common.log's 44,356. A retry-later that
            //is not marked hidden keeps its record: "retry me" is not a claim of "not a fault". onRetryAborted and
            //onRetrySucceeded still report the outcome.
            LOG.warn(message + ", " + Throwables.getMessagesAndStackTraceIfDebugEnabled(cause));
            return;
        }
        //a plain wrapper on purpose: every Fast* retry type carries IHiddenException, and this branch reports a
        //genuine fault, so wrapping it in a Fast form would make the record claim to be a skip verdict and hide the
        //fault's own frames from everything that honours the marker
        LOG.catching(LogLevel.ERROR, new RuntimeException(message, cause));
    }

    /**
     * Whether the failed call carries a verdict whose producer declared it not a fault (IHiddenException). The cause
     * chain is searched and not the top type, because the retried call often re-wraps the verdict - a joined async
     * update rethrows it as a plain RuntimeException via Futures.get.
     */
    public static boolean isHiddenVerdict(final Throwable cause) {
        return cause != null && Throwables.getCauseByType(cause, IHiddenException.class) != null;
    }

    @Override
    public void onRetryAborted(final RetryOriginator originator, final int retryCount, final Throwable cause) {
        if (isSkipRetryLog()) {
            return;
        }
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
        if (isSkipRetryLog()) {
            return;
        }
        onRetrySucceeded(PREVIOUS_CAUSE_HOLDER, originator, retryCount);
    }

    public static void onRetrySucceeded(final IMutableReference<LoggingPreviousCause> previousCauseHolder,
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
