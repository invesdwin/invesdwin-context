package de.invesdwin.context.jcache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.cache.Cache;
import javax.cache.Cache.Entry;

@NotThreadSafe
public abstract class ACacheCollection<E> implements Collection<E> {

    private ACacheMap<Integer, E> delegate;

    protected abstract Cache<Integer, E> createDelegate();

    protected abstract Integer keyOf(final Object o);

    public synchronized ACacheMap<Integer, E> getDelegate() {
        if (delegate == null) {
            delegate = new ACacheMap<Integer, E>() {
                @Override
                protected Cache<Integer, E> createDelegate() {
                    return ACacheCollection.this.createDelegate();
                }
            };
        }
        return delegate;
    }

    @Override
    public int size() {
        return getDelegate().size();
    }

    @Override
    public boolean isEmpty() {
        return getDelegate().isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return getDelegate().containsKey(keyOf(o));
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Iterator<Entry<Integer, E>> iterator = getDelegate().getDelegate().iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public E next() {
                return iterator.next().getValue();
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    /**
     * Might be really slow!
     */
    @Deprecated
    @Override
    public Object[] toArray() {
        return getDelegate().values().toArray();
    }

    /**
     * Might be really slow!
     */
    @Deprecated
    @Override
    public <T> T[] toArray(final T[] a) {
        return getDelegate().values().toArray(a);
    }

    @Override
    public boolean add(final E e) {
        return getDelegate().put(keyOf(e), e) != e;
    }

    @Override
    public boolean remove(final Object o) {
        return getDelegate().getDelegate().remove(keyOf(o));
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        for (final Object o : c) {
            if (!getDelegate().containsKey(keyOf(o))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        boolean changed = false;
        for (final E e : c) {
            if (add(e)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        boolean changed = false;
        for (final Object o : c) {
            if (remove(o)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        final Set<Integer> cKeys = new HashSet<Integer>();
        for (final Object o : c) {
            cKeys.add(keyOf(o));
        }
        boolean changed = false;
        for (final Entry<Integer, E> e : getDelegate().getDelegate()) {
            if (!cKeys.contains(e.getKey())) {
                if (getDelegate().getDelegate().remove(e.getKey())) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    @Override
    public void clear() {
        getDelegate().clear();
    }

}
