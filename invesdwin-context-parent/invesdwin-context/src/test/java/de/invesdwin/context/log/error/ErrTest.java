package de.invesdwin.context.log.error;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.Test;
import org.junit.jupiter.api.function.Executable;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public class ErrTest extends ATest {

    @Test
    public void testProcess() {
        final LoggedRuntimeException le = Err.process(new Exception("testProcess"));
        Assertions.assertThat(le.getId()).isGreaterThan(0);
        Assertions.assertThat(le.getIdTrace()).isNotNull();
    }

    @Test
    public void testProcessAndThrow() {
        Assertions.assertThrows(LoggedRuntimeException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                throw Err.process(new Exception("testProcessAndThrow"));
            }
        });
    }

    @Test
    public void testLoggingOfLoggedException() {
        final LoggedRuntimeException le_inner = Err.process(new Exception("testLoggingOfLoggedException_inner"));
        Assertions.assertThat(le_inner).isSameAs(Err.process(le_inner));
        final LoggedRuntimeException le_outer = Err
                .process(new Exception("testLoggingOfLoggedException_outer", le_inner));
        Assertions.assertThat(le_outer.getId()).isGreaterThan(0);
        Assertions.assertThat(le_outer.getIdTrace()).isNotNull();
        Assertions.assertThat(le_outer.getIdTrace()).contains("->");
    }

}
