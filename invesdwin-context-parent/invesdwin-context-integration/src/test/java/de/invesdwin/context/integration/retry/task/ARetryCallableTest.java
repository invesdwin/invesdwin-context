package de.invesdwin.context.integration.retry.task;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.Test;

import de.invesdwin.context.test.ATest;

// CHECKSTYLE:OFF
@NotThreadSafe
public class ARetryCallableTest extends ATest {

    @Test
    public void test() throws Exception {
        ARetryCallableMainTest.test();
    }

}
