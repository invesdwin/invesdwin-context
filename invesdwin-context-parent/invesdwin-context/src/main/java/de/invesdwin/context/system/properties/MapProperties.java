package de.invesdwin.context.system.properties;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.configuration2.AbstractConfiguration;

@ThreadSafe
public class MapProperties extends AProperties {

    private final Map<String, Object> map;

    public MapProperties() {
        this(new HashMap<>());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MapProperties(final Properties props) {
        this((Map) props);
    }

    @SuppressWarnings("unchecked")
    public MapProperties(final Map<String, ?> map) {
        this.map = (Map<String, Object>) map;
    }

    @Override
    protected AbstractConfiguration createDelegate() {
        return new AbstractConfiguration() {
            @Override
            protected boolean isEmptyInternal() {
                return map.isEmpty();
            }

            @Override
            protected Object getPropertyInternal(final String key) {
                return map.get(key);
            }

            @Override
            protected Iterator<String> getKeysInternal() {
                return map.keySet().iterator();
            }

            @Override
            protected boolean containsKeyInternal(final String key) {
                return map.containsKey(key);
            }

            @Override
            protected void clearPropertyDirect(final String key) {
                map.remove(key);
            }

            @Override
            protected void addPropertyDirect(final String key, final Object value) {
                map.put(key, value);
            }

            @Override
            protected boolean containsValueInternal(final Object value) {
                return map.containsValue(value);
            }
        };
    }

}
