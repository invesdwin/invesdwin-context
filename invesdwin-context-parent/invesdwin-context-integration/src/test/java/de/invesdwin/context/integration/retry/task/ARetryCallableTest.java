package de.invesdwin.context.integration.retry.task;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.context.integration.retry.RetryLaterRuntimeException;
import de.invesdwin.util.assertions.Assertions;

// CHECKSTYLE:OFF
@NotThreadSafe
public class ARetryCallableTest {

    public static void main(final String[] args) throws Exception {
        PlatformInitializerProperties.setAllowed(false);
        final Integer retries = new ARetryCallable<Integer>(new RetryOriginator(ARetryCallableTest.class, "main")) {

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
        }.call();
        Assertions.assertThat(retries).isEqualTo(2);
        System.out.println("SUCCESS!");
    }

}
