package de.invesdwin.context.integration.persistentmap.navigable;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.persistentmap.APersistentMapConfig;

@NotThreadSafe
public abstract class APersistentNavigableMapConfig<K, V> extends APersistentMapConfig<K, V>
        implements IPersistentNavigableMapConfig<K, V> {

    public APersistentNavigableMapConfig(final String name) {
        super(name);
    }

}
