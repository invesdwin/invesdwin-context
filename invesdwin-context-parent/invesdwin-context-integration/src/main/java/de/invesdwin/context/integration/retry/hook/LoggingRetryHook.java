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
import io.netty.util.concurrent.FastThreadLocal;

@ThreadSafe
public class LoggingRetryHook implements IRetryHook {

    private final Log log = new Log(this);
    private final FastThreadLocal<Throwable> previousCause = new FastThreadLocal<Throwable>();

    @Override
    public void onBeforeRetry(final RetryOriginator originator, final int retryCount, final Throwable cause) {
        if (!Err.isSameMeaning(cause, previousCause.get())) {
            log.catching(Level.ERROR,
                    new FastRetryLaterRuntimeException(createFailureMessage(originator, retryCount), cause));
            previousCause.set(cause);
        }
    }

    @Override
    public void onRetryAborted(final RetryOriginator originator, final int retryCount, final Throwable cause) {
        if (retryCount > 0) {
            log.warn(createAbortedMessage(originator, retryCount, cause));
        }
        previousCause.remove();
    }

    @Override
    public void onRetrySucceeded(final RetryOriginator originator, final int retryCount) {
        log.warn(createSuccessMessage(originator, retryCount));
        previousCause.remove();
    }

    /**
     * Message should only be logged when this exception gets logged because a retry happened.
     */
    public static String createFailureMessage(final RetryOriginator originator, final int retryCount) {
        final StringBuilder sb = new StringBuilder();
        if (retryCount > 0) {
            sb.append("On ");
            sb.append(retryCount);
            sb.append(". retry a new error occured. ");
        }
        sb.append("(+) Call of ");
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
