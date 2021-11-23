package de.invesdwin.context.integration.persistentmap;

import java.io.File;

import de.invesdwin.util.marshallers.serde.ISerde;

public interface IPersistentMapConfig<K, V> {
    String getName();

    boolean isDiskPersistence();

    ISerde<K> newKeySerde();

    ISerde<V> newValueSerde();

    Class<K> getKeyType();

    Class<V> getValueType();

    File getFile();

    File getDirectory();

    File getBaseDirectory();

}
