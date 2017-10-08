package de.invesdwin.context.log.error;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.test.ATest;

@ThreadSafe
public class LoggedRuntimeExceptionTest extends ATest {

    @SuppressWarnings("serial")
    @Test
    public void testNewInstance() {
        LoggedRuntimeException.newInstance(new Exception() {
            @Override
            public StackTraceElement[] getStackTrace() {
                return null;
            }
        });
    }

}
