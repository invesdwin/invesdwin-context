package de.invesdwin.context;

import javax.annotation.concurrent.ThreadSafe;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public class CacheTest extends ATest {

    private int methodInvocations;

    @Test
    public void testCacheHits() {
        final CacheAspectMethods m = new CacheAspectMethods();
        for (int i = 0; i < 10; i++) {
            Assertions.assertThat(m.hitCache()).isZero();
        }
        Assertions.assertThat(methodInvocations).isEqualTo(1);
        m.evictAll();
        for (int i = 0; i < 10; i++) {
            Assertions.assertThat(m.hitCache()).isEqualTo(1);
        }
        Assertions.assertThat(methodInvocations).isEqualTo(2);
    }

    @CacheDefaults(cacheName = ContextProperties.COMMON_CACHE_NAME)
    private class CacheAspectMethods {

        //        @Cacheable(CommonProperties.COMMON_CACHE_NAME)
        @CacheResult
        private int hitCache() {
            return methodInvocations++;
        }

        //        @CacheEvict(value = CommonProperties.COMMON_CACHE_NAME, allEntries = true)
        @CacheRemoveAll
        private void evictAll() {}

    }

}
