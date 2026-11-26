package de.invesdwin.context.integration.network.request;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.integration.retry.IRetryDisabledException;
import de.invesdwin.context.integration.retry.IRetryLaterException;
import de.invesdwin.context.integration.retry.Retries;
import de.invesdwin.context.integration.retry.RetryDisabledRuntimeException;
import de.invesdwin.context.integration.retry.RetryLaterRuntimeException;
import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.error.Throwables;

/**
 * The refreshing request wrapper used to label every failure of the delegate as retry-later, which made the retry
 * engine loop on failures that will never pass: measured through a personal data source, 26 re-reads of one
 * immutable classpath fixture in 12 minutes, a minute of back off per attempt, and the ask never answers.
 * <p>
 * A retried body must either declare its verdict or have it classified by the same policy the engine uses. These
 * cells pin both halves: a permanent failure stops, and the genuinely transient ones keep being retried - which is
 * the whole point of the wrapper and must not be lost by the fix.
 */
@NotThreadSafe
public class DelegateDailyRefreshingRequestVerdictTest extends ATest {

    @Test
    public void testAPermanentFailureIsNotTurnedIntoAnUnboundedRetryLoop() {
        final AtomicInteger attempts = new AtomicInteger();
        final DelegateDailyRefreshingRequest<String> wrapper = new DelegateDailyRefreshingRequest<String>(() -> {
            attempts.incrementAndGet();
            throw new IllegalStateException("the fixture header does not match the mapper");
        });

        Throwable thrown = null;
        try {
            wrapper.call();
        } catch (final Throwable t) {
            thrown = t;
        }
        Assertions.checkNotNull(thrown, "the wrapper must report the failure");
        Assertions.checkTrue(!Retries.shouldRetry(thrown),
                "an undeclared, non-transient failure must not be classified as a wait: " + thrown);
        Assertions.checkTrue(Throwables.isCausedByType(thrown, IRetryDisabledException.class),
                "and it must carry the not-retryable marker so the next layer can see it: " + thrown);
        Assertions.assertThat(attempts.get())
                .as("the delegate must be asked once - every extra attempt re-reads the same immutable input")
                .isEqualTo(1);
    }

    @Test
    public void testADeclaredRetryDisabledVerdictIsNotRelabelledAsAWait() {
        final AtomicInteger attempts = new AtomicInteger();
        final DelegateDailyRefreshingRequest<String> wrapper = new DelegateDailyRefreshingRequest<String>(() -> {
            attempts.incrementAndGet();
            throw new RetryDisabledRuntimeException("this instrument is not offered by the source");
        });

        Throwable thrown = null;
        try {
            wrapper.call();
        } catch (final Throwable t) {
            thrown = t;
        }
        Assertions.checkNotNull(thrown, "the wrapper must report the refusal");
        Assertions.checkTrue(Throwables.isCausedByType(thrown, IRetryDisabledException.class),
                "the producer's marker must survive the wrapping: " + thrown);
        Assertions.checkTrue(Throwables.getCauseByType(thrown, IRetryLaterException.class) == null,
                "a refusal that gets re-labelled as retry-later is indistinguishable from a wait for every caller "
                        + "that classifies on the marker: " + thrown);
        Assertions.assertThat(attempts.get())
                .as("a declared refusal must end the retry sequence, not extend it")
                .isEqualTo(1);
    }

    @Test
    public void testATransientFailureIsStillRetriedUntilItSucceeds() {
        final AtomicInteger attempts = new AtomicInteger();
        final DelegateDailyRefreshingRequest<String> wrapper = new DelegateDailyRefreshingRequest<String>(() -> {
            if (attempts.incrementAndGet() < 3) {
                throw new RetryLaterRuntimeException("destination not reachable");
            }
            return "ok";
        });
        Assertions.assertThat(wrapper.call()).as("the declared wait must still be honoured").isEqualTo("ok");
        Assertions.assertThat(attempts.get())
                .as("two retry-later verdicts and then the success - the fix must not shorten this path")
                .isEqualTo(3);
    }

    @Test
    public void testAConnectionStyleFailureIsStillClassifiedAsRetryable() {
        final AtomicInteger attempts = new AtomicInteger();
        final DelegateDailyRefreshingRequest<String> wrapper = new DelegateDailyRefreshingRequest<String>(() -> {
            if (attempts.incrementAndGet() < 3) {
                throw new UncheckedIOException(new IOException("socket closed while reading"));
            }
            return "ok";
        });
        Assertions.assertThat(wrapper.call())
                .as("an IOException chain is on the retryable list, so classifying it must not refuse it")
                .isEqualTo("ok");
        Assertions.assertThat(attempts.get()).isEqualTo(3);
    }

}
