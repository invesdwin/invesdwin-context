package de.invesdwin.context.integration.persistentmap.large.storage;

import java.io.Closeable;

import de.invesdwin.context.integration.persistentmap.large.summary.ChunkSummary;

public interface IChunkStorage<V> extends Closeable {

    V get(ChunkSummary summary);

    void remove(ChunkSummary summary);

    boolean isRemovable();

    ChunkSummary put(V value);

    void clear();

}
