package de.invesdwin.context.integration.persistentmap;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.collections.fast.concurrent.locked.pre.APreLockedCollection;
import de.invesdwin.util.collections.fast.concurrent.locked.pre.APreLockedSet;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.Locks;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.concurrent.lock.readwrite.IReentrantReadWriteLock;
import de.invesdwin.util.lang.Closeables;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.description.TextDescription;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.shutdown.ShutdownHookManager;
import de.invesdwin.util.time.date.FDate;

/**
 * If you need to store large data on disk, it is better to use LevelDB only for an ordered index and store the actual
 * db in a file based persistent hash map. This is because LevelDB has very bad insertion speed when handling large
 * elements.
 */
@ThreadSafe
public abstract class APersistentMap<K, V> extends APersistentMapConfig<K, V>
        implements ConcurrentMap<K, V>, Closeable {

    protected TextDescription iteratorName;
    protected final IReadWriteLock tableLock;
    protected final TableFinalizer<K, V> tableFinalizer;
    protected FDate tableCreationTime;
    protected IPersistentMapFactory<K, V> factory;
    protected final Set<K> keySet;
    protected final Set<Entry<K, V>> entrySet;
    protected final Collection<V> values;
    protected File timestampFile;

    public APersistentMap(final String name) {
        super(name);
        this.iteratorName = new TextDescription("%s[%s].iterator", APersistentMap.class.getSimpleName(), getName());
        this.tableLock = newTableLock();
        this.tableFinalizer = new TableFinalizer<>();
        this.keySet = newKeySet();
        this.entrySet = newEntrySet();
        this.values = newValues();
    }

    private APersistentMap<K, V> getThis() {
        return this;
    }

    protected Collection<V> newValues() {
        return new APreLockedCollection<V>(iteratorName, tableLock.readLock()) {
            @Override
            protected Collection<V> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().values();
            }
        };
    }

    protected Set<Entry<K, V>> newEntrySet() {
        return new APreLockedSet<Entry<K, V>>(iteratorName, tableLock.readLock()) {
            @Override
            protected Set<Entry<K, V>> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().entrySet();
            }
        };
    }

    protected Set<K> newKeySet() {
        return new APreLockedSet<K>(iteratorName, tableLock.readLock()) {
            @Override
            protected Set<K> getPreLockedDelegate() {
                return getThis().getPreLockedDelegate().keySet();
            }
        };
    }

    protected final IPersistentMapFactory<K, V> getFactory() {
        if (factory == null) {
            this.factory = newFactory();
        }
        return factory;
    }

    protected abstract IPersistentMapFactory<K, V> newFactory();

    protected IReentrantReadWriteLock newTableLock() {
        return Locks.newReentrantReadWriteLock(APersistentMap.class.getSimpleName() + "_" + getName() + "_tableLock");
    }

    protected ILock getReadLock() {
        return tableLock.readLock();
    }

    protected File getTimestampFile() {
        if (timestampFile == null) {
            timestampFile = new File(getDirectory(), getName() + "_createdTimestamp");
        }
        return timestampFile;
    }

    public FDate getTableCreationTime() {
        if (tableCreationTime == null) {
            final File timestampFile = getTimestampFile();
            if (!timestampFile.exists()) {
                return null;
            } else {
                tableCreationTime = FDate.valueOf(timestampFile.lastModified());
            }
        }
        return tableCreationTime;
    }

    protected ConcurrentMap<K, V> getPreLockedDelegate() {
        maybePurgeTable();
        //directly return table with read lock if not null
        final ILock readLock = getReadLock();
        readLock.lock();
        if (tableFinalizer.table != null) {
            return tableFinalizer.table;
        }
        readLock.unlock();

        //otherwise initialize it with write lock (though check again because of lock switch)
        initializeTable();

        //and return the now not null table with read lock
        readLock.lock();
        if (tableFinalizer.table == null) {
            readLock.unlock();
            throw new IllegalStateException("table should not be null here");
        }
        return tableFinalizer.table;
    }

    private void initializeTable() {
        if (ShutdownHookManager.isShuttingDown()) {
            throw new RuntimeException("Shutting down");
        }
        tableLock.writeLock().lock();
        try {
            if (tableFinalizer.table == null) {
                if (getTableCreationTime() == null) {
                    if (isDiskPersistence()) {
                        try {
                            final File timestampFile = getTimestampFile();
                            Files.forceMkdir(timestampFile.getParentFile());
                            Files.touch(timestampFile);
                        } catch (final IOException e) {
                            throw Err.process(e);
                        }
                    }
                    tableCreationTime = new FDate();
                }
                try {
                    tableFinalizer.table = getFactory().newPersistentMap(this);
                    tableFinalizer.register(this);
                    PersistentMapCloseManager.register(this);
                } catch (final Throwable e) {
                    Err.process(new RuntimeException("Table data for [" + getDirectory() + "/" + getName()
                            + "] is inconsistent. Resetting data and trying again.", e));
                    innerDeleteTable();
                    tableFinalizer.table = getFactory().newPersistentMap(this);
                    tableFinalizer.register(this);
                }
            }
        } finally {
            tableLock.writeLock().unlock();
        }
    }

    @Override
    public synchronized void close() {
        tableLock.writeLock().lock();
        try {
            if (tableFinalizer.table != null) {
                PersistentMapCloseManager.unregister(this);
                Closeables.closeQuietly(tableFinalizer.table);
                tableFinalizer.table = null;
            }
        } finally {
            tableLock.writeLock().unlock();
        }
    }

    public synchronized void deleteTable() {
        tableLock.writeLock().lock();
        try {
            innerDeleteTable();
        } finally {
            tableLock.writeLock().unlock();
        }
    }

    private void innerDeleteTable() {
        if (tableFinalizer.table != null) {
            PersistentMapCloseManager.unregister(this);
            Closeables.closeQuietly(tableFinalizer.table);
            tableFinalizer.table = null;
        }
        if (isDiskPersistence()) {
            Files.deleteQuietly(timestampFile);
            final File tableDirectory = new File(getDirectory(), getName());
            if (tableDirectory.isDirectory()) {
                final String[] list = tableDirectory.list();
                if (list == null || list.length == 0) {
                    Files.deleteNative(tableDirectory);
                }
            } else {
                Files.deleteQuietly(getFile());
            }
        }
        tableCreationTime = null;
        onDeleteTableFinished();
    }

    protected boolean shouldPurgeTable() {
        return false;
    }

    protected void onDeleteTableFinished() {
    }

    private void maybePurgeTable() {
        if (shouldPurgeTable()) {
            //only purge if currently not used
            if (tableLock.writeLock().tryLock()) {
                try {
                    //condition could have changed since lock has been acquired
                    if (shouldPurgeTable()) {
                        innerDeleteTable();
                    }
                } finally {
                    tableLock.writeLock().unlock();
                }
            }
        }
    }

    private static final class TableFinalizer<_K, _V> extends AFinalizer {
        private volatile ConcurrentMap<_K, _V> table;

        @Override
        protected void clean() {
            Closeables.closeQuietly(table);
            table = null;
        }

        @Override
        protected boolean isCleaned() {
            return table == null;
        }

        @Override
        public boolean isThreadLocal() {
            return false;
        }
    }

    //-----------------------------------------------

    @Override
    public final void clear() {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            delegate.clear();
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final boolean isEmpty() {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.isEmpty();
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final int size() {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.size();
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final boolean containsKey(final Object key) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.containsKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final boolean containsValue(final Object value) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.containsValue(value);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final V get(final Object key) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.get(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final V put(final K key, final V value) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.put(key, value);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final V remove(final Object key) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.remove(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final void putAll(final Map<? extends K, ? extends V> m) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            delegate.putAll(m);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public final Collection<V> values() {
        return values;
    }

    @Override
    public final Set<Entry<K, V>> entrySet() {
        return entrySet;
    }

    @Override
    public final boolean remove(final Object key, final Object value) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.remove(key, value);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final boolean replace(final K key, final V oldValue, final V newValue) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.replace(key, oldValue, newValue);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final V replace(final K key, final V value) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.replace(key, value);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.compute(key, remappingFunction);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.computeIfAbsent(key, mappingFunction);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final V computeIfPresent(final K key,
            final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.computeIfPresent(key, remappingFunction);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final V getOrDefault(final Object key, final V defaultValue) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.getOrDefault(key, defaultValue);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public final V putIfAbsent(final K key, final V value) {
        final Map<K, V> delegate = getPreLockedDelegate();
        try {
            return delegate.putIfAbsent(key, value);
        } finally {
            getReadLock().unlock();
        }
    }
}
