package de.invesdwin.context.beans.init.internal;

import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.NotThreadSafe;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.springframework.cache.jcache.JCacheCacheManager;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Reflections;

@NotThreadSafe
public class ConfiguredJCacheCacheManager extends JCacheCacheManager {

    public ConfiguredJCacheCacheManager(final net.sf.ehcache.CacheManager cacheManager) {
        final org.ehcache.jcache.JCacheCachingProvider jCacheCachingProvider = (org.ehcache.jcache.JCacheCachingProvider) Caching.getCachingProvider();
        cacheManager.getConfiguration().setClassLoader(jCacheCachingProvider.getDefaultClassLoader());
        setCacheManager(new org.ehcache.jcache.JCacheManager(jCacheCachingProvider, cacheManager,
                jCacheCachingProvider.getDefaultURI(), new Properties()));
    }

    @Override
    public void afterPropertiesSet() {
        Assertions.assertThat(getCache(ContextProperties.COMMON_CACHE_NAME)).isNotNull(); //load default cache
        super.afterPropertiesSet();
        hackDefaultRegisterCacheManager();
    }

    /**
     * Without this the custom created cache manager is not found next time and another one gets created as the default
     * cache manager with a failsafe configuration!
     */
    @SuppressWarnings("unchecked")
    private void hackDefaultRegisterCacheManager() {
        final CachingProvider cachingProvider = Caching.getCachingProvider();
        final Map<ClassLoader, ConcurrentMap<URI, org.ehcache.jcache.JCacheManager>> cacheManagers = Reflections.field(
                "cacheManagers")
                .ofType(Map.class)
                .in(cachingProvider)
                .get();
        synchronized (cacheManagers) {
            final ClassLoader classLoader = cachingProvider.getDefaultClassLoader();
            final ConcurrentMap<URI, org.ehcache.jcache.JCacheManager> map = new ConcurrentHashMap<URI, org.ehcache.jcache.JCacheManager>();
            Assertions.assertThat(cacheManagers.put(classLoader, map)).isNull();
            final org.ehcache.jcache.JCacheManager cacheManager = (org.ehcache.jcache.JCacheManager) getCacheManager();
            Assertions.assertThat(map.put(cacheManager.getURI(), cacheManager)).isNull();
        }
    }
}
