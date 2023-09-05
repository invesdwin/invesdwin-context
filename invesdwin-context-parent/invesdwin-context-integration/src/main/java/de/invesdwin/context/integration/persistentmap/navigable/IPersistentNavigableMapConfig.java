package de.invesdwin.context.integration.persistentmap.navigable;

import java.util.Comparator;

import de.invesdwin.context.integration.persistentmap.IPersistentMapConfig;

public interface IPersistentNavigableMapConfig<K, V> extends IPersistentMapConfig<K, V> {

    Comparator<K> newComparator();

}
