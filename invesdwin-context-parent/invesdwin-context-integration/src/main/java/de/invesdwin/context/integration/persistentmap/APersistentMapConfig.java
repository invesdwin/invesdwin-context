package de.invesdwin.context.integration.persistentmap;

import java.io.File;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.TypeDelegateSerde;

@NotThreadSafe
public abstract class APersistentMapConfig<K, V> {

    private final String name;

    public APersistentMapConfig(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isDiskPersistence() {
        return true;
    }

    public ISerde<K> newKeySerde() {
        return new TypeDelegateSerde<K>(getKeyType());
    }

    public ISerde<V> newValueSerde() {
        return new TypeDelegateSerde<V>(getValueType());
    }

    @SuppressWarnings("unchecked")
    public Class<K> getKeyType() {
        return (Class<K>) Reflections.resolveTypeArguments(getClass(), APersistentMapConfig.class)[0];
    }

    @SuppressWarnings("unchecked")
    public Class<V> getValueType() {
        return (Class<V>) Reflections.resolveTypeArguments(getClass(), APersistentMapConfig.class)[1];
    }

    public File getFile() {
        return new File(getDirectory(), name);
    }

    public abstract File getDirectory();

    public File getBaseDirectory() {
        return ContextProperties.getHomeDataDirectory();
    }

}