package de.invesdwin.context.log.internal;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public class FormattedDescriptionTest extends ATest {

    @Test
    public void testValue() {
        try {
            Assertions.assertThat(false).as("[%s] asdf %s", 1, 2).isTrue();
        } catch (final AssertionError e) {
            Assertions.assertThat(e.getMessage()).contains("[1] asdf 2");
        }
    }

}
