package de.invesdwin.context.integration.persistentmap.navigable;

import java.io.File;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentNavigableMap;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.persistentmap.APersistentMap;
import de.invesdwin.util.collections.fast.concurrent.locked.pre.APreLockedConcurrentNavigableMap;
import de.invesdwin.util.collections.fast.concurrent.locked.pre.APreLockedNavigableSet;
import de.invesdwin.util.lang.reflection.Reflections;

@ThreadSafe
public abstract class APersistentNavigableMap<K, V> extends APersistentMap<K, V>
        implements ConcurrentNavigableMap<K, V>, IPersistentNavigableMap<K, V> {

    private ConcurrentNavigableMap<K, V> descendingMap;
    private NavigableSet<K> descendingKeySet;

    public APersistentNavigableMap(final String name) {
        super(name);
    }

    @Override
    public File getDirectory() {
        return new File(new File(getBaseDirectory(), APersistentNavigableMap.class.getSimpleName()),
                Reflections.getClassSimpleNameNonBlank(getFactory().getClass()));
    }

    @Override
    protected abstract IPersistentNavigableMapFactory<K, V> newFactory();

    @Override
    public ConcurrentNavigableMap<K, V> getPreLockedDelegate() {
        return (ConcurrentNavigableMap<K, V>) super.getPreLockedDelegate();
    }

    @Override
    public final Comparator<? super K> comparator() {
        final SortedMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.comparator();
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final K firstKey() {
        final SortedMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.firstKey();
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final K lastKey() {
        final SortedMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.lastKey();
        } finally {
            getReadLock().unlock();
        }
    }

    private APersistentNavigableMap<K, V> getThis() {
        return this;
    }

    protected NavigableSet<K> newDescendingKeySet() {
        return new APreLockedNavigableSet<K>(iteratorName, getReadLock()) {
            @Override
            protected NavigableSet<K> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().descendingKeySet();
            }
        };
    }

    @Override
    public final Entry<K, V> lowerEntry(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.lowerEntry(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final K lowerKey(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.lowerKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final Entry<K, V> floorEntry(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.floorEntry(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final K floorKey(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.floorKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final Entry<K, V> ceilingEntry(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.ceilingEntry(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final K ceilingKey(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.ceilingKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final Entry<K, V> higherEntry(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.higherEntry(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final K higherKey(final K key) {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.higherKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final Entry<K, V> firstEntry() {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.firstEntry();
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final Entry<K, V> lastEntry() {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.lastEntry();
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final Entry<K, V> pollFirstEntry() {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.pollFirstEntry();
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final Entry<K, V> pollLastEntry() {
        final NavigableMap<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.pollLastEntry();
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public ConcurrentNavigableMap<K, V> descendingMap() {
        if (descendingMap == null) {
            descendingMap = newDescendingMap();
        }
        return descendingMap;
    }

    @Override
    protected Set<K> newKeySet() {
        return new APreLockedNavigableSet<K>(iteratorName, getReadLock()) {
            @Override
            protected NavigableSet<K> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().navigableKeySet();
            }
        };
    }

    @Override
    public final NavigableSet<K> keySet() {
        return (NavigableSet<K>) super.keySet();
    }

    @Override
    public final NavigableSet<K> navigableKeySet() {
        return keySet();
    }

    @Override
    public final NavigableSet<K> descendingKeySet() {
        if (descendingKeySet == null) {
            descendingKeySet = newDescendingKeySet();
        }
        return descendingKeySet;
    }

    @Override
    public final ConcurrentNavigableMap<K, V> subMap(final K fromKey, final boolean fromInclusive, final K toKey,
            final boolean toInclusive) {
        return new APreLockedConcurrentNavigableMap<K, V>(iteratorName, getReadLock()) {
            @Override
            protected ConcurrentNavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().subMap(fromKey, fromInclusive, toKey, toInclusive);
            }
        };
    }

    @Override
    public final ConcurrentNavigableMap<K, V> headMap(final K toKey, final boolean inclusive) {
        return new APreLockedConcurrentNavigableMap<K, V>(iteratorName, getReadLock()) {
            @Override
            protected ConcurrentNavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().headMap(toKey, inclusive);
            }
        };
    }

    @Override
    public final ConcurrentNavigableMap<K, V> tailMap(final K fromKey, final boolean inclusive) {
        return new APreLockedConcurrentNavigableMap<K, V>(iteratorName, getReadLock()) {
            @Override
            protected ConcurrentNavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().tailMap(fromKey, inclusive);
            }
        };
    }

    @Override
    public final ConcurrentNavigableMap<K, V> subMap(final K fromKey, final K toKey) {
        return new APreLockedConcurrentNavigableMap<K, V>(iteratorName, getReadLock()) {
            @Override
            protected ConcurrentNavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().subMap(fromKey, toKey);
            }
        };
    }

    @Override
    public final ConcurrentNavigableMap<K, V> headMap(final K toKey) {
        return new APreLockedConcurrentNavigableMap<K, V>(iteratorName, getReadLock()) {
            @Override
            protected ConcurrentNavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().headMap(toKey);
            }
        };
    }

    @Override
    public final ConcurrentNavigableMap<K, V> tailMap(final K fromKey) {
        return new APreLockedConcurrentNavigableMap<K, V>(iteratorName, getReadLock()) {
            @Override
            protected ConcurrentNavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().tailMap(fromKey);
            }
        };
    }

    protected ConcurrentNavigableMap<K, V> newDescendingMap() {
        return new APreLockedConcurrentNavigableMap<K, V>(iteratorName, getReadLock()) {
            @Override
            protected ConcurrentNavigableMap<K, V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().descendingMap();
            }
        };
    }

}
