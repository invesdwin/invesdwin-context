package de.invesdwin.context.integration.concurrent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.integration.retry.IRetryLaterException;
import de.invesdwin.context.integration.retry.RetryLaterPendingRuntimeException;
import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.lock.IQueuedLock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.time.date.millis.FDateMillis;
import de.invesdwin.util.time.duration.Duration;

/**
 * The acquire ladder of {@link UpdateLockAcquirePolicy}, pinned per branch.
 * <p>
 * These branches are what replace the unbounded {@code updateLock.lock()} fall-through that three layers used to have,
 * so the cells must show both halves: that a caller is never parked without a ceiling, and that a ceiling does not
 * turn ordinary contention into refusals (a patient caller absorbs a short hand-over; a request caller is answered a
 * pending verdict quickly). Every case uses a real second thread, because an in-process reentrant acquire would succeed
 * and prove nothing.
 */
@NotThreadSafe
public class UpdateLockAcquirePolicyTest extends ATest {

    private static final Duration REQUEST_WAIT = Duration.ONE_HUNDRED_MILLISECONDS;
    private static final Duration PATIENT_WAIT = Duration.FIVE_SECONDS;

    @Test
    public void testAnUncontendedLockIsTakenExactlyAsBefore() {
        final IQueuedLock lock = newLock("uncontended");
        Assertions.checkTrue(UpdateLockAcquirePolicy.tryLockForUpdate(lock, REQUEST_WAIT, PATIENT_WAIT, false, false,
                "test"), "an uncontended update lock must simply be taken");
        lock.unlock();
        Assertions.checkTrue(!lock.isHeldByCurrentThread(), "and it must be released again");
    }

    @Test
    public void testAnIdleRoundSkipsWhileAnotherThreadHoldsTheLock() throws InterruptedException {
        final IQueuedLock lock = newLock("idle-round");
        final Holder holder = new Holder(lock, 300);
        holder.start();
        try {
            Assertions.checkTrue(!UpdateLockAcquirePolicy.tryLockForUpdate(lock, REQUEST_WAIT, PATIENT_WAIT,
                    Boolean.TRUE, false, "test"), "an idle round takes the lock instantly or skips it: the holder is"
                            + " already writing what this round would write");
            Assertions.checkTrue(!lock.isHeldByCurrentThread(), "a skipped round must not hold the lock");
        } finally {
            holder.finish();
        }
    }

    @Test
    public void testAWarmCacheAskedByARequestCallerSkipsWithoutAVerdict() throws InterruptedException {
        final IQueuedLock lock = newLock("warm-request");
        final Holder holder = new Holder(lock, 300);
        holder.start();
        final Boolean tierToken = FinancialdataThreads.registerThreadBlockingUpdateCacheDisabled(true);
        try {
            final long startMillis = FDateMillis.nowMillis();
            Assertions.checkTrue(!UpdateLockAcquirePolicy.tryLockForUpdate(lock, REQUEST_WAIT, PATIENT_WAIT,
                    Boolean.FALSE, true, "test"), "a request-tier caller on a warm cache reads the last published"
                            + " state; it does not need this round's update to answer");
            Assertions.checkTrue(FDateMillis.nowMillis() - startMillis < 100L,
                    "and it must not wait at all for that decision");
        } finally {
            FinancialdataThreads.unregisterThreadBlockingUpdateCacheDisabled(tierToken);
            holder.finish();
        }
    }

    @Test
    public void testARequestCallerPastItsShortWaitIsRefusedAsPending() throws InterruptedException {
        final IQueuedLock lock = newLock("request-refused");
        final Holder holder = new Holder(lock, 400);
        holder.start();
        final Boolean tierToken = FinancialdataThreads.registerThreadBlockingUpdateCacheDisabled(true);
        try {
            final long startMillis = FDateMillis.nowMillis();
            Throwable thrown = null;
            try {
                UpdateLockAcquirePolicy.tryLockForUpdate(lock, REQUEST_WAIT, PATIENT_WAIT, Boolean.FALSE, false,
                        "requestCaller");
            } catch (final Throwable t) {
                thrown = t;
            }
            final long elapsedMillis = FDateMillis.nowMillis() - startMillis;
            Assertions.checkNotNull(thrown, "a request caller that cannot take the update lock must be answered");
            Assertions.checkTrue(Throwables.isCausedByType(thrown, IRetryLaterException.class),
                    "the refusal must be a retry-later verdict: " + thrown);
            Assertions.checkTrue(RetryLaterPendingRuntimeException.isPending(thrown),
                    "and it must be PENDING, so it does not consume the non-pending count the demand budgets"
                            + " exhaust on: " + thrown);
            Assertions.checkTrue(elapsedMillis >= 100L && elapsedMillis < 400L,
                    "the caller must be released at its own tier's wait, not at the holder's: "
                            + Long.valueOf(elapsedMillis) + " ms");
            Assertions.checkTrue(!lock.isHeldByCurrentThread(), "a refused caller must not hold the lock");
        } finally {
            FinancialdataThreads.unregisterThreadBlockingUpdateCacheDisabled(tierToken);
            holder.finish();
        }
    }

    @Test
    public void testAPatientCallerAbsorbsTheHandOverInsteadOfRefusing() throws InterruptedException {
        final IQueuedLock lock = newLock("patient-absorbed");
        final Holder holder = new Holder(lock, 250);
        holder.start();
        final long startMillis = FDateMillis.nowMillis();
        try {
            Assertions.checkTrue(UpdateLockAcquirePolicy.tryLockForUpdate(lock, REQUEST_WAIT, PATIENT_WAIT,
                    Boolean.FALSE, false, "test"), "an updater-class caller waits for the hand-over: refusing it would"
                            + " cost a whole re-ask cycle, which is more churn than the wait absorbs");
            //the holder sleeps 250 ms and my clock starts a few ms into that sleep, so the honest bar is the
            //request tier's 100 ms ceiling: waiting past it is what proves the patient caller absorbed the
            //hand-over rather than refusing it
            Assertions.checkTrue(FDateMillis.nowMillis() - startMillis >= 150L,
                    "a patient caller must wait past the request tier's ceiling instead of refusing");
            lock.unlock();
        } finally {
            holder.finish();
        }
    }

    @Test
    public void testANonPositiveWaitRefusesWithoutQueueing() throws InterruptedException {
        final IQueuedLock lock = newLock("zero-wait");
        final Holder holder = new Holder(lock, 400);
        holder.start();
        try {
            final long startMillis = FDateMillis.nowMillis();
            Throwable thrown = null;
            try {
                UpdateLockAcquirePolicy.tryLockForUpdate(lock, Duration.ZERO, Duration.ZERO, Boolean.FALSE, false,
                        "zeroWait");
            } catch (final Throwable t) {
                thrown = t;
            }
            Assertions.checkNotNull(thrown, "a configured non-positive wait means: never wait, refuse");
            Assertions.checkTrue(Throwables.isCausedByType(thrown, IRetryLaterException.class), "and the refusal is a"
                    + " retry-later verdict rather than a parked thread: " + thrown);
            Assertions.checkTrue(FDateMillis.nowMillis() - startMillis < 100L,
                    "with a zero wait the caller must not queue at all");
        } finally {
            holder.finish();
        }
    }

    @Test
    public void testAThreadTheRetryMachineryRefusesNeverTouchesTheLock() {
        final IQueuedLock lock = newLock("retry-disabled-thread");
        final AtomicBoolean skipped = new AtomicBoolean();
        final AtomicInteger thrown = new AtomicInteger();
        final Thread caller = new Thread(() -> {
            Thread.currentThread().interrupt();
            try {
                skipped.set(!UpdateLockAcquirePolicy.tryLockForUpdate(lock, REQUEST_WAIT, PATIENT_WAIT, Boolean.FALSE,
                        false, "test"));
            } catch (final Throwable t) {
                thrown.incrementAndGet();
            }
        }, "UpdateLockAcquirePolicyTest-retryDisabled");
        caller.start();
        try {
            caller.join(10000L);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError("interrupted while waiting for the caller thread", e);
        }
        Assertions.checkTrue(skipped.get(), "a thread the retry machinery refuses must skip the round");
        Assertions.checkTrue(thrown.get() == 0, "and skipping must not raise a verdict");
        Assertions.checkTrue(!lock.isHeldByCurrentThread(), "the lock must be untouched");
    }

    private static IQueuedLock newLock(final String name) {
        return Locks.newReentrantReadWriteLock("UpdateLockAcquirePolicyTest_" + name).writeLock();
    }

    /** A real second thread holding the lock for a fixed time - an in-process acquire would be reentrant. */
    private static final class Holder {

        private final IQueuedLock lock;
        private final int holdMillis;
        private final Thread thread;
        private final AtomicBoolean held = new AtomicBoolean();

        private Holder(final IQueuedLock lock, final int holdMillis) {
            this.lock = lock;
            this.holdMillis = holdMillis;
            this.thread = new Thread(() -> {
                lock.lock();
                try {
                    held.set(true);
                    Thread.sleep(holdMillis);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }, "UpdateLockAcquirePolicyTest-holder");
        }

        private void start() throws InterruptedException {
            thread.start();
            final long deadline = FDateMillis.nowMillis() + 5000L;
            while (!held.get() && FDateMillis.nowMillis() < deadline) {
                Thread.sleep(5L);
            }
            Assertions.checkTrue(held.get(), "precondition: the holder thread must own the lock before the caller asks");
        }

        private void finish() throws InterruptedException {
            thread.join(10000L);
        }
    }
}
