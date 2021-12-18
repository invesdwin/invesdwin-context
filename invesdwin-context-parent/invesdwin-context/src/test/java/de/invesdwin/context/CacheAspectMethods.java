package de.invesdwin.context;

import javax.annotation.concurrent.NotThreadSafe;
import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;

@NotThreadSafe
@CacheDefaults(cacheName = ContextProperties.DEFAULT_CACHE_NAME)
public class CacheAspectMethods {

    //CHECKSTYLE:OFF
    public int methodInvocations;
    //CHECKSTYLE:ON

    //        @Cacheable(CommonProperties.COMMON_CACHE_NAME)
    @CacheResult
    public int hitCache() {
        return methodInvocations++;
    }

    //        @CacheEvict(value = CommonProperties.COMMON_CACHE_NAME, allEntries = true)
    @CacheRemoveAll
    public void evictAll() {
    }

}