package de.invesdwin.context.graalvm.jsr223;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.script.Bindings;

import org.graalvm.polyglot.Value;

@NotThreadSafe
public class PolyglotBindings implements Bindings {
    private final Value languageBindings;

    public PolyglotBindings(final Value languageBindings) {
        this.languageBindings = languageBindings;
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsValue(final Object value) {
        for (final String s : keySet()) {
            if (get(s) == value) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        for (final String s : keySet()) {
            remove(s);
        }
    }

    @Override
    public Set<String> keySet() {
        return languageBindings.getMemberKeys();
    }

    @Override
    public Collection<Object> values() {
        final List<Object> values = new ArrayList<>();
        for (final String s : keySet()) {
            values.add(get(s));
        }
        return values;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        final Set<Entry<String, Object>> values = new HashSet<>();
        for (final String s : keySet()) {
            values.add(new Entry<String, Object>() {
                @Override
                public String getKey() {
                    return s;
                }

                @Override
                public Object getValue() {
                    return get(s);
                }

                @Override
                public Object setValue(final Object value) {
                    return put(s, value);
                }
            });
        }
        return values;
    }

    @Override
    public Object put(final String name, final Object value) {
        final Object previous = get(name);
        languageBindings.putMember(name, value);
        return previous;
    }

    @Override
    public void putAll(final Map<? extends String, ? extends Object> toMerge) {
        for (final Entry<? extends String, ? extends Object> e : toMerge.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public boolean containsKey(final Object key) {
        if (key instanceof String) {
            return languageBindings.hasMember((String) key);
        } else {
            return false;
        }
    }

    @Override
    public Object get(final Object key) {
        if (key instanceof String) {
            final Value value = languageBindings.getMember((String) key);
            if (value != null) {
                return value.as(Object.class);
            }
        }
        return null;
    }

    @Override
    public Object remove(final Object key) {
        final Object prev = get(key);
        if (prev != null) {
            languageBindings.removeMember((String) key);
            return prev;
        } else {
            return null;
        }
    }
}