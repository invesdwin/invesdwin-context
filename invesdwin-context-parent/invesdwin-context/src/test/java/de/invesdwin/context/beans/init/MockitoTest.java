package de.invesdwin.context.beans.init;

import javax.annotation.concurrent.NotThreadSafe;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class MockitoTest extends ATest {

    @Inject
    @InjectMocks
    private TestBean testBean;
    @Mock
    private TestBeanToo mock;

    @Test
    public void testMockito() {
        Assertions.assertThat(mock).isSameAs(testBean.getToo());
    }
}
