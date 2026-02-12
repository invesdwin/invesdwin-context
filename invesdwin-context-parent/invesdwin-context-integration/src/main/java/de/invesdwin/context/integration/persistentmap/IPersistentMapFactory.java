package de.invesdwin.context.integration.persistentmap;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

@Immutable
public interface IPersistentMapFactory<K, V> {

    Map<K, V> newPersistentMap(IPersistentMapConfig<K, V> config);

    void removeAll(Map<K, V> table, IKeyMatcher<K> matcher);

    boolean isDiskPersistenceSupported();

}
