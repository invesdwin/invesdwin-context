package de.invesdwin.context.integration.persistentmap.navigable;

import java.util.concurrent.ConcurrentNavigableMap;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.persistentmap.APersistentMapConfig;
import de.invesdwin.context.integration.persistentmap.IPersistentMapFactory;

@Immutable
public interface IPersistentNavigableMapFactory<K, V> extends IPersistentMapFactory<K, V> {

    @Override
    ConcurrentNavigableMap<K, V> newPersistentMap(APersistentMapConfig<K, V> config);

}
