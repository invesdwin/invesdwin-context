package de.invesdwin.context.integration.persistentmap.large;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.persistentmap.APersistentMap;
import de.invesdwin.context.integration.persistentmap.APersistentMapConfig;
import de.invesdwin.context.integration.persistentmap.IKeyMatcher;
import de.invesdwin.context.integration.persistentmap.IPersistentMap;
import de.invesdwin.context.integration.persistentmap.IPersistentMapFactory;
import de.invesdwin.context.integration.persistentmap.large.storage.IChunkStorage;
import de.invesdwin.context.integration.persistentmap.large.storage.MappedFileChunkStorage;
import de.invesdwin.context.integration.persistentmap.large.storage.SequentialFileChunkStorage;
import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummary;
import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummarySerde;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.OperatingSystem;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.marshallers.serde.ISerde;

@ThreadSafe
public abstract class ALargePersistentMap<K, V> extends APersistentMapConfig<K, V> implements IPersistentMap<K, V> {

    private IPersistentMap<K, ChunkSummary> indexMap;
    private IChunkStorage<V> chunkStorage;

    public ALargePersistentMap(final String name) {
        super(name);
    }

    private IPersistentMap<K, ChunkSummary> newIndexMap() {
        return new APersistentMap<K, ChunkSummary>("index") {
            @Override
            protected IPersistentMapFactory<K, ChunkSummary> newFactory() {
                return ALargePersistentMap.this.newIndexFactory();
            }

            @Override
            public ISerde<K> newKeySerde() {
                return ALargePersistentMap.this.newKeySerde();
            }

            @Override
            public ISerde<ChunkSummary> newValueSerde() {
                return ChunkSummarySerde.GET;
            }

            @Override
            public File getBaseDirectory() {
                return ALargePersistentMap.this.getBaseDirectory();
            }

            @Override
            public File getDirectory() {
                return ALargePersistentMap.this.getFile();
            }
        };
    }

    protected IPersistentMap<K, ChunkSummary> getIndexMap() {
        if (indexMap == null) {
            synchronized (this) {
                if (indexMap == null) {
                    indexMap = newIndexMap();
                }
            }
        }
        return indexMap;
    }

    protected IChunkStorage<V> getChunkStorage() {
        if (chunkStorage == null) {
            synchronized (this) {
                if (chunkStorage == null) {
                    chunkStorage = newChunkStorage();
                }
            }
        }
        return chunkStorage;
    }

    private IChunkStorage<V> newChunkStorage() {
        final File directory = new File(getFile(), "storage");
        return newChunkStorage(directory, newValueSerde(), isReadOnly(), isCloseAllowed());
    }

    protected abstract boolean isReadOnly();

    protected abstract boolean isCloseAllowed();

    protected IChunkStorage<V> newChunkStorage(final File directory, final ISerde<V> valueSerde, final boolean readOnly,
            final boolean closeAllowed) {
        return newDefaultChunkStorage(directory, valueSerde, readOnly, closeAllowed);
    }

    public static <V> IChunkStorage<V> newDefaultChunkStorage(final File directory, final ISerde<V> valueSerde,
            final boolean readOnly, final boolean closeAllowed) {
        if (OperatingSystem.isWindows()) {
            //            Caused by: java.io.IOException: Die Auslagerungsdatei ist zu klein, um diesen Vorgang durchzuführen
            //            at java.base/sun.nio.ch.FileChannelImpl.map0(Native Method)
            //            at org.agrona.IoUtil.map(IoUtil.java:578)
            //            at de.invesdwin.util.streams.buffer.file.MemoryMappedFile$MemoryMappedFileFinalizer.<init>(MemoryMappedFile.java:167)
            return new SequentialFileChunkStorage<>(directory, valueSerde);
        } else {
            return new MappedFileChunkStorage<>(directory, valueSerde, readOnly, closeAllowed);
        }
    }

    @Override
    public File getDirectory() {
        return new File(new File(getBaseDirectory(), ALargePersistentMap.class.getSimpleName()),
                Reflections.getClassSimpleNameNonBlank(newIndexFactory().getClass()));
    }

    protected abstract IPersistentMapFactory<K, ChunkSummary> newIndexFactory();

    @Override
    public int size() {
        return getIndexMap().size();
    }

    @Override
    public boolean isEmpty() {
        return getIndexMap().isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return getIndexMap().containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(final Object key) {
        final ChunkSummary summary = getIndexMap().get(key);
        if (summary == null) {
            return null;
        }
        return getChunkStorage().get(summary);
    }

    @Override
    public V put(final K key, final V value) {
        if (getChunkStorage().isRemovable()) {
            final ChunkSummary existingSummary = getIndexMap().get(key);

            final ChunkSummary newSummary = getChunkStorage().put(value);
            getIndexMap().put(key, newSummary);

            if (existingSummary != null) {
                getChunkStorage().remove(existingSummary);
            }
            return null;
        } else {
            final ChunkSummary newSummary = getChunkStorage().put(value);
            getIndexMap().put(key, newSummary);
            return null;
        }
    }

    @Override
    public V remove(final Object key) {
        if (getChunkStorage().isRemovable()) {
            final ChunkSummary existing = getIndexMap().get(key);
            if (existing != null) {
                getIndexMap().remove(key);
                getChunkStorage().remove(existing);
            }
        } else {
            getIndexMap().remove(key);
        }
        return null;
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        for (final Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        getIndexMap().clear();
        getChunkStorage().clear();
    }

    @Override
    public Set<K> keySet() {
        return getIndexMap().keySet();
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void close() {
        if (indexMap != null) {
            indexMap.close();
            indexMap = null;
        }
        if (chunkStorage != null) {
            getChunkStorage().close();
            chunkStorage = null;
        }
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        final V existing = get(key);
        if (existing != null) {
            return existing;
        } else {
            put(key, value);
            return null;
        }
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V replace(final K key, final V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void deleteTable() {
        close();
        Files.deleteNative(getFile());
    }

    @Override
    public void removeAll(final IKeyMatcher<K> matcher) {
        for (final K key : getIndexMap().keySet()) {
            if (matcher.matches(key)) {
                remove(key);
            }
        }
    }

    @Override
    public V getOrLoad(final K key, final Supplier<V> loadable) {
        final ChunkSummary existingSummary = getIndexMap().get(key);
        if (existingSummary == null) {
            final V loaded = loadable.get();
            put(key, loaded);
            return loaded;
        } else {
            return getChunkStorage().get(existingSummary);
        }
    }

}
