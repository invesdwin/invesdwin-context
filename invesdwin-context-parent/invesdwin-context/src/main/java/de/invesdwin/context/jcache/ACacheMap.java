package de.invesdwin.context.jcache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.cache.Cache;

import de.invesdwin.context.jcache.internal.SizeCountingCacheEntryListener;

@NotThreadSafe
public abstract class ACacheMap<K, V> implements Map<K, V> {

    private Cache<K, V> delegate;
    private final SizeCountingCacheEntryListener<K, V> sizeCounter;

    public ACacheMap() {
        this.sizeCounter = new SizeCountingCacheEntryListener<K, V>();
    }

    protected abstract Cache<K, V> createDelegate();

    public synchronized Cache<K, V> getDelegate() {
        if (delegate == null) {
            delegate = createDelegate();
            delegate.registerCacheEntryListener(sizeCounter.newConfiguration());
        }
        return delegate;
    }

    /**
     * This might not return the correct value!
     */
    @Override
    public int size() {
        return sizeCounter.getSize();
    }

    @Override
    public boolean isEmpty() {
        return size() <= 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean containsKey(final Object key) {
        try {
            return getDelegate().containsKey((K) key);
        } catch (final ClassCastException e) {
            return false;
        }
    }

    /**
     * Might be really slow!
     */
    @Deprecated
    @Override
    public boolean containsValue(final Object value) {
        for (final javax.cache.Cache.Entry<K, V> e : getDelegate()) {
            if (e.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(final Object key) {
        try {
            return getDelegate().get((K) key);
        } catch (final ClassCastException e) {
            return (V) null;
        }
    }

    /**
     * not overriding putIfAbsend here since no benefit available
     */
    @Override
    public V put(final K key, final V value) {
        return getDelegate().getAndPut(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(final Object key) {
        try {
            return getDelegate().getAndRemove((K) key);
        } catch (final ClassCastException e) {
            return (V) null;
        }
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        getDelegate().putAll(m);
    }

    @Override
    public void clear() {
        getDelegate().removeAll();
    }

    @Override
    @Deprecated
    /**
     * Use keysIterator instead
     */
    public Set<K> keySet() {
        final Set<K> keys = new HashSet<K>();
        for (final javax.cache.Cache.Entry<K, V> e : getDelegate()) {
            keys.add(e.getKey());
        }
        return keys;
    }

    public Iterator<K> keysIterator() {
        return new Iterator<K>() {
            private final Iterator<javax.cache.Cache.Entry<K, V>> entryIterator = getDelegate().iterator();

            @Override
            public boolean hasNext() {
                return entryIterator.hasNext();
            }

            @Override
            public K next() {
                return entryIterator.next().getKey();
            }

            @Override
            public void remove() {
                entryIterator.remove();
            }
        };
    }

    @Override
    @Deprecated
    /**
     * Use valuesIterator instead
     */
    public Collection<V> values() {
        final List<V> values = new ArrayList<V>();
        for (final javax.cache.Cache.Entry<K, V> e : getDelegate()) {
            values.add(e.getValue());
        }
        return values;
    }

    public Iterator<V> valuesIterator() {
        return new Iterator<V>() {
            private final Iterator<javax.cache.Cache.Entry<K, V>> entryIterator = getDelegate().iterator();

            @Override
            public boolean hasNext() {
                return entryIterator.hasNext();
            }

            @Override
            public V next() {
                return entryIterator.next().getValue();
            }

            @Override
            public void remove() {
                entryIterator.remove();
            }
        };
    }

    @Override
    @Deprecated
    /**
     * Use entriesIterator instead
     */
    public Set<Entry<K, V>> entrySet() {
        final Set<Entry<K, V>> entries = new HashSet<Entry<K, V>>();
        for (final javax.cache.Cache.Entry<K, V> e : getDelegate()) {
            entries.add(new CacheEntryWrapper(e));
        }
        return entries;
    }

    public Iterator<Entry<K, V>> entriesIterator() {
        return new Iterator<Entry<K, V>>() {
            private final Iterator<javax.cache.Cache.Entry<K, V>> entryIterator = getDelegate().iterator();

            @Override
            public boolean hasNext() {
                return entryIterator.hasNext();
            }

            @Override
            public Entry<K, V> next() {
                return new CacheEntryWrapper(entryIterator.next());
            }

            @Override
            public void remove() {
                entryIterator.remove();
            }
        };
    }

    private class CacheEntryWrapper implements Entry<K, V> {
        private V overridenValue;
        private final javax.cache.Cache.Entry<K, V> entry;

        CacheEntryWrapper(final javax.cache.Cache.Entry<K, V> entry) {
            this.entry = entry;
        }

        @Override
        public K getKey() {
            return entry.getKey();
        }

        @Override
        public V getValue() {
            if (overridenValue != null) {
                return overridenValue;
            } else {
                return entry.getValue();
            }
        }

        @Override
        public V setValue(final V value) {
            overridenValue = value;
            return put(getKey(), value);
        }
    }

}
