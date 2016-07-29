package de.invesdwin.context.jcache.internal;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.cache.event.CacheEntryEvent;

import de.invesdwin.context.jcache.CacheEntryListenerSupport;

@ThreadSafe
public class SizeCountingCacheEntryListener<K, V> extends CacheEntryListenerSupport<K, V> {

    @GuardedBy("this")
    private int size;

    @Override
    public void onRemoved(final Iterable<CacheEntryEvent<? extends K, ? extends V>> events) {
        int count = 0;
        for (@SuppressWarnings("unused")
        final CacheEntryEvent<? extends K, ? extends V> event : events) {
            count++;
        }
        synchronized (this) {
            size = Math.max(0, size - count);
        }
    }

    @Override
    public void onExpired(final Iterable<CacheEntryEvent<? extends K, ? extends V>> events) {
        onRemoved(events);
    }

    @Override
    public void onCreated(final Iterable<CacheEntryEvent<? extends K, ? extends V>> events) {
        int count = 0;
        for (@SuppressWarnings("unused")
        final CacheEntryEvent<? extends K, ? extends V> event : events) {
            count++;
        }
        synchronized (this) {
            size = Math.max(0, size + count);
        }
    }

    public synchronized int getSize() {
        return size;
    }

}
