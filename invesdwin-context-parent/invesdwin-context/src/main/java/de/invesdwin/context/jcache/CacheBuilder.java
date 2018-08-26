package de.invesdwin.context.jcache;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.cache.Cache;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;

import de.invesdwin.context.jcache.internal.CacheBuilderInternalFactory;
import de.invesdwin.util.bean.AValueObject;
import de.invesdwin.util.lang.UniqueNameGenerator;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class CacheBuilder<K, V> extends AValueObject {

    private static final UniqueNameGenerator UNIQUE_NAME_GENERATOR = new UniqueNameGenerator();

    //jCacheConfig
    private Boolean readThrough;
    private Boolean writeThrough;
    private Boolean storeByValue;
    private Boolean statisticsEnabled;
    private final Set<ExpiryPolicy> expiryPolicies = new LinkedHashSet<ExpiryPolicy>();
    private CacheLoader<K, V> cacheLoader;
    private CacheWriter<K, V> cacheWriter;
    private final Set<CacheEntryListenerSupport<K, V>> cacheEntryListeners = new LinkedHashSet<CacheEntryListenerSupport<K, V>>();

    //caffeine
    private String name;
    private Integer maximumSize;
    private Duration expireAfterWrite;
    private Duration expireAfterAccess;
    private Duration refreshAfterWrite;

    public CacheBuilder() {}

    //---------------------------------- jCacheConfig -----------------------------------//

    public Boolean getReadThrough() {
        return readThrough;
    }

    public CacheBuilder<K, V> withReadThrough(final Boolean readThrough) {
        this.readThrough = readThrough;
        return this;
    }

    public Boolean getWriteThrough() {
        return writeThrough;
    }

    public CacheBuilder<K, V> withWriteThrough(final Boolean writeThrough) {
        this.writeThrough = writeThrough;
        return this;
    }

    public Boolean getStoreByValue() {
        return storeByValue;
    }

    public CacheBuilder<K, V> withStoreByValue(final Boolean storeByValue) {
        this.storeByValue = storeByValue;
        return this;
    }

    public Boolean getStatisticsEnabled() {
        return statisticsEnabled;
    }

    public CacheBuilder<K, V> withStatisticsEnabled(final Boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
        return this;
    }

    public Set<ExpiryPolicy> getExpiryPolicies() {
        return Collections.unmodifiableSet(expiryPolicies);
    }

    public CacheBuilder<K, V> withExpiryPolicy(final ExpiryPolicy expiryPolicy) {
        this.expiryPolicies.add(expiryPolicy);
        return this;
    }

    public CacheLoader<K, V> getCacheLoader() {
        return cacheLoader;
    }

    public CacheBuilder<K, V> withCacheLoader(final CacheLoader<K, V> cacheLoader) {
        this.cacheLoader = cacheLoader;
        return this;
    }

    public CacheWriter<K, V> getCacheWriter() {
        return cacheWriter;
    }

    public CacheBuilder<K, V> withCacheWriter(final CacheWriter<K, V> cacheWriter) {
        this.cacheWriter = cacheWriter;
        return this;
    }

    public Set<CacheEntryListenerSupport<K, V>> getCacheEntryListeners() {
        return Collections.unmodifiableSet(cacheEntryListeners);
    }

    public CacheBuilder<K, V> withCacheEntryListener(final CacheEntryListenerSupport<K, V> cacheEntryListener) {
        this.cacheEntryListeners.add(cacheEntryListener);
        return this;
    }

    //---------------------------------- caffeine -----------------------------------//

    public String getName() {
        return name;
    }

    public CacheBuilder<K, V> withName(final String name) {
        this.name = UNIQUE_NAME_GENERATOR.get(name);
        return this;
    }

    public CacheBuilder<K, V> withName(final Object idObj) {
        this.name = UNIQUE_NAME_GENERATOR.get(idObj.getClass().getSimpleName());
        return this;
    }

    public Cache<K, V> newCache() {
        return CacheBuilderInternalFactory.newCache(this);
    }

    public Integer getMaximumSize() {
        return maximumSize;
    }

    public CacheBuilder<K, V> withMaximumSize(final Integer maximumSize) {
        this.maximumSize = maximumSize;
        return this;
    }

    public Duration getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public CacheBuilder<K, V> withExpireAfterWrite(final Duration expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
        return this;
    }

    public Duration getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public CacheBuilder<K, V> withExpireAfterAccess(final Duration expireAfterAccess) {
        this.expireAfterAccess = expireAfterAccess;
        return this;
    }

    public Duration getRefreshAfterWrite() {
        return refreshAfterWrite;
    }

    public CacheBuilder<K, V> setRefreshAfterWrite(final Duration refreshAfterWrite) {
        this.refreshAfterWrite = refreshAfterWrite;
        return this;
    }

}
