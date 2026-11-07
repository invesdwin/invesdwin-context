package de.invesdwin.context.integration.retry;

import java.nio.channels.OverlappingFileLockException;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.retry.task.RetryOriginator;
import de.invesdwin.context.log.Log;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

/**
 * The one rule a service that restarts a failing component applies to the cause its retry hook reports before
 * retrying: a cause from the retryLater family is a skipped cycle, a hard failure is a restart cause.
 *
 * The retryLater family is the IRetryLaterException marker of the retry machinery - RetryLaterException,
 * RetryLaterRuntimeException, NonBlockingRetryLaterRuntimeException, RetryLaterPendingException,
 * RetryLaterPendingRuntimeException and their stack-trace-free Fast* variants - recognised over the whole cause chain,
 * because the retry machinery wraps the original failure and a handler that re-throws it wraps the RetryLater again.
 * Such a cause says 'ask again later', which is not evidence of a broken component: recording it as the restart cause
 * made the next restart check take the component down for a failure the retry machinery had already decided to wait
 * out, and did so with an ERROR plus a fresh stack on every request that ran into it.
 *
 * Hard failures keep the restart behaviour. RetryDisabled (the IRetryDisabledException marker) is the opposite
 * statement - do not retry - so it is never a skipped cycle.
 *
 * This is the shared rule of every service that restarts on the cause reported by the retry hooks: the live source
 * services and the live strategy services of the trading platform all run on the same retry machinery and see the
 * same cause shapes, so one classification decides for all of them.
 */
@ThreadSafe
public final class RestartCauses {

    private static final Log LOG = new Log(RestartCauses.class);
    /**
     * One WARN per seam over this interval: the same RetryLater is reported by every retry of the hook and by every
     * request that runs into it.
     */
    private static final Duration WARN_INTERVAL = Duration.ONE_MINUTE;
    //only throttles the WARN above, a benign race between two seams logging at once is fine
    private static final Map<String, Instant> LAST_WARN_BY_SEAM = ILockCollectionFactory.getInstance(true)
            .newConcurrentMap();

    private RestartCauses() {}

    /**
     * Whether the cause chain carries the retryLater marker, which makes it a cycle this component skips instead of a
     * reason to restart it.
     */
    public static boolean isRetryLater(final Throwable cause) {
        return Throwables.isCausedByType(cause, IRetryLaterException.class);
    }

    /**
     * Whether this cause may be remembered as the reason to restart the component: only a hard failure, never
     * something the retry machinery already decided to retry later.
     */
    public static boolean isRestartCause(final Throwable cause) {
        return !isRetryLater(cause) && Throwables.isCausedByType(cause, OverlappingFileLockException.class);
    }

    /**
     * Reports the skipped cycle of the given seam once over WARN_INTERVAL, with the cause chain rendered as messages
     * only - the retry machinery is working as intended here, so there is nothing to stack-trace.
     *
     * @return true when the WARN was written, false when it was throttled
     */
    public static boolean logSkippedCycle(final String seamId, final RetryOriginator originator, final int retryCount,
            final Throwable cause) {
        final Instant lastWarn = LAST_WARN_BY_SEAM.get(seamId);
        if (lastWarn != null && !lastWarn.isGreaterThan(WARN_INTERVAL)) {
            return false;
        }
        LOG.warn("Skipping cycle of [%s] on retry %s of %s due to retryable %s: %s", seamId, retryCount, originator,
                cause.getClass().getSimpleName(), Throwables.concatMessagesShort(cause));
        LAST_WARN_BY_SEAM.put(seamId, new Instant());
        return true;
    }

}
