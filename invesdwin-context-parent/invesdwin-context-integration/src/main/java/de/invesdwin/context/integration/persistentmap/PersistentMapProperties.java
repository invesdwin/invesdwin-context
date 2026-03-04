package de.invesdwin.context.integration.persistentmap;

import java.io.File;
import java.util.Iterator;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.configuration2.AbstractConfiguration;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.system.properties.AProperties;

@ThreadSafe
public class PersistentMapProperties extends AProperties {

    private final APersistentMap<String, Object> table;

    public PersistentMapProperties(final String name, final IPersistentMapFactory<String, Object> factory) {
        if (factory == null) {
            throw new NullPointerException("factory should not be null");
        }
        this.table = newPersistentMap(name, factory);
    }

    protected APersistentMap<String, Object> newPersistentMap(final String name,
            final IPersistentMapFactory<String, Object> pFactory) {
        return new APersistentMap<String, Object>(name) {

            @Override
            public File getDirectory() {
                return new File(PersistentMapProperties.this.getBaseDirectory(),
                        PersistentMapProperties.class.getSimpleName());
            }

            @Override
            protected IPersistentMapFactory<String, Object> newFactory() {
                return pFactory;
            }
        };
    }

    protected File getBaseDirectory() {
        return ContextProperties.getHomeDataDirectory();
    }

    @Override
    protected AbstractConfiguration createDelegate() {
        return new AbstractConfiguration() {
            @Override
            protected boolean isEmptyInternal() {
                return table.isEmpty();
            }

            @Override
            protected Object getPropertyInternal(final String key) {
                return table.get(key);
            }

            @Override
            protected Iterator<String> getKeysInternal() {
                return table.keySet().iterator();
            }

            @Override
            protected boolean containsKeyInternal(final String key) {
                return table.containsKey(key);
            }

            @Override
            protected void clearPropertyDirect(final String key) {
                table.remove(key);
            }

            @Override
            protected void addPropertyDirect(final String key, final Object value) {
                table.put(key, value);
            }

            @Override
            protected boolean containsValueInternal(final Object value) {
                return table.containsValue(value);
            }
        };
    }

}
