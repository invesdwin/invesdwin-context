package de.invesdwin.context.integration.retry.hook;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.retry.RetryDisabledRuntimeException;
import de.invesdwin.context.integration.retry.task.RetryOriginator;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

/**
 * Time-based counterpart to the MaxCountRetriesHook: retries are only exhausted when a wall-clock wait budget is
 * exceeded instead of a retry count, so the bound is independent of the backoff schedule and tolerates long
 * productive waits (e.g. remote services still starting up or initial downloads in progress). A hook instance
 * is created once per retry operation (getRetryListener), which anchors the start timestamp. A null or
 * non-positive maxWait disables the bound entirely (unlimited retrying).
 */
@NotThreadSafe
public class MaxDurationRetriesHook implements IRetryHook {

    private final Duration maxWait;
    private final Instant start;

    public MaxDurationRetriesHook(final Duration maxWait) {
        this.maxWait = maxWait;
        this.start = new Instant();
    }

    @Override
    public void onBeforeRetry(final RetryOriginator originator, final int retryCount, final Throwable cause) {
        final Duration waited = new Duration(start);
        if (waited.isGreaterThan(maxWait)) {
            throw new RetryDisabledRuntimeException("Max Wait Exhausted: [" + waited + "] > [" + maxWait
                    + "] after [" + retryCount + "] retries", cause);
        }
    }

    @Override
    public void onRetryAborted(final RetryOriginator originator, final int retryCount, final Throwable cause) {
    }

    @Override
    public void onRetrySucceeded(final RetryOriginator originator, final int retryCount) {
    }

    public static MaxDurationRetriesHook of(final Duration maxWait) {
        if (maxWait != null && maxWait.isGreaterThanNanos(0)) {
            return new MaxDurationRetriesHook(maxWait);
        } else {
            return null;
        }
    }

}
