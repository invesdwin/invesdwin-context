package de.invesdwin.context.jcache.internal;

import javax.annotation.concurrent.Immutable;
import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.FactoryBuilder.SingletonFactory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;

import de.invesdwin.context.jcache.CacheBuilder;
import de.invesdwin.context.jcache.CacheEntryListenerSupport;
import de.invesdwin.context.jcache.MergedExpiryPolicy;
import de.invesdwin.context.jcache.util.MemoryUnits;
import de.invesdwin.util.lang.Reflections;

@Immutable
public final class CacheBuilderInternalFactory {

    private CacheBuilderInternalFactory() {}

    @SuppressWarnings("unchecked")
    public static <K, V> Cache<K, V> newCache(final CacheBuilder<K, V> builder) {
        final MutableConfiguration<K, V> cacheConfiguration = new MutableConfiguration<K, V>();
        applyJCacheConfiguration(builder, cacheConfiguration);
        final org.ehcache.jcache.JCacheManager jCacheManager = Caching.getCachingProvider()
                .getCacheManager()
                .unwrap(org.ehcache.jcache.JCacheManager.class);
        final net.sf.ehcache.config.CacheConfiguration ehCacheConfiguration = newEhCacheConfiguration(
                builder.getName(), cacheConfiguration, jCacheManager);
        applyEhCacheConfiguration(builder, ehCacheConfiguration);
        final net.sf.ehcache.Cache ehCache = new net.sf.ehcache.Cache(ehCacheConfiguration);
        new org.ehcache.jcache.JCache<K, V>(jCacheManager, new org.ehcache.jcache.JCacheConfiguration<K, V>(
                cacheConfiguration), ehCache);
        jCacheManager.unwrap(net.sf.ehcache.CacheManager.class).addCache(ehCache);
        final org.ehcache.jcache.JCache<K, V> cache = (org.ehcache.jcache.JCache<K, V>) Caching.getCachingProvider()
                .getCacheManager()
                .getCache(builder.getName());
        return cache;
    }

    private static <K, V> net.sf.ehcache.config.CacheConfiguration newEhCacheConfiguration(final String cacheName,
            final MutableConfiguration<K, V> cacheConfiguration, final org.ehcache.jcache.JCacheManager jCacheManager) {
        return Reflections.method("toEhcacheConfig")
                .withReturnType(net.sf.ehcache.config.CacheConfiguration.class)
                .withParameterTypes(String.class, Configuration.class)
                .in(jCacheManager)
                .invoke(cacheName, cacheConfiguration);
    }

    private static <K, V> void applyJCacheConfiguration(final CacheBuilder<K, V> builder,
            final MutableConfiguration<K, V> config) {
        if (builder.getReadThrough() != null) {
            config.setReadThrough(builder.getReadThrough());
        }
        if (builder.getWriteThrough() != null) {
            config.setWriteThrough(builder.getWriteThrough());
        }
        if (builder.getStoreByValue() != null) {
            config.setStoreByValue(builder.getStoreByValue());
        }
        if (builder.getStatisticsEnabled() != null) {
            config.setStatisticsEnabled(builder.getStatisticsEnabled());
        }
        applyJCacheFactories(builder, config);
    }

    private static <K, V> void applyJCacheFactories(final CacheBuilder<K, V> builder,
            final MutableConfiguration<K, V> config) {
        if (!builder.getExpiryPolicies().isEmpty()) {
            final ExpiryPolicy expiryPolicy;
            if (builder.getExpiryPolicies().size() == 1) {
                expiryPolicy = builder.getExpiryPolicies().iterator().next();
            } else {
                expiryPolicy = new MergedExpiryPolicy(builder.getExpiryPolicies());
            }
            config.setExpiryPolicyFactory(new SingletonFactory<ExpiryPolicy>(expiryPolicy));
        }
        if (builder.getCacheLoader() != null) {
            config.setCacheLoaderFactory(new SingletonFactory<CacheLoader<K, V>>(builder.getCacheLoader()));
        }
        if (builder.getCacheWriter() != null) {
            config.setCacheWriterFactory(new SingletonFactory<CacheWriter<K, V>>(builder.getCacheWriter()));
        }
        for (final CacheEntryListenerSupport<K, V> cacheEntryListener : builder.getCacheEntryListeners()) {
            config.addCacheEntryListenerConfiguration(cacheEntryListener.newConfiguration());
        }
    }

    private static <K, V> void applyEhCacheConfiguration(final CacheBuilder<K, V> builder,
            final net.sf.ehcache.config.CacheConfiguration config) {
        config.addPersistence(new net.sf.ehcache.config.PersistenceConfiguration().strategy(net.sf.ehcache.config.PersistenceConfiguration.Strategy.NONE));
        if (builder.getMaxDiskSize() != null) {
            final net.sf.ehcache.config.MemoryUnit memoryUnit = MemoryUnits.toEhCache(builder.getMaxDiskSize()
                    .getScale());
            config.maxBytesLocalDisk(
                    builder.getMaxDiskSize().getValue(MemoryUnits.fromEhCache(memoryUnit)).longValue(), memoryUnit);
            config.getPersistenceConfiguration().strategy(
                    net.sf.ehcache.config.PersistenceConfiguration.Strategy.LOCALTEMPSWAP);
        }
        if (builder.getMaxDiskElements() != null) {
            config.setMaxEntriesLocalDisk(builder.getMaxDiskElements());
            config.getPersistenceConfiguration().strategy(
                    net.sf.ehcache.config.PersistenceConfiguration.Strategy.LOCALTEMPSWAP);
        }
        if (builder.getMaxHeapSize() != null) {
            final net.sf.ehcache.config.MemoryUnit memoryUnit = MemoryUnits.toEhCache(builder.getMaxHeapSize()
                    .getScale());
            config.maxBytesLocalHeap(
                    builder.getMaxHeapSize().getValue(MemoryUnits.fromEhCache(memoryUnit)).longValue(), memoryUnit);
        }
        if (builder.getMaxHeapElements() != null) {
            config.setMaxEntriesLocalHeap(builder.getMaxHeapElements());
        }
        config.setName(builder.getName());
    }

}
