package de.invesdwin.context.integration.persistentmap.navigable;

import java.util.concurrent.ConcurrentNavigableMap;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.persistentmap.IPersistentMapConfig;
import de.invesdwin.context.integration.persistentmap.IPersistentMapFactory;

@Immutable
public interface IPersistentNavigableMapFactory<K, V> extends IPersistentMapFactory<K, V> {

    @Deprecated
    @Override
    default ConcurrentNavigableMap<K, V> newPersistentMap(final IPersistentMapConfig<K, V> config) {
        return newPersistentNavigableMap((IPersistentNavigableMapConfig<K, V>) config);
    }

    ConcurrentNavigableMap<K, V> newPersistentNavigableMap(IPersistentNavigableMapConfig<K, V> config);

}
