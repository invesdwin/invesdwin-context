package de.invesdwin.context.integration.retry.task;

import javax.annotation.concurrent.Immutable;

import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.backoff.UniformRandomBackOffPolicy;

import de.invesdwin.context.integration.retry.Retry;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@Immutable
public final class BackOffPolicies {
    private static final NoBackOffPolicy NO_BACK_OFF = new NoBackOffPolicy();

    private BackOffPolicies() {}

    public static NoBackOffPolicy noBackOff() {
        return NO_BACK_OFF;
    }

    public static BackOffPolicy fixedBackOffMillis(final long millis) {
        if (millis < 0) {
            return null;
        } else if (millis == 0) {
            return NO_BACK_OFF;
        } else {
            final FixedBackOffPolicy fixedBackOff = new FixedBackOffPolicy();
            fixedBackOff.setBackOffPeriod(millis);
            return fixedBackOff;
        }
    }

    public static BackOffPolicy fixedBackOff(final Duration duration) {
        if (duration == null) {
            return noBackOff();
        } else {
            return fixedBackOffMillis(duration.longValue(FTimeUnit.MILLISECONDS));
        }
    }

    public static BackOffPolicy fixedBackOff(final Retry annotation) {
        if (annotation != null) {
            return BackOffPolicies.fixedBackOffMillis(annotation.fixedBackOffMillis());
        } else {
            return null;
        }
    }

    public static BackOffPolicy randomFixedBackOff(final Duration maxDuration) {
        return randomFixedBackOff(Duration.ONE_MILLISECOND, maxDuration);
    }

    public static BackOffPolicy randomFixedBackOff(final Duration minDuration, final Duration maxDuration) {
        final UniformRandomBackOffPolicy randomBackOff = new UniformRandomBackOffPolicy();
        randomBackOff.setMinBackOffPeriod(minDuration.longValue(FTimeUnit.MILLISECONDS));
        randomBackOff.setMaxBackOffPeriod(maxDuration.longValue(FTimeUnit.MILLISECONDS));
        return randomBackOff;
    }

}
