package de.invesdwin.context.jcache;

import javax.annotation.concurrent.Immutable;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.FactoryBuilder.SingletonFactory;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;

/**
 * Since the individual listener interface have been created as a performance optimization in JCache, you should rather
 * implement the interfaces you actually need instead of using this class.
 * 
 * Though still providing this class for convenience purposes.
 * 
 * @author subes
 */
@Immutable
public class CacheEntryListenerSupport<K, V> implements CacheEntryCreatedListener<K, V>,
        CacheEntryExpiredListener<K, V>, CacheEntryRemovedListener<K, V>, CacheEntryUpdatedListener<K, V>,
        CacheEntryEventFilter<K, V> {

    @Override
    public boolean evaluate(final CacheEntryEvent<? extends K, ? extends V> event) {
        return true;
    }

    @Override
    public void onUpdated(final Iterable<CacheEntryEvent<? extends K, ? extends V>> events) {}

    @Override
    public void onRemoved(final Iterable<CacheEntryEvent<? extends K, ? extends V>> events) {}

    @Override
    public void onExpired(final Iterable<CacheEntryEvent<? extends K, ? extends V>> events) {}

    @Override
    public void onCreated(final Iterable<CacheEntryEvent<? extends K, ? extends V>> events) {}

    public CacheEntryListenerConfiguration<K, V> newConfiguration() {
        return new MutableCacheEntryListenerConfiguration<K, V>(new SingletonFactory<CacheEntryListener<K, V>>(this),
                new SingletonFactory<CacheEntryEventFilter<K, V>>(this), false, false);
    }

}
