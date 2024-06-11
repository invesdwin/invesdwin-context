package de.invesdwin.context.integration.csv;

import java.util.NoSuchElementException;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.batch.item.ItemStreamReader;

import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;

@NotThreadSafe
public class ItemStreamReaderCloseableIterator<E> implements ICloseableIterator<E> {

    private ItemStreamReader<? extends E> delegate;
    private E cachedReadNext;
    private final E invalidRow;

    public ItemStreamReaderCloseableIterator(final ItemStreamReader<? extends E> delegate, final E invalidRow) {
        this.delegate = delegate;
        this.invalidRow = invalidRow;
    }

    @Override
    public boolean hasNext() {
        return readNext() != null;
    }

    @Override
    public E next() {
        final E readNext = readNext();
        cachedReadNext = null;
        if (readNext == null) {
            throw FastNoSuchElementException.getInstance("ItemStreamReaderCloseableIterator: readNext is null");
        }
        return readNext;
    }

    private E readNext() {
        if (isClosed()) {
            return null;
        }
        if (cachedReadNext != null) {
            return cachedReadNext;
        } else {
            try {
                while (!isClosed()) {
                    final E next = delegate.read();
                    if (next == null) {
                        close();
                        return null;
                    } else if (!skip(next)) {
                        cachedReadNext = next;
                        break;
                    }
                }
                //catching nosuchelement might be faster sometimes than checking hasNext(), e.g. for LevelDB
            } catch (final NoSuchElementException e) {
                close();
            } catch (final Exception e) {
                return null;
            }
            return cachedReadNext;
        }
    }

    protected boolean skip(final E element) {
        return element == invalidRow;
    }

    @Override
    public void close() {
        delegate.close();
        delegate = null;
    }

    public boolean isClosed() {
        return delegate == null;
    }

}
