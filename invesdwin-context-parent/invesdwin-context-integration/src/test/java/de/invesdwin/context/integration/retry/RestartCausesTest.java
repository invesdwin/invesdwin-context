package de.invesdwin.context.integration.retry;

import java.nio.channels.OverlappingFileLockException;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.integration.retry.fast.FastNonBlockingRetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.fast.FastRetryLaterException;
import de.invesdwin.context.integration.retry.fast.FastRetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.task.RetryOriginator;
import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

/**
 * A RetryLater means 'ask again later', so it must never be remembered as the cause to restart a component on - not
 * even when it carries a failure that would justify a restart on its own. Only a hard failure may, and the skip of a
 * cycle is reported once per seam instead of once per retry.
 */
@NotThreadSafe
public class RestartCausesTest extends ATest {

    @Test
    public void theRetryLaterFamilyIsASkippedCycleAndNeverARestartCause() {
        //the retry hook wraps the original failure, so a RetryLater carrying a hard failure is what reaches the hook
        final OverlappingFileLockException lockFailure = new OverlappingFileLockException();
        final Throwable[] retryLaterFamily = { new RetryLaterRuntimeException(lockFailure),
                new RetryLaterException(lockFailure), new NonBlockingRetryLaterRuntimeException(lockFailure),
                new RetryLaterPendingRuntimeException(
                        RetryLaterPendingRuntimeException.PENDING_PREFIX + "downloading", lockFailure),
                new FastRetryLaterRuntimeException(lockFailure), new FastRetryLaterException(lockFailure),
                new FastNonBlockingRetryLaterRuntimeException(lockFailure) };
        for (final Throwable retryLater : retryLaterFamily) {
            Assertions.checkTrue(RestartCauses.isRetryLater(retryLater),
                    "[%s] must be recognised as the retryLater family", retryLater.getClass().getSimpleName());
            Assertions.checkFalse(RestartCauses.isRestartCause(retryLater),
                    "[%s] must never restart the component", retryLater.getClass().getSimpleName());
        }
        //a retryLater that the retry machinery wrapped in a plain exception is still a retryLater
        final Throwable wrappedRetryLater = new RuntimeException("handler failed",
                new FastRetryLaterRuntimeException(lockFailure));
        Assertions.checkTrue(RestartCauses.isRetryLater(wrappedRetryLater),
                "a retryLater deeper in the cause chain must still be recognised");
        Assertions.checkFalse(RestartCauses.isRestartCause(wrappedRetryLater),
                "a retryLater deeper in the cause chain must still not restart the component");
    }

    @Test
    public void hardFailuresKeepTheRestartBehaviour() {
        final OverlappingFileLockException lockFailure = new OverlappingFileLockException();
        Assertions.checkFalse(RestartCauses.isRetryLater(lockFailure),
                "an overlapping file lock is not retryable by itself");
        Assertions.checkTrue(RestartCauses.isRestartCause(lockFailure),
                "an overlapping file lock still has to restart the component");
        //RetryDisabled is the opposite marker: do not retry, which makes it a hard failure too
        final Throwable retryDisabled = new RetryDisabledRuntimeException("retry disabled", lockFailure);
        Assertions.checkFalse(RestartCauses.isRetryLater(retryDisabled),
                "a disabled retry is not the retryLater family");
        Assertions.checkTrue(RestartCauses.isRestartCause(retryDisabled),
                "a hard failure under a disabled retry still restarts the component");
        //and a cause that is neither leaves the component alone, exactly as before
        Assertions.checkFalse(RestartCauses.isRestartCause(new IllegalStateException("unrelated")));
    }

    @Test
    public void aSkippedCycleIsWarnedAboutOncePerSeam() {
        final RetryOriginator originator = new RetryOriginator(RestartCausesTest.class, "runStrategy");
        final Throwable retryLater = new NonBlockingRetryLaterRuntimeException("all permits are busy");
        Assertions.checkTrue(RestartCauses.logSkippedCycle("test-seam-first", originator, 1, retryLater),
                "the first skipped cycle of a seam has to be warned about");
        Assertions.checkFalse(RestartCauses.logSkippedCycle("test-seam-first", originator, 2, retryLater),
                "the retries of the same skipped cycle must not spam the log");
        Assertions.checkTrue(RestartCauses.logSkippedCycle("test-seam-other", originator, 1, retryLater),
                "the other seam has its own WARN budget");
    }

}
