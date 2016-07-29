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
import de.invesdwin.util.math.decimal.scaled.ByteSize;

@SuppressWarnings("serial")
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

    //ehCacheConfig
    private String name;
    private ByteSize maxDiskSize;
    private Integer maxDiskElements;
    private ByteSize maxHeapSize;
    private Integer maxHeapElements;

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

    //---------------------------------- ehCacheConfig -----------------------------------//

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

    public ByteSize getMaxDiskSize() {
        return maxDiskSize;
    }

    public CacheBuilder<K, V> withMaxDiskSize(final ByteSize maxDiskSize) {
        this.maxDiskSize = maxDiskSize;
        return this;
    }

    public Integer getMaxDiskElements() {
        return maxDiskElements;
    }

    public CacheBuilder<K, V> withMaxDiskElements(final Integer maxDiskElements) {
        this.maxDiskElements = maxDiskElements;
        return this;
    }

    public ByteSize getMaxHeapSize() {
        return maxHeapSize;
    }

    public CacheBuilder<K, V> withMaxHeapSize(final ByteSize maxHeapSize) {
        this.maxHeapSize = maxHeapSize;
        return this;
    }

    public Integer getMaxHeapElements() {
        return maxHeapElements;
    }

    public CacheBuilder<K, V> withMaxHeapElements(final Integer maxHeapElements) {
        this.maxHeapElements = maxHeapElements;
        return this;
    }

    public Cache<K, V> newCache() {
        return CacheBuilderInternalFactory.newCache(this);
    }

}
