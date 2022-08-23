package de.invesdwin.context.integration.persistentmap;

import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.Immutable;

@Immutable
public interface IPersistentMapFactory<K, V> {

    ConcurrentMap<K, V> newPersistentMap(IPersistentMapConfig<K, V> config);

    void removeAll(ConcurrentMap<K, V> table, IKeyMatcher<K> matcher);

    boolean isDiskPersistenceSupported();

}
