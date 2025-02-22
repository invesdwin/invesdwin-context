package de.invesdwin.context.system.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Collections;

@NotThreadSafe
public class PropertiesAsMap implements Map<String, String> {

    private final IProperties properties;

    public PropertiesAsMap(final IProperties properties) {
        this.properties = properties;
    }

    @Override
    public int size() {
        return properties.size();
    }

    @Override
    public boolean isEmpty() {
        return properties.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return properties.containsKey((String) key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return properties.containsValue((String) value);
    }

    @Override
    public String get(final Object key) {
        return properties.getString((String) key);
    }

    @Override
    public String put(final String key, final String value) {
        final String before = properties.getStringOptional(key);
        properties.setString(key, value);
        return before;
    }

    @Override
    public String remove(final Object key) {
        final String before = properties.getStringOptional((String) key);
        properties.remove((String) key);
        return before;
    }

    @Override
    public void putAll(final Map<? extends String, ? extends String> m) {
        for (final Entry<? extends String, ? extends String> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        properties.clear();
    }

    @Override
    public Set<String> keySet() {
        return new LinkedHashSet<>(properties.getKeys());
    }

    @Override
    public Collection<String> values() {
        final List<String> keys = properties.getKeys();
        final List<String> values = new ArrayList<>(keys.size());
        for (int i = 0; i < keys.size(); i++) {
            final String key = keys.get(i);
            final String value = get(key);
            values.add(value);
        }
        return values;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        final List<String> keys = properties.getKeys();
        final Set<Entry<String, String>> entries = new LinkedHashSet<>(keys.size());
        for (int i = 0; i < keys.size(); i++) {
            final String key = keys.get(i);
            entries.add(new Entry<String, String>() {
                @Override
                public String getKey() {
                    return key;
                }

                @Override
                public String getValue() {
                    return get(key);
                }

                @Override
                public String setValue(final String value) {
                    return put(key, value);
                }
            });
        }
        return entries;
    }

    @Override
    public String toString() {
        return Collections.toString(this);
    }

}
