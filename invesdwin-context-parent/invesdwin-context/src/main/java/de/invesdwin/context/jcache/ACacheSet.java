package de.invesdwin.context.jcache;

import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ACacheSet<E> extends ACacheCollection<E> implements Set<E> {

    @Override
    protected final Integer keyOf(final Object o) {
        if (o == null) {
            return null;
        } else {
            return o.hashCode();
        }
    }

}
