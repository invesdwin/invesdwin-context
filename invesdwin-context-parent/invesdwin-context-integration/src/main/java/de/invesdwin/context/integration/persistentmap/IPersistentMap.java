package de.invesdwin.context.integration.persistentmap;

import java.io.Closeable;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public interface IPersistentMap<K, V> extends ConcurrentMap<K, V>, Closeable, IPersistentMapConfig<K, V> {

    void deleteTable();

    void removeAll(IKeyMatcher<K> matcher);

    V getOrLoad(K key, Supplier<V> loadable);

    @Override
    void close();

}
