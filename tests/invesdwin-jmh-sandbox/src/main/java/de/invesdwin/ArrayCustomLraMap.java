package de.invesdwin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.GuavaCompactHashMap;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import de.invesdwin.util.math.Integers;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class ArrayCustomLraMap<K, V> implements Map<K, V> {

	private final int maximumSize;
	private Object[] orderedKeys;
	private int leastRecentlyAddedKeyIndex = -1;
	private int mostRecentlyAddedKeyIndex = -1;
	private final Map<K, V> map;

	public ArrayCustomLraMap(int maximumSize) {
		this.maximumSize = maximumSize;
		this.orderedKeys = new Object[0];
		this.map = new GuavaCompactHashMap<>(maximumSize);
	}

	@Override
	public void clear() {
		orderedKeys = new Object[0];
		leastRecentlyAddedKeyIndex = -1;
		mostRecentlyAddedKeyIndex = -1;
		map.clear();
	}

	@Override
	public boolean containsKey(Object arg0) {
		return map.containsKey(arg0);
	}

	@Override
	public boolean containsValue(Object arg0) {
		return map.containsValue(arg0);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	@Override
	public V get(Object arg0) {
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
    public V put(final K key, final V value) {
        final V put = map.put(key, value);
        if (put == null) {
            mostRecentlyAddedKeyIndex++;
            if (mostRecentlyAddedKeyIndex >= maximumSize) {
                mostRecentlyAddedKeyIndex = 0;
            }
            if (orderedKeys.length <= mostRecentlyAddedKeyIndex) {
                final Object[] oldArray = orderedKeys;
                orderedKeys = new Object[Integers.max(1, Integers.min(maximumSize, oldArray.length * 2))];
                System.arraycopy(oldArray, 0, orderedKeys, 0, oldArray.length);
            }
            while (map.size() > maximumSize) {
                do {
                    leastRecentlyAddedKeyIndex++;
                    if (leastRecentlyAddedKeyIndex >= maximumSize) {
                        leastRecentlyAddedKeyIndex = 0;
                    }
                    //jump over removed keys
                } while (orderedKeys[leastRecentlyAddedKeyIndex] == null);
                map.remove(orderedKeys[leastRecentlyAddedKeyIndex]);
                orderedKeys[leastRecentlyAddedKeyIndex] = null;
            }
            orderedKeys[mostRecentlyAddedKeyIndex] = key;
        }
        return put;
    }

	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
		for (Entry<? extends K, ? extends V> e : arg0.entrySet()) {
			put(e.getKey(), e.getValue());
		}
	}

	@Override
	public V remove(Object arg0) {
		V removed = map.remove(arg0);
		if (removed != null) {
			if (map.isEmpty()) {
				orderedKeys = new Object[0];
				leastRecentlyAddedKeyIndex = -1;
				mostRecentlyAddedKeyIndex = -1;
			} else {
				int index = findIndex(arg0);
				if (index == mostRecentlyAddedKeyIndex) {
					orderedKeys[mostRecentlyAddedKeyIndex] = null;
					mostRecentlyAddedKeyIndex--;
				} else {
					orderedKeys[index] = null;
				}
			}
		}
		return removed;
	}

	private int findIndex(Object arg0) {
		for (int i = 0; i < orderedKeys.length; i++) {
			Object cur = orderedKeys[i];
			if (cur != null && cur.equals(arg0)) {
				return i;
			}
		}
		throw new IllegalStateException("Key not found: " + arg0);
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
