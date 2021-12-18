package de.invesdwin.context.jcache;

import javax.annotation.concurrent.NotThreadSafe;
import javax.cache.Cache;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

// CHECKSTYLE:OFF abstract
@NotThreadSafe
public class ACacheMapTest extends ATest {
    //CHECKSTYLE:ON

    @Test
    public void testSize() {
        final ACacheMap<Integer, String> map = new ACacheMap<Integer, String>() {
            @Override
            protected Cache<Integer, String> createDelegate() {
                return new CacheBuilder<Integer, String>().setName(ACacheMap.class.getName())
                        .setMaximumSize(1000)
                        .create();
            }
        };
        Assertions.assertThat(map.size()).isEqualTo(0);
        map.put(1, "one");
        map.put(2, "two");
        Assertions.assertThat(map.size()).isEqualTo(2);
        Assertions.assertThat(map.remove(1)).isEqualTo("one");
        Assertions.assertThat(map.size()).isEqualTo(1);
    }

}
