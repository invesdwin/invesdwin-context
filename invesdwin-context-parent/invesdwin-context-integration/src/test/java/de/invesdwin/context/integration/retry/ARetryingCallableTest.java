package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.beans.init.InvesdwinInitializationProperties;
import de.invesdwin.util.assertions.Assertions;

// CHECKSTYLE:OFF
@NotThreadSafe
public class ARetryingCallableTest {

    public static void main(final String[] args) throws Exception {
        InvesdwinInitializationProperties.setInvesdwinInitializationAllowed(false);
        final Integer retries = new ARetryingCallable<Integer>(
                new RetryOriginator(ARetryingCallableTest.class, "main")) {

            private int tries = 0;

            @Override
            protected Integer callRetryable() {
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
        }.call();
        Assertions.assertThat(retries).isEqualTo(2);
        System.out.println("SUCCESS!");
    }

}
