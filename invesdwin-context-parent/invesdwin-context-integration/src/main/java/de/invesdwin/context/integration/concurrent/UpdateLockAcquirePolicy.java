package de.invesdwin.context.integration.concurrent;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.retry.RetryLaterPendingRuntimeException;
import de.invesdwin.context.integration.retry.fast.FastRetryLaterPendingRuntimeException;
import de.invesdwin.util.concurrent.RetryThreads;
import de.invesdwin.util.concurrent.lock.IQueuedLock;
import de.invesdwin.util.time.duration.Duration;

/**
 * The shared acquire ladder for an {@link IQueuedLock update lock}: take it, or answer a pending RetryLater - never
 * park a caller on it without a ceiling.
 * <p>
 * Three layers of this fleet own an update lock and each used to fall through to an unbounded {@code lock()} after a
 * bounded first attempt: the timeseriesdb store ({@code ACQUIRE_UPDATE_LOCK_TIMEOUT} then {@code updateLock.lock()}),
 * the live-calculated caches (the same shape, fixed in place by {@code LiveCacheUpdateLockPolicy}), and the
 * subscription storages. An unbounded park is what turns a healthy load into a wedged service: every thread of a
 * finite pool ends up queued behind the one update that holds the lock, the holder is itself in a retry back off, and
 * nothing is answered. A ceiling cannot be reached by tuning one timeout per site, because the right ceiling depends
 * on who is asking - so the tier decision and the verdict shape live here, once.
 * <p>
 * The tier comes from the markers {@link FinancialdataThreads} already maintains: a caller that entered the demand
 * path in non-blocking mode (a webserver request thread, a user interface thread, a finalizer) gets the short wait,
 * because that pool is finite and shared with answering requests; an updater, subscription or startup-hook thread gets
 * the patient wait. A wait in the middle is deliberate rather than a concession: for a path that will pay network
 * latency anyway, absorbing a sub-second hand-over is cheaper than a refused round followed by a re-ask, and skipping
 * the update on a warm cache means the caller reads the last published state instead.
 * <p>
 * A refusal is always {@link FastRetryLaterPendingRuntimeException}: <i>pending</i>, because the holder is running the
 * very update the caller wants, so it must not consume the non-pending count the demand budgets exhaust on; and
 * <i>fast</i> (no captured stack), because these refusals recur per demand cycle and a stackful verdict buries the log
 * - the production box measured 18,496 frames from 547 such chains in 21 minutes.
 *
 * @see FinancialdataThreads#isThreadBlockingUpdateCacheDisabled()
 * @see RetryLaterPendingRuntimeException#PENDING_PREFIX
 */
@ThreadSafe
public final class UpdateLockAcquirePolicy {

    private UpdateLockAcquirePolicy() {}

    /**
     * True on a thread that must not queue behind an update: it entered the demand path in non-blocking mode, so it
     * serves a finite pool that also answers requests.
     */
    public static boolean isRequestTier() {
        return FinancialdataThreads.isThreadBlockingUpdateCacheDisabled();
    }

    /**
     * The bounded wait chosen for the calling thread: the short one for a request-tier caller, the patient one for an
     * updater or subscription caller.
     */
    public static Duration getAcquireTimeout(final Duration requestWait, final Duration patientWait) {
        return isRequestTier() ? requestWait : patientWait;
    }

    /**
     * Takes {@code lock} for one update round, or answers the caller otherwise.
     * <ol>
     * <li>a thread the retry machinery refuses (UI, finalizer, interrupted) skips the round;</li>
     * <li>an uncontended lock is taken exactly as an unbounded {@code lock()} would have taken it;</li>
     * <li>an idle round takes the lock instantly or skips - the holder is already writing what this round would write,
     * so only the parking was wrong; no verdict, and no convoy reading;</li>
     * <li>a warm cache asked by a request-tier caller skips - that caller reads the last published state, it does not
     * need this round's update to answer;</li>
     * <li>otherwise the tier's bounded wait applies, and past it a pending RetryLater verdict is thrown.</li>
     * </ol>
     *
     * @param requestWait the wait for a request-tier caller; non-positive means never wait, only take a free lock
     * @param patientWait the wait for an updater or subscription caller
     * @param idle whether this round was started by an idle/helper trigger rather than by demand
     * @param warmStateReadable whether the caller could answer correctly from the last published state
     * @param ownerName names the lock and its owner in the verdict, since the captured stack would only ever show the
     *            refused caller and not the holder
     * @return false when the caller must skip this round (no lock taken, no verdict raised)
     * @throws RetryLaterPendingRuntimeException when the lock is not free within the tier's wait
     */
    public static boolean tryLockForUpdate(final IQueuedLock lock, final Duration requestWait,
            final Duration patientWait, final boolean idle, final boolean warmStateReadable, final String ownerName) {
        if (RetryThreads.isThreadRetryDisabledDefault()) {
            //do not update from a UI thread, a finalizer or an interrupted thread
            return false;
        } else if (lock.tryLock()) {
            return true;
        } else if (idle) {
            return false;
        } else if (warmStateReadable && isRequestTier()) {
            return false;
        }
        final Duration acquireTimeout = getAcquireTimeout(requestWait, patientWait);
        if (acquireTimeout == null || acquireTimeout.longValue() <= 0L) {
            throw newPendingVerdict(lock, ownerName, acquireTimeout);
        }
        if (!lock.tryLockNoInterrupt(acquireTimeout)) {
            throw newPendingVerdict(lock, ownerName, acquireTimeout);
        }
        return true;
    }

    private static RetryLaterPendingRuntimeException newPendingVerdict(final IQueuedLock lock, final String ownerName,
            final Duration acquireTimeout) {
        return new FastRetryLaterPendingRuntimeException(RetryLaterPendingRuntimeException.PENDING_PREFIX + ownerName
                + ".lockForUpdate: update of [" + lock.getName() + "] is held by another context past the "
                + acquireTimeout + " acquire timeout - retrying later instead of queueing further threads behind the"
                + " update");
    }

}
