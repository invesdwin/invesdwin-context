package de.invesdwin.context.integration.csv;

import java.io.Closeable;
import java.io.IOException;
import java.util.NoSuchElementException;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

public interface IBeanWriter<E> extends Closeable {

    default int write(final ICloseableIterable<? extends E> iterable) throws IOException {
        int count = 0;
        try (ICloseableIterator<? extends E> iterator = iterable.iterator()) {
            while (true) {
                write(iterator.next());
                count++;
            }
        } catch (final NoSuchElementException e) {
            //end reached
        }
        return count;
    }

    default int write(final Iterable<? extends E> iterable) throws IOException {
        int count = 0;
        for (final E e : iterable) {
            write(e);
            count++;
        }
        return count;
    }

    void write(E e) throws IOException;

    void flush() throws IOException;

}
