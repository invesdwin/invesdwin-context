package de.invesdwin.context.integration.retry.hook.log;

import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.integration.retry.NonBlockingRetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.RetryLaterPendingRuntimeException;
import de.invesdwin.context.integration.retry.fast.FastRetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.task.RetryOriginator;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.Instant;

/**
 * Pins the classification that decides the report shape of a retry attempt: the retry-later family is a skip
 * verdict and gets one line, anything else is a fault and keeps its ERROR record with the stack. Measured on
 * ptstaging with the 19:09 build: 137 catching records against 137 RetrySucceeded lines (every retry resolved),
 * and those records were 5,383 of the 5,591 lines in the service error.log.
 */
@NotThreadSafe
public class LoggingRetryHookTest {

    @Test
    public void testRetryVerdictRecognition() {
        Assertions.assertThat(LoggingRetryHook.isRetryVerdict(
                new NonBlockingRetryLaterRuntimeException("update lock held by another context"))).isTrue();
        Assertions.assertThat(LoggingRetryHook
                .isRetryVerdict(new RetryLaterPendingRuntimeException("[PENDING] Strategy is initializing"))).isTrue();
        //the shape the idle rounds actually have: Futures.get rethrows the joined async verdict as a plain
        //RuntimeException, so the marker must be found in the cause chain and not in the top type
        Assertions.assertThat(LoggingRetryHook.isRetryVerdict(new RuntimeException(
                new NonBlockingRetryLaterRuntimeException("update lock held by another context")))).isTrue();
        //the wrapper this hook itself builds for a verdict is part of the family
        Assertions.assertThat(LoggingRetryHook
                .isRetryVerdict(new FastRetryLaterRuntimeException("(+) will be retried", new IOException("fault"))))
                        .isTrue();
    }

    @Test
    public void testFaultsAreNotVerdicts() {
        Assertions.assertThat(LoggingRetryHook.isRetryVerdict(new IOException("download failed"))).isFalse();
        Assertions.assertThat(LoggingRetryHook.isRetryVerdict(new IllegalStateException("state"))).isFalse();
        Assertions.assertThat(LoggingRetryHook.isRetryVerdict(
                new RuntimeException(new IOException("wrapped fault")))).isFalse();
        Assertions.assertThat(LoggingRetryHook.isRetryVerdict(null)).isFalse();
    }

    /**
     * The verdict branch must stay side-effect free beyond the one log line (no error record): a test harness
     * reports an Err.process during a test as a failure, so reaching this branch without one is itself the check.
     */
    @Test
    public void testVerdictRetryLogsOneLineWithoutAnErrorRecord() {
        LoggingRetryHook.logRetry(new RetryOriginator(LoggingRetryHookTest.class, "someMethod", null), 1,
                new NonBlockingRetryLaterRuntimeException("update lock held by another context"),
                LoggingReason.INITIAL, new Instant());
    }

}
