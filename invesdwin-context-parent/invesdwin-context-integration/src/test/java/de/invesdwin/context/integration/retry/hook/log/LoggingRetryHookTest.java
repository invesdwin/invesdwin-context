package de.invesdwin.context.integration.retry.hook.log;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.integration.retry.NonBlockingRetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.RestartCauses;
import de.invesdwin.context.integration.retry.RetryLaterPendingRuntimeException;
import de.invesdwin.context.integration.retry.RetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.fast.FastNonBlockingRetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.fast.FastRetryLaterPendingRuntimeException;
import de.invesdwin.context.integration.retry.fast.FastRetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.task.RetryOriginator;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.reference.IMutableReference;
import de.invesdwin.util.concurrent.reference.ThreadLocalReference;
import de.invesdwin.util.time.Instant;

/**
 * Pins the marker that decides the report shape of a logged retry attempt: the stack-trace-free Fast* variants carry
 * IHiddenException, which is the producer's declaration that this failure is a skip verdict and not a fault, so it
 * gets one line. A plain RetryLater makes no such claim and keeps its ERROR record with the stack. Measured on
 * ptstaging with the 19:09 build: 547 retry chains (546 of them resolved after exactly one retry) filled 18,496 of
 * error.log's 21,903 lines and 23,392 of common.log's 44,356 with stack frames.
 */
@NotThreadSafe
public class LoggingRetryHookTest {

    @Test
    public void testHiddenFastVerdictsGetOneLine() {
        //the form the update-lock producer now throws
        final FastNonBlockingRetryLaterRuntimeException fastNonBlocking = new FastNonBlockingRetryLaterRuntimeException(
                "ALiveCalculatedBarCache.lockForUpdate: update of X._updateLock is held by another context past the"
                        + " PT5S acquire timeout - retrying later instead of queueing request threads behind the update");
        Assertions.assertThat(LoggingRetryHook.isHiddenVerdict(fastNonBlocking)).isTrue();
        //the pending mirror keeps the subtype, so the retry budget still counts the wait as pending
        final FastRetryLaterPendingRuntimeException fastPending = new FastRetryLaterPendingRuntimeException(
                RetryLaterPendingRuntimeException.PENDING_PREFIX + "Strategy is initializing");
        Assertions.assertThat(LoggingRetryHook.isHiddenVerdict(fastPending)).isTrue();
        Assertions.assertThat(RetryLaterPendingRuntimeException.isPending(fastPending)).isTrue();
        //the shape the idle rounds actually have: Futures.get rethrows the joined async verdict as a plain
        //RuntimeException, so the marker must be found in the cause chain and not in the top type
        Assertions.assertThat(LoggingRetryHook.isHiddenVerdict(new RuntimeException(fastNonBlocking))).isTrue();
    }

    /**
     * The marker is the Fast form itself, so the stack-free declaration and the hidden decision cannot drift apart -
     * and the consequence is that a Fast form must never wrap a genuine fault, because honouring the marker hides
     * that fault's frames too.
     */
    @Test
    public void testHiddenFollowsTheFastFormNotTheSemanticType() {
        Assertions.assertThat(LoggingRetryHook
                .isHiddenVerdict(new FastRetryLaterRuntimeException("Dispatch timeout", new IOException("fault"))))
                        .isTrue();
        Assertions.assertThat(LoggingRetryHook.isHiddenVerdict(
                new NonBlockingRetryLaterRuntimeException("update lock held by another context"))).isFalse();
        Assertions.assertThat(LoggingRetryHook
                .isHiddenVerdict(new RetryLaterPendingRuntimeException("[PENDING] Strategy is initializing"))).isFalse();
    }

    @Test
    public void testPlainRetryLatersAndFaultsKeepTheirRecord() {
        //"retry me" is not a claim of "not a fault": these keep the ERROR record with the stack
        Assertions.assertThat(LoggingRetryHook.isHiddenVerdict(
                new RetryLaterRuntimeException("Waiting for at least one node to be available"))).isFalse();
        Assertions.assertThat(LoggingRetryHook.isHiddenVerdict(new IOException("download failed"))).isFalse();
        Assertions.assertThat(LoggingRetryHook.isHiddenVerdict(new IllegalStateException("state"))).isFalse();
        Assertions.assertThat(LoggingRetryHook.isHiddenVerdict(
                new RuntimeException(new IOException("wrapped fault")))).isFalse();
        Assertions.assertThat(LoggingRetryHook.isHiddenVerdict(null)).isFalse();
    }

    /**
     * The hidden branch must stay side-effect free beyond the one log line (no error record): a test harness
     * reports an Err.process during a test as a failure, so reaching this branch without one is itself the check.
     */
    @Test
    public void testHiddenVerdictRetryLogsOneLineWithoutAnErrorRecord() {
        LoggingRetryHook.logRetry(new RetryOriginator(LoggingRetryHookTest.class, "someMethod", null), 1,
                new FastNonBlockingRetryLaterRuntimeException("update lock held by another context"),
                LoggingReason.INITIAL, new Instant());
    }

    /**
     * A productive wait that is also declared not a fault is silent until the chain says otherwise. The classification
     * needs BOTH markers: a hidden verdict that is not pending (the non-blocking refusal) and a pending wait that no
     * producer declared harmless (the plain pending type) are still reported.
     */
    @Test
    public void testProductiveWaitSkipNeedsBothMarkers() {
        final FastRetryLaterPendingRuntimeException pending = new FastRetryLaterPendingRuntimeException(
                RetryLaterPendingRuntimeException.PENDING_PREFIX + "update of X._updateLock is held by another context");
        Assertions.assertThat(LoggingRetryHook.isProductiveWaitSkip(pending)).isTrue();
        Assertions.assertThat(LoggingRetryHook.isProductiveWaitSkip(new RuntimeException(pending))).isTrue();
        Assertions.assertThat(LoggingRetryHook
                .isProductiveWaitSkip(new FastNonBlockingRetryLaterRuntimeException("update lock held, retry later")))
                        .isFalse();
        Assertions.assertThat(LoggingRetryHook
                .isProductiveWaitSkip(new RetryLaterPendingRuntimeException("[PENDING] hidden is missing"))).isFalse();
        Assertions.assertThat(LoggingRetryHook.isProductiveWaitSkip(null)).isFalse();
    }

    /**
     * The silent shape of a routine skip, end to end: no report below the stall threshold, and because nothing was
     * reported the chain also gets no terminal line. Above the threshold the skip is a stall and must be named, and
     * then the terminal bracket comes back. The report state is observable through the chain's own counter, which is
     * what the terminal reports read.
     */
    @Test
    public void testRoutineSkipStaysSilentToItsEndAndAStallDoesNot() {
        final RetryOriginator originator = new RetryOriginator(LoggingRetryHookTest.class, "determineLastTickTimeTry",
                null);
        final IMutableReference<LoggingPreviousCause> holder = new ThreadLocalReference<>();
        final FastRetryLaterPendingRuntimeException pending = new FastRetryLaterPendingRuntimeException(
                RetryLaterPendingRuntimeException.PENDING_PREFIX + "update of X._updateLock is held by another context");

        LoggingRetryHook.onBeforeRetry(holder, originator, 0, pending);
        Assertions.checkFalse(holder.get().hasReported(),
                "the first attempt of a productive wait must not be reported");
        LoggingRetryHook.onRetrySucceeded(holder, originator, 3);
        Assertions.checkNull(holder.get(), "a chain that reported nothing must also stay silent at its end");

        //same seam, but this time the wait has become a stall: reported, and the terminal line follows the report
        LoggingRetryHook.onBeforeRetry(holder, originator, LoggingRetryHook.ROUTINE_SKIP_REPORT_RETRIES, pending);
        LoggingRetryHook.onBeforeRetry(holder, originator, LoggingRetryHook.ROUTINE_SKIP_REPORT_RETRIES + 1,
                new FastRetryLaterPendingRuntimeException(RetryLaterPendingRuntimeException.PENDING_PREFIX
                        + "update of Y._updateLock is held by another context"));
        Assertions.checkTrue(holder.get().hasReported(),
                "at the stall threshold the skip must be named even though it stays a one-liner");
        LoggingRetryHook.onRetrySucceeded(holder, originator, LoggingRetryHook.ROUTINE_SKIP_REPORT_RETRIES + 1);
    }

    /**
     * The live source services' skipped-cycle report obeys the same rule, so one hot key stops producing five records
     * per demand cycle (the retry hook plus the four live source services on ptstaging).
     */
    @Test
    public void testSkippedCycleReportFollowsTheSameRule() {
        final RetryOriginator originator = new RetryOriginator(LoggingRetryHookTest.class, "determineLastTickTimeTry",
                null);
        final FastRetryLaterPendingRuntimeException pending = new FastRetryLaterPendingRuntimeException(
                RetryLaterPendingRuntimeException.PENDING_PREFIX + "update of X._updateLock is held by another context");
        final RuntimeException chain = new RuntimeException("At: FinancialdataLiveTickCache[X]", pending);

        Assertions.checkFalse(RestartCauses.logSkippedCycle("testSeamRoutineSkip", originator, 0, chain),
                "a routine productive wait must not write a skipped-cycle WARN per seam and minute");
        Assertions.checkTrue(
                RestartCauses.logSkippedCycle("testSeamStallSkip", originator,
                        LoggingRetryHook.ROUTINE_SKIP_REPORT_RETRIES, chain),
                "a productive wait at the stall threshold still gets its one line");
    }

}
