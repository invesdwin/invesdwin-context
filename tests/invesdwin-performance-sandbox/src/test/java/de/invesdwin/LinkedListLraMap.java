package de.invesdwin;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@NotThreadSafe
public class LinkedListLraMap<K, V> implements Map<K, V> {

    private final int maximumSize;
    private final List<K> orderedKeys;
    private final Map<K, V> map = new Object2ObjectOpenHashMap<>();

    public LinkedListLraMap(final int maximumSize) {
        this.maximumSize = maximumSize;
        this.orderedKeys = new LinkedList<>();
    }

    @Override
    public void clear() {
        orderedKeys.clear();
        map.clear();
    }

    @Override
    public boolean containsKey(final Object arg0) {
        return map.containsKey(arg0);
    }

    @Override
    public boolean containsValue(final Object arg0) {
        return map.containsValue(arg0);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public V get(final Object arg0) {
        return map.get(arg0);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public V put(final K arg0, final V arg1) {
        final V put = map.put(arg0, arg1);
        if (put == null) {
            orderedKeys.add(arg0);
            while (orderedKeys.size() > maximumSize) {
                map.remove(orderedKeys.remove(0));
            }
        }
        return put;
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> arg0) {
        for (final Entry<? extends K, ? extends V> e : arg0.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public V remove(final Object arg0) {
        final V removed = map.remove(arg0);
        if (removed != null) {
            orderedKeys.remove(arg0);
        }
        return removed;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

}
