package de.invesdwin.context;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public class CacheTest extends ATest {

    @Test
    public void testCacheHits() {
        final CacheAspectMethods m = new CacheAspectMethods();
        for (int i = 0; i < 10; i++) {
            Assertions.assertThat(m.hitCache()).isZero();
        }
        Assertions.assertThat(m.methodInvocations).isEqualTo(1);
        m.evictAll();
        for (int i = 0; i < 10; i++) {
            Assertions.assertThat(m.hitCache()).isEqualTo(1);
        }
        Assertions.assertThat(m.methodInvocations).isEqualTo(2);
    }

}
