package de.invesdwin.context.integration.csv;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.iterable.ICloseableIterable;
import de.invesdwin.util.collections.iterable.ICloseableIterator;

@NotThreadSafe
public abstract class ABeanCsvWriter<E> implements Closeable {

    private final CsvWriter csvWriter;
    private boolean headerWritten = false;

    public ABeanCsvWriter(final OutputStream out) throws IOException {
        this.csvWriter = newCsvWriter(out);
    }

    protected CsvWriter newCsvWriter(final OutputStream out) throws IOException {
        final CsvWriter csvWriter = new CsvWriter(out);
        return csvWriter;
    }

    protected void writeHeaderLine() throws IOException {
        final List<String> headers = getHeaders();
        this.csvWriter.withAssertColumnCount(headers.size());
        this.csvWriter.line(headers);
    }

    public final void write(final ICloseableIterable<? extends E> iterable) throws IOException {
        try (ICloseableIterator<? extends E> iterator = iterable.iterator()) {
            while (iterator.hasNext()) {
                write(iterator.next());
            }
        }
    }

    public final void write(final Iterable<? extends E> iterable) throws IOException {
        for (final E e : iterable) {
            write(e);
        }
    }

    public final void write(final E e) throws IOException {
        if (!headerWritten) {
            writeHeaderLine();
            headerWritten = true;
        }
        final List<?> element = getElement(e);
        csvWriter.line(element);
    }

    protected abstract List<String> getHeaders();

    protected abstract List<?> getElement(E e);

    @Override
    public final void close() throws IOException {
        csvWriter.close();
    }

    public void flush() throws IOException {
        csvWriter.flush();
    }

}
