package de.invesdwin.context.integration.persistentmap.large;

import java.io.File;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.persistentmap.IKeyMatcher;
import de.invesdwin.context.integration.persistentmap.IPersistentMapConfig;
import de.invesdwin.context.integration.persistentmap.IPersistentMapFactory;
import de.invesdwin.context.integration.persistentmap.large.storage.IChunkStorage;
import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummary;
import de.invesdwin.util.marshallers.serde.ISerde;

@Immutable
public class LargePersistentMapFactory<K, V> implements IPersistentMapFactory<K, V> {

    private final IPersistentMapFactory<K, ChunkSummary> indexFactory;
    private final boolean readOnly;

    public LargePersistentMapFactory(final IPersistentMapFactory<K, ChunkSummary> indexFactory) {
        this(indexFactory, true);
    }

    public LargePersistentMapFactory(final IPersistentMapFactory<K, ChunkSummary> indexFactory,
            final boolean readOnly) {
        this.indexFactory = indexFactory;
        this.readOnly = readOnly;
    }

    public IPersistentMapFactory<K, ChunkSummary> getIndexFactory() {
        return indexFactory;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public boolean isDiskPersistenceSupported() {
        return indexFactory.isDiskPersistenceSupported();
    }

    protected IChunkStorage<V> newChunkStorage(final File directory, final ISerde<V> valueSerde,
            final boolean readOnly) {
        return ALargePersistentMap.newDefaultChunkStorage(directory, valueSerde, readOnly);
    }

    @Override
    public final ConcurrentMap<K, V> newPersistentMap(final IPersistentMapConfig<K, V> config) {
        return new ALargePersistentMap<K, V>(config.getName()) {
            @Override
            protected IPersistentMapFactory<K, ChunkSummary> newIndexFactory() {
                return indexFactory;
            }

            @Override
            protected boolean isReadOnly() {
                return readOnly;
            }

            @Override
            protected IChunkStorage<V> newChunkStorage(final File directory, final ISerde<V> valueSerde,
                    final boolean readOnly) {
                return LargePersistentMapFactory.this.newChunkStorage(directory, valueSerde, readOnly);
            }

            @Override
            public boolean isDiskPersistence() {
                return config.isDiskPersistence();
            }

            @Override
            public ISerde<K> newKeySerde() {
                return config.newKeySerde();
            }

            @Override
            public ISerde<V> newValueSerde() {
                return config.newValueSerde();
            }

            @Override
            public Class<K> getKeyType() {
                return config.getKeyType();
            }

            @Override
            public Class<V> getValueType() {
                return config.getValueType();
            }

            @Override
            public File getFile() {
                return config.getFile();
            }

            @Override
            public File getDirectory() {
                return config.getDirectory();
            }

            @Override
            public File getBaseDirectory() {
                return config.getBaseDirectory();
            }
        };
    }

    @Override
    public void removeAll(final ConcurrentMap<K, V> table, final IKeyMatcher<K> matcher) {
        final ALargePersistentMap<K, V> cTable = (ALargePersistentMap<K, V>) table;
        cTable.removeAll(matcher);
    }

}
