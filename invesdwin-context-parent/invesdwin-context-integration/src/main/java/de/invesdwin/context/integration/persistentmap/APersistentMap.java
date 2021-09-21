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

    private final IReadWriteLock tableLock;
    private final TableFinalizer<K, V> tableFinalizer;
    private FDate tableCreationTime;
    private IPersistentMapFactory<K, V> factory;
    private final APreLockedSet<K> keySet;
    private final APreLockedSet<Entry<K, V>> entrySet;
    private final APreLockedCollection<V> values;
    private File timestampFile;

    public APersistentMap(final String name) {
        super(name);
        this.tableLock = newTableLock();
        this.tableFinalizer = new TableFinalizer<>();
        this.keySet = new APreLockedSet<K>(
                new TextDescription("%s[%s].keySet.iterator", APersistentMap.class.getSimpleName(), getName()),
                tableLock.readLock()) {
            @Override
            protected Set<K> getPreLockedDelegate() {
                return getTableWithReadLock().keySet();
            }
        };
        this.entrySet = new APreLockedSet<Entry<K, V>>(
                new TextDescription("%s[%s].keySet.iterator", APersistentMap.class.getSimpleName(), getName()),
                tableLock.readLock()) {
            @Override
            protected Set<Entry<K, V>> getPreLockedDelegate() {
                return getTableWithReadLock().entrySet();
            }
        };
        this.values = new APreLockedCollection<V>(
                new TextDescription("%s[%s].values.iterator", APersistentMap.class.getSimpleName(), getName()),
                tableLock.readLock()) {
            @Override
            protected Collection<V> getPreLockedDelegate() {
                return getTableWithReadLock().values();
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

    private ConcurrentMap<K, V> getTableWithReadLock() {
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
    public int size() {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            return table.size();
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            return table.isEmpty();
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public boolean containsKey(final Object key) {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            return table.containsKey(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public boolean containsValue(final Object value) {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            return table.containsValue(value);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public V get(final Object key) {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            return table.get(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public V put(final K key, final V value) {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            return table.put(key, value);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            return table.computeIfAbsent(key, mappingFunction);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            return table.compute(key, remappingFunction);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            return table.computeIfPresent(key, remappingFunction);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public V remove(final Object key) {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            return table.remove(key);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            table.putAll(m);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public void clear() {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            table.clear();
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public Collection<V> values() {
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entrySet;
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            return table.putIfAbsent(key, value);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            return table.remove(key, value);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            return table.replace(key, oldValue, newValue);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public V replace(final K key, final V value) {
        final ConcurrentMap<K, V> table = getTableWithReadLock();
        try {
            return table.replace(key, value);
        } finally {
            getReadLock().unlock();
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
        close();
        Files.deleteQuietly(getFile());
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

}
