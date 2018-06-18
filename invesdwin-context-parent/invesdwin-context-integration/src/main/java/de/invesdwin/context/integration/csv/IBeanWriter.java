package de.invesdwin.context.integration.csv;

import java.io.Closeable;
import java.io.IOException;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

public interface IBeanWriter<E> extends Closeable {

    default void write(final ICloseableIterable<? extends E> iterable) throws IOException {
        try (ICloseableIterator<? extends E> iterator = iterable.iterator()) {
            while (iterator.hasNext()) {
                write(iterator.next());
            }
        }
    }

    default void write(final Iterable<? extends E> iterable) throws IOException {
        for (final E e : iterable) {
            write(e);
        }
    }

    void write(E e) throws IOException;

    void flush() throws IOException;

}
