package de.invesdwin.context.jcache.internal;

import java.util.OptionalLong;
import java.util.concurrent.Executor;

import javax.annotation.concurrent.Immutable;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder.SingletonFactory;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;

import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration;
import com.github.benmanes.caffeine.jcache.copy.Copier;

import de.invesdwin.context.jcache.CacheBuilder;
import de.invesdwin.context.jcache.CacheEntryListenerSupport;
import de.invesdwin.context.jcache.MergedExpiryPolicy;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.fdate.FTimeUnit;

@Immutable
public final class CacheBuilderInternalFactory {

    private static final Factory<Copier> COPIER_FACTORY = new Factory<Copier>() {
        @Override
        public Copier create() {
            return new Copier() {
                @SuppressWarnings("null")
                @Override
                public <T> T copy(final T object, final ClassLoader classLoader) {
                    return Objects.deepClone(object);
                }
            };
        }
    };
    private static final Factory<Executor> EXECUTOR_FACTORY = new Factory<Executor>() {
        @Override
        public Executor create() {
            return Executors.DISABLED_EXECUTOR;
        }
    };

    private CacheBuilderInternalFactory() {}

    public static <K, V> Cache<K, V> newCache(final CacheBuilder<K, V> builder) {
        final CaffeineConfiguration<K, V> cacheConfiguration = new CaffeineConfiguration<K, V>();
        applyConfiguration(builder, cacheConfiguration);
        final CacheManager jCacheManager = Caching.getCachingProvider().getCacheManager();
        return jCacheManager.createCache(builder.getName(), cacheConfiguration);
    }

    private static <K, V> void applyConfiguration(final CacheBuilder<K, V> builder,
            final CaffeineConfiguration<K, V> config) {
        applyJCacheConfiguration(builder, config);
        applyCaffeineConfiguration(builder, config);
        applyJCacheFactories(builder, config);
    }

    private static <K, V> void applyJCacheConfiguration(final CacheBuilder<K, V> builder,
            final CaffeineConfiguration<K, V> config) {
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
    }

    private static <K, V> void applyCaffeineConfiguration(final CacheBuilder<K, V> builder,
            final CaffeineConfiguration<K, V> config) {
        if (builder.getMaximumSize() != null) {
            config.setMaximumSize(OptionalLong.of(builder.getMaximumSize()));
        }
        if (builder.getExpireAfterWrite() != null) {
            config.setExpireAfterWrite(OptionalLong.of(builder.getExpireAfterWrite().longValue(FTimeUnit.NANOSECONDS)));
        }
        if (builder.getExpireAfterAccess() != null) {
            config.setExpireAfterAccess(
                    OptionalLong.of(builder.getExpireAfterAccess().longValue(FTimeUnit.NANOSECONDS)));
        }
        if (builder.getRefreshAfterWrite() != null) {
            config.setRefreshAfterWrite(
                    OptionalLong.of(builder.getRefreshAfterWrite().longValue(FTimeUnit.NANOSECONDS)));
        }
        config.setCopierFactory(COPIER_FACTORY);
        config.setExecutorFactory(EXECUTOR_FACTORY);
    }

    private static <K, V> void applyJCacheFactories(final CacheBuilder<K, V> builder,
            final CaffeineConfiguration<K, V> config) {
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

}
