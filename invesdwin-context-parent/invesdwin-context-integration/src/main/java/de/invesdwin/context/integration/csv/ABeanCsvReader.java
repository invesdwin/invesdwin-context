package de.invesdwin.context.integration.csv;

import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.collections.iterable.ACloseableIterator;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.lang.finalizer.AFinalizer;

@NotThreadSafe
public abstract class ABeanCsvReader<E> extends ACloseableIterator<E> {

    private final BeanCsvReaderFinalizer<E> finalizer;
    private E cachedNext;

    public ABeanCsvReader(final InputStream in) {
        finalizer = new BeanCsvReaderFinalizer<E>();
        try {
            finalizer.itemReader = newItemReader(in);
            finalizer.itemReader.open(new ExecutionContext());
        } catch (final Exception e) {
            throw Err.process(e);
        }
        finalizer.register(this);
    }

    private static final class BeanCsvReaderFinalizer<_E> extends AFinalizer {
        private FlatFileItemReader<_E> itemReader;

        @Override
        protected void clean() {
            itemReader.close();
            itemReader = null;
        }

        @Override
        public boolean isClosed() {
            return itemReader == null;
        }

    }

    protected abstract FlatFileItemReader<E> newItemReader(InputStream in) throws Exception;

    protected abstract boolean isInvalidRow(E row);

    @Override
    protected final boolean innerHasNext() {
        return maybeNext() != null;
    }

    @Override
    protected final E innerNext() {
        final E next = maybeNext();
        if (next == null) {
            throw new FastNoSuchElementException("ABeanCsvReader maybeNext() returned null");
        } else {
            cachedNext = (E) null;
            return next;
        }
    }

    private E maybeNext() {
        if (cachedNext != null) {
            return cachedNext;
        } else {
            if (finalizer.isClosed()) {
                return null;
            }
            do {
                try {
                    cachedNext = finalizer.itemReader.read();
                } catch (final Exception e) {
                    throw Err.process(e);
                }
                if (cachedNext == null) {
                    finalizer.close();
                    return null;
                }
            } while (isInvalidRow(cachedNext));
            return cachedNext;
        }
    }

    @Override
    public void close() {
        super.close();
        finalizer.close();
    }

}
