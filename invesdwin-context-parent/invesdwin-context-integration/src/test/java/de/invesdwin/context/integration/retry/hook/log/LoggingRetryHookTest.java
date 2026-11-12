package de.invesdwin.context.integration.retry.hook.log;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.integration.retry.NonBlockingRetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.RetryLaterPendingRuntimeException;
import de.invesdwin.context.integration.retry.RetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.fast.FastNonBlockingRetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.fast.FastRetryLaterPendingRuntimeException;
import de.invesdwin.context.integration.retry.fast.FastRetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.task.RetryOriginator;
import de.invesdwin.util.assertions.Assertions;
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

}
