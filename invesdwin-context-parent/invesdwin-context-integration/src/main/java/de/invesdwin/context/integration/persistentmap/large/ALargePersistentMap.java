package de.invesdwin.context.integration.persistentmap.large;

import java.io.File;
import java.io.IOException;
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
import de.invesdwin.context.integration.persistentmap.large.storage.FileChunkStorage;
import de.invesdwin.context.integration.persistentmap.large.storage.IChunkStorage;
import de.invesdwin.context.integration.persistentmap.large.storage.MappedChunkStorage;
import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummary;
import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummarySerde;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.marshallers.serde.ISerde;

@ThreadSafe
public abstract class ALargePersistentMap<K, V> extends APersistentMapConfig<K, V> implements IPersistentMap<K, V> {

    private final APersistentMap<K, ChunkSummary> indexMap = newIndexMap();

    private final IChunkStorage<V> chunkStorage = newChunkStorage(new File(getFile(), "storage"));

    public ALargePersistentMap(final String name) {
        super(name);
    }

    protected APersistentMap<K, ChunkSummary> newIndexMap() {
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

    protected IChunkStorage<V> newChunkStorage(final File directory) {
        if (isHighConcurrency()) {
            return new FileChunkStorage<>(directory, newValueSerde());
        } else {
            return new MappedChunkStorage<>(directory, newValueSerde());
        }
    }

    protected boolean isHighConcurrency() {
        return false;
    }

    @Override
    public File getDirectory() {
        return new File(new File(getBaseDirectory(), ALargePersistentMap.class.getSimpleName()),
                Reflections.getClassSimpleNameNonBlank(newIndexFactory().getClass()));
    }

    protected abstract IPersistentMapFactory<K, ChunkSummary> newIndexFactory();

    @Override
    public int size() {
        return indexMap.size();
    }

    @Override
    public boolean isEmpty() {
        return indexMap.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return indexMap.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(final Object key) {
        final ChunkSummary summary = indexMap.get(key);
        if (summary == null) {
            return null;
        }
        return chunkStorage.get(summary);
    }

    @Override
    public V put(final K key, final V value) {
        if (chunkStorage.isRemovable()) {
            final ChunkSummary existingSummary = indexMap.get(key);

            final ChunkSummary newSummary = chunkStorage.put(value);
            indexMap.put(key, newSummary);

            if (existingSummary != null) {
                chunkStorage.remove(existingSummary);
            }
            return null;
        } else {
            final ChunkSummary newSummary = chunkStorage.put(value);
            indexMap.put(key, newSummary);
            return null;
        }
    }

    @Override
    public V remove(final Object key) {
        if (chunkStorage.isRemovable()) {
            final ChunkSummary existing = indexMap.get(key);
            if (existing != null) {
                indexMap.remove(key);
                chunkStorage.remove(existing);
            }
        } else {
            indexMap.remove(key);
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
        indexMap.clear();
        chunkStorage.clear();
    }

    @Override
    public Set<K> keySet() {
        return indexMap.keySet();
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
    public void close() throws IOException {
        indexMap.close();
        chunkStorage.close();
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
    public void deleteTable() {
        clear();
    }

    @Override
    public void removeAll(final IKeyMatcher<K> matcher) {
        for (final K key : indexMap.keySet()) {
            if (matcher.matches(key)) {
                remove(key);
            }
        }
    }

    @Override
    public V getOrLoad(final K key, final Supplier<V> loadable) {
        final ChunkSummary existingSummary = indexMap.get(key);
        if (existingSummary == null) {
            final V loaded = loadable.get();
            put(key, loaded);
            return loaded;
        } else {
            return chunkStorage.get(existingSummary);
        }
    }

}
