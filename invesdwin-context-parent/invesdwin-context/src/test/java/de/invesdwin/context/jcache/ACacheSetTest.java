package de.invesdwin.context.jcache;

import javax.annotation.concurrent.NotThreadSafe;
import javax.cache.Cache;

import org.junit.Test;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

// CHECKSTYLE:OFF abstract
@NotThreadSafe
public class ACacheSetTest extends ATest {
    //CHECKSTYLE:ON

    @Test
    public void testSize() {
        final ACacheSet<String> cacheSet = new ACacheSet<String>() {
            @Override
            protected Cache<Integer, String> createDelegate() {
                return new CacheBuilder<Integer, String>().setName(ACacheSetTest.class.getName())
                        .setMaximumSize(100)
                        .create();
            }
        };
        Assertions.assertThat(cacheSet.size()).isEqualTo(0);
        cacheSet.add("one");
        Assertions.assertThat(cacheSet.size()).isEqualTo(1);
        cacheSet.remove("one");
        Assertions.assertThat(cacheSet.size()).isEqualTo(0);
    }

}
