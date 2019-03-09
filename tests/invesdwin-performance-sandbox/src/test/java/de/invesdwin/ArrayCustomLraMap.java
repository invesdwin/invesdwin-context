package de.invesdwin;

import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.collect.GuavaCompactHashMap;

import de.invesdwin.util.collections.eviction.ArrayLeastRecentlyAddedMap;

@NotThreadSafe
public class ArrayCustomLraMap<K, V> extends ArrayLeastRecentlyAddedMap<K, V> {

    public ArrayCustomLraMap(final int maximumSize) {
        super(maximumSize);
    }

    @Override
    protected Map<K, V> newMap() {
        return new GuavaCompactHashMap<>();
    }

}