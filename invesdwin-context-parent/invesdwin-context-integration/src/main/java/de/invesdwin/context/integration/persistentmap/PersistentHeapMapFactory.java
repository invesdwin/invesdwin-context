package de.invesdwin.context.integration.persistentmap;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.collections.factory.SynchronizedLockCollectionFactory;

/**
 * Not actually persistent.
 */
@Immutable
public class PersistentHeapMapFactory<K, V> implements IPersistentMapFactory<K, V> {

    @Override
    public Map<K, V> newPersistentMap(final IPersistentMapConfig<K, V> config) {
        return SynchronizedLockCollectionFactory.INSTANCE.newConcurrentMap();
    }

    @Override
    public boolean isDiskPersistenceSupported() {
        return false;
    }

    @Override
    public void removeAll(final Map<K, V> table, final IKeyMatcher<K> matcher) {
        for (final K key : table.keySet()) {
            if (matcher.matches(key)) {
                table.remove(key);
            }
        }
    }

}
