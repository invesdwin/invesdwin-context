package de.invesdwin.context.integration.retry.internal;

import javax.annotation.concurrent.Immutable;

import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;

import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@Immutable
public final class ExceptionCauseBackOffPolicy implements BackOffPolicy {

    public static final String ATTRIBUTE_BACK_OFF_POLICY_OVERRIDE = "ATTRIBUTE_BACK_OFF_POLICY_OVERRIDE";

    public static final ExceptionCauseBackOffPolicy INSTANCE = new ExceptionCauseBackOffPolicy();

    private final ExponentialBackOffPolicy defaultBackOffPolicy;

    private ExceptionCauseBackOffPolicy() {
        defaultBackOffPolicy = new ExponentialBackOffPolicy();
        defaultBackOffPolicy.setInitialInterval(new Duration(1, FTimeUnit.SECONDS).longValue(FTimeUnit.MILLISECONDS));
        defaultBackOffPolicy.setMaxInterval(new Duration(1, FTimeUnit.MINUTES).longValue(FTimeUnit.MILLISECONDS));
    }

    @Override
    public BackOffContext start(final RetryContext context) {
        final BackOffPolicy backOffPolicyOverride = (BackOffPolicy) context
                .getAttribute(ATTRIBUTE_BACK_OFF_POLICY_OVERRIDE);
        if (backOffPolicyOverride != null) {
            return new OverrideBackOffContext(backOffPolicyOverride.start(context), backOffPolicyOverride);
        } else {
            return defaultBackOffPolicy.start(context);
        }
    }

    @Override
    public void backOff(final BackOffContext backOffContext) throws BackOffInterruptedException {
        if (backOffContext instanceof OverrideBackOffContext) {
            final OverrideBackOffContext cBackOffContext = (OverrideBackOffContext) backOffContext;
            final BackOffPolicy overrideBackOffPolicy = cBackOffContext.getBackOffPolicy();
            final BackOffContext overrideBackOffContext = cBackOffContext.getBackOffContext();
            overrideBackOffPolicy.backOff(overrideBackOffContext);
        } else {
            defaultBackOffPolicy.backOff(backOffContext);
        }
    }

    private static final class OverrideBackOffContext implements BackOffContext {

        private final BackOffContext backOffContext;
        private final BackOffPolicy backOffPolicy;

        private OverrideBackOffContext(final BackOffContext backOffContext, final BackOffPolicy backOffPolicy) {
            this.backOffContext = backOffContext;
            this.backOffPolicy = backOffPolicy;
        }

        public BackOffContext getBackOffContext() {
            return backOffContext;
        }

        public BackOffPolicy getBackOffPolicy() {
            return backOffPolicy;
        }
    }

}
