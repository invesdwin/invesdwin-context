package de.invesdwin.context.integration.retry.hook.log;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.integration.retry.RetryLaterPendingRuntimeException;
import de.invesdwin.context.integration.retry.fast.FastRetryLaterRuntimeException;
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

    /**
     * Attempts of a routine productive wait that pass before the chain gets a line at all (see
     * {@link #isProductiveWaitSkip(Throwable)}): below this the skip is what the machinery is for, at and above it the
     * same skip is a stall worth naming. Every later attempt of that chain reports at most once per minute through
     * LoggingPreviousCause, and the terminal line only appears for a chain that was reported (see hasReported).
     */
    public static final int ROUTINE_SKIP_REPORT_RETRIES = 5;

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
            //the holder exists before the report so that a written line can be counted for the terminal report
            previousCauseHolder.set(new LoggingPreviousCause(cause, retryCount));
            logRetry(previousCauseHolder, originator, retryCount, cause, LoggingReason.INITIAL, null);
        } else {
            final LoggingReason reason = previousCause.shouldLog(cause, retryCount);
            if (reason != null) {
                logRetry(previousCauseHolder, originator, retryCount, cause, reason, previousCause.getStart());
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
        logRetry(null, originator, retryCount, cause, reason, waitingSince);
    }

    private static void logRetry(final IMutableReference<LoggingPreviousCause> previousCauseHolder,
            final RetryOriginator originator, final int retryCount, final Throwable cause,
            final LoggingReason reason, final Instant waitingSince) {
        if (isProductiveWaitSkip(cause) && retryCount < ROUTINE_SKIP_REPORT_RETRIES) {
            //a live-cache update lock that is held by the very updater this caller waits behind is routine: on the
            //ptstaging iqfeed service one hot key wrote five such records per demand cycle (the retry hook plus the
            //four live source services' skipped-cycle reports), each carrying the four-level cause chain of the demand
            //path. A chain that never reports also never gets a terminal line, so the skip is silent end to end.
            return;
        }
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
            markReported(previousCauseHolder);
            return;
        }
        //Fast wrapper, and it is only ever logged, never thrown: fillInStackTrace() skips this wrapper's own frames
        //while logback still prints every cause's stack (proven by the ptstaging records, where the Fast top layer
        //had no frames at all and the two causes below it printed 27 and 8)
        LOG.catching(LogLevel.ERROR, new FastRetryLaterRuntimeException(message, cause));
        markReported(previousCauseHolder);
    }

    /**
     * A skip that is routine rather than a fault: the verdict's producer declared it not a fault (IHiddenException)
     * AND the wait behind it is provably productive (RetryLaterPendingRuntimeException.isPending - the holder of the
     * lock or resource is running the very work the waiter wants). Both markers are required: 'retry me' alone is not
     * a claim of 'not a fault', and a hidden verdict that is not pending is a skip someone else decided to make.
     */
    public static boolean isProductiveWaitSkip(final Throwable cause) {
        return isHiddenVerdict(cause) && RetryLaterPendingRuntimeException.isPending(cause);
    }

    private static void markReported(final IMutableReference<LoggingPreviousCause> previousCauseHolder) {
        if (previousCauseHolder != null) {
            final LoggingPreviousCause previousCause = previousCauseHolder.get();
            if (previousCause != null) {
                previousCause.markReported();
            }
        }
    }

    private static boolean isReported(final LoggingPreviousCause previousCause) {
        return previousCause != null && previousCause.hasReported();
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
        if (retryCount > 0 && isReported(previousCauseHolder.get())) {
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
        if (isReported(previousCauseHolder.get())) {
            //only a chain that opened with a report gets the closing one
            LOG.warn(createSuccessMessage(originator, retryCount));
        }
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
