package de.invesdwin.context.integration.persistentmap;

import java.io.File;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.large.ILargeSerde;

public interface IPersistentMapConfig<K, V> {

    String getName();

    boolean isDiskPersistence();

    ISerde<K> newKeySerde();

    /**
     * has precedence over newValueSerde on supported storages
     */
    ILargeSerde<V> newLargeValueSerde();

    ISerde<V> newValueSerde();

    Class<K> getKeyType();

    Class<V> getValueType();

    File getFile();

    File getDirectory();

    File getBaseDirectory();

}
