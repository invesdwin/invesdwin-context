package de.invesdwin.context.integration.retry.task;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.BackOffPolicy;

import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.context.integration.retry.RetryLaterRuntimeException;
import de.invesdwin.util.assertions.Assertions;

// CHECKSTYLE:OFF
@NotThreadSafe
public class ARetryCallableMainTest {

    public static void main(final String[] args) throws Exception {
        PlatformInitializerProperties.setAllowed(false);
        test();
    }

    public static void test() {
        final MutableInt startCount = new MutableInt(0);
        final MutableInt backOffCount = new MutableInt(0);
        final Integer retries = new ARetryCallable<Integer>(new RetryOriginator(ARetryCallableMainTest.class, "main")) {

            private int tries = 0;

            @Override
            protected Integer callRetry() {
                if (tries == 0) {
                    tries++;
                    throw new RetryLaterRuntimeException("First try");
                } else if (tries == 1) {
                    tries++;
                    throw new RetryLaterRuntimeException("Second try");
                } else {
                    return tries;
                }
            }

            @Override
            protected BackOffPolicy getBackOffPolicyOverride() {
                return new BackOffPolicy() {

                    private final BackOffPolicy delegate = BackOffPolicies.noBackOff();

                    @Override
                    public BackOffContext start(final RetryContext context) {
                        startCount.increment();
                        return delegate.start(context);
                    }

                    @Override
                    public void backOff(final BackOffContext backOffContext) throws BackOffInterruptedException {
                        backOffCount.increment();
                        delegate.backOff(backOffContext);
                    }
                };
            }
        }.call();
        Assertions.assertThat(retries).isEqualTo(2);
        System.out.println("SUCCESS!");
        Assertions.assertThat(startCount.intValue()).isEqualTo(1);
        Assertions.assertThat(backOffCount.intValue()).isEqualTo(2);
    }

}
