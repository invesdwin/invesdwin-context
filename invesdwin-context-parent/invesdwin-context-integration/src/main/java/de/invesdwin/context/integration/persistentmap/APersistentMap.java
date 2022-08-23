package de.invesdwin.context.integration.persistentmap;

import java.io.File;
import java.io.IOException;
import java.nio.channels.OverlappingFileLockException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.retry.RetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.task.ARetryCallable;
import de.invesdwin.context.integration.retry.task.RetryOriginator;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.fast.concurrent.locked.pre.APreLockedCollection;
import de.invesdwin.util.collections.fast.concurrent.locked.pre.APreLockedSet;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.readwrite.IReadWriteLock;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Closeables;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.lang.description.TextDescription;
import de.invesdwin.util.lang.finalizer.AFinalizer;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.shutdown.ShutdownHookManager;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;

/**
 * If you need to store large data on disk, it is better to use LevelDB only for an ordered index and store the actual
 * db in a file based persistent hash map. This is because LevelDB has very bad insertion speed when handling large
 * elements.
 */
@ThreadSafe
public abstract class APersistentMap<K, V> extends APersistentMapConfig<K, V> implements IPersistentMap<K, V> {

    protected TextDescription iteratorName;
    protected final IReadWriteLock tableLock;
    protected final TableFinalizer<K, V> tableFinalizer;
    protected FDate tableCreationTime;
    protected IPersistentMapFactory<K, V> factory;
    protected File timestampFile;
    private Set<K> keySet;
    private Set<Entry<K, V>> entrySet;
    private Collection<V> values;

    private final AtomicBoolean initializing = new AtomicBoolean();

    public APersistentMap(final String name) {
        super(name);
        this.iteratorName = new TextDescription("%s[%s].iterator", APersistentMap.class.getSimpleName(), getName());
        this.tableLock = newTableLock();
        this.tableFinalizer = new TableFinalizer<>();
    }

    @Override
    public File getDirectory() {
        return new File(new File(getBaseDirectory(), APersistentMap.class.getSimpleName()),
                Reflections.getClassSimpleNameNonBlank(getFactory().getClass()));
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

    @Override
    public void removeAll(final IKeyMatcher<K> matcher) {
        final ConcurrentMap<K, V> delegate = getPreLockedDelegate();
        try {
            getFactory().removeAll(delegate, matcher);
        } finally {
            getReadLock().unlock();
        }
    }

    @Override
    public V getOrLoad(final K key, final Supplier<V> loadable) {
        final V cachedValue = get(key);
        if (cachedValue == null) {
            //don't hold read lock while loading value
            final V loadedValue = loadable.get();
            //write lock is only for the actual table variable, not the table values, thus read lock is fine here
            if (loadedValue != null) {
                put(key, loadedValue);
            }
            return loadedValue;
        } else {
            return cachedValue;
        }
    }

    @Override
    public boolean isDiskPersistence() {
        return getFactory().isDiskPersistenceSupported();
    }

    protected final IPersistentMapFactory<K, V> getFactory() {
        if (factory == null) {
            this.factory = newFactory();
        }
        return factory;
    }

    protected abstract IPersistentMapFactory<K, V> newFactory();

    protected IReadWriteLock newTableLock() {
        return ILockCollectionFactory.getInstance(isThreadSafe())
                .newReadWriteLock(APersistentMap.class.getSimpleName() + "_" + getName() + "_tableLock");
    }

    protected boolean isThreadSafe() {
        return true;
    }

    public ILock getReadLock() {
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
            if (isDiskPersistence()) {
                final File timestampFile = getTimestampFile();
                if (!timestampFile.exists()) {
                    return null;
                } else {
                    tableCreationTime = FDate.valueOf(timestampFile.lastModified());
                }
            }
        }
        return tableCreationTime;
    }

    public ConcurrentMap<K, V> getPreLockedDelegate() {
        maybePurgeTable();
        //directly return table with read lock if not null
        final ILock readLock = getReadLock();
        readLock.lock();
        if (tableFinalizer.table != null) {
            //keep locked
            return tableFinalizer.table;
        }
        readLock.unlock();
        if (!initializing.compareAndSet(false, true)) {
            while (initializing.get()) {
                FTimeUnit.MILLISECONDS.sleepNoInterrupt(1);
            }
            return getPreLockedDelegate();
        } else {
            try {
                return new ARetryCallable<ConcurrentMap<K, V>>(
                        new RetryOriginator(APersistentMap.class, "initializeTableInitLocked", getName())) {

                    @Override
                    protected ConcurrentMap<K, V> callRetry() throws Exception {
                        return initializeTableInitLocked(readLock);
                    }
                }.call();
            } finally {
                initializing.set(false);
            }
        }
    }

    private ConcurrentMap<K, V> initializeTableInitLocked(final ILock readLock) {
        //otherwise initialize it with write lock (though check again because of lock switch)
        initializeTable();

        //and return the now not null table with read lock
        readLock.lock();
        if (tableFinalizer.table == null) {
            readLock.unlock();
            throw new RetryLaterRuntimeException(
                    "table might have been deleted in the mean time, thus retry initialization");
        }
        return tableFinalizer.table;
    }

    private void initializeTable() {
        if (ShutdownHookManager.isShuttingDown()) {
            throw new RuntimeException("Shutting down");
        }
        tableLock.writeLock().lock();
        try {
            initializeTableLocked();
        } finally {
            tableLock.writeLock().unlock();
        }
    }

    private void initializeTableLocked() {
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
                if (tableFinalizer.table == null) {
                    throw new IllegalStateException("table should not be null");
                }
                tableFinalizer.register(this);
                PersistentMapCloseManager.register(this);
            } catch (final Throwable e) {
                if (Throwables.isCausedByType(e, OverlappingFileLockException.class)) {
                    //Caused by: org.mapdb.DBException$FileLocked: File is already opened and is locked: Y:\InvesdwinData\FinancialdataHistorical\default\APersistentMap\PersistentMapDBFactory\CachingFinancialdataHistoricalServiceTime_getTimeInstrument
                    //at org.mapdb.volume.Volume.lockFile(Volume.java:495)
                    //at org.mapdb.volume.MappedFileVol.<init>(MappedFileVol.java:88)
                    //at org.mapdb.volume.MappedFileVol$MappedFileFactory.factory(MappedFileVol.java:64)
                    //Caused by: java.nio.channels.OverlappingFileLockException: null
                    //at java.base/sun.nio.ch.FileLockTable.checkList(FileLockTable.java:229)
                    //at java.base/sun.nio.ch.FileLockTable.add(FileLockTable.java:123)
                    //at java.base/sun.nio.ch.FileChannelImpl.tryLock(FileChannelImpl.java:1154)
                    //at java.base/java.nio.channels.FileChannel.tryLock(FileChannel.java:1165)
                    //at org.mapdb.volume.Volume.lockFile(Volume.java:490)
                    throw new RetryLaterRuntimeException(e);
                } else if (Strings.containsIgnoreCase(e.getMessage(), "LOCK")) {
                    //ezdb.DbException: org.fusesource.leveldbjni.internal.NativeDB$DBException: IO error: lock /home/subes/Dokumente/Entwicklung/invesdwin/invesdwin-trading/invesdwin-trading-parent/invesdwin-trading-modules/invesdwin-trading-backtest/.invesdwin/de.invesdwin.context.persistence.leveldb.ADelegateRangeTable/CachingFinancialdataService_getInstrument/LOCK: Die Ressource ist zur Zeit nicht verfügbar
                    //at ezdb.leveldb.EzLevelDbTable.<init>(EzLevelDbTable.java:50)
                    //at ezdb.leveldb.EzLevelDb.getTable(EzLevelDb.java:69)
                    //at de.invesdwin.context.persistence.leveldb.ADelegateRangeTable.getTableWithReadLock(ADelegateRangeTable.java:144)
                    throw new RetryLaterRuntimeException(e);
                } else {
                    if (isDeleteTableOnCorruption()) {
                        Err.process(new RuntimeException("Table data for [" + getDirectory() + "/" + getName()
                                + "] is inconsistent. Resetting data and trying again.", e));
                        innerDeleteTable();
                        tableFinalizer.table = getFactory().newPersistentMap(this);
                        if (tableFinalizer.table == null) {
                            throw new IllegalStateException("table should not be null");
                        }
                        tableFinalizer.register(this);
                    } else {
                        throw new CorruptedStorageException("Table data for [" + getDirectory() + "/" + getName()
                                + "] is inconsistent. Automatic recovery is disabled.", e);
                    }
                }
            }
        }
    }

    protected boolean isDeleteTableOnCorruption() {
        return true;
    }

    @Override
    public void close() {
        if (initializing.get()) {
            return;
        }
        tableLock.writeLock().lock();
        try {
            if (tableFinalizer.table != null) {
                PersistentMapCloseManager.unregister(this);
                Closeables.closeOrThrow(tableFinalizer.table);
                tableFinalizer.table = null;
            }
        } finally {
            tableLock.writeLock().unlock();
        }
    }

    @Override
    public void deleteTable() {
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
            Closeables.closeOrThrow(tableFinalizer.table);
            tableFinalizer.table = null;
        }
        if (isDiskPersistence()) {
            Files.deleteQuietly(timestampFile);
            final File file = getFile();
            if (file.isDirectory()) {
                final String[] list = file.list();
                if (list != null && list.length > 0) {
                    Files.deleteNative(file);
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

    protected void onDeleteTableFinished() {}

    private void maybePurgeTable() {
        if (!initializing.get() && shouldPurgeTable()) {
            //only purge if currently not used, might happen due to recursive computeIfAbsent with different loading functions
            if (tableLock.writeLock().tryLock()) {
                try {
                    //condition could have changed since lock has been acquired
                    if (!initializing.get() && shouldPurgeTable()) {
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
            Closeables.closeOrThrow(table);
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
        if (tableFinalizer.table == null) {
            if (!isDiskPersistence()) {
                return true;
            }
            final File file = getFile();
            if (!file.exists()) {
                return true;
            }
            if (file.isDirectory()) {
                final String[] list = file.list();
                if (list == null || list.length == 0) {
                    return true;
                }
            }
        }
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
        if (keySet == null) {
            keySet = newKeySet();
        }
        return keySet;
    }

    @Override
    public final Collection<V> values() {
        if (values == null) {
            values = newValues();
        }
        return values;
    }

    @Override
    public final Set<Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = newEntrySet();
        }
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
