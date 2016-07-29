package de.invesdwin.context.integration.csv;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Strings;

@NotThreadSafe
public abstract class ABeanCsvWriter<E> implements Closeable {

    private final CsvWriter csvWriter;

    public ABeanCsvWriter(final OutputStream out) throws IOException {
        this.csvWriter = newCsvWriter(out);
        writeHeaderLine();
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

    public final void write(final Iterable<? extends E> iterable) throws IOException {
        for (final E e : iterable) {
            write(e);
        }
    }

    public final void write(final E e) throws IOException {
        final List<?> element = getElement(e);
        final List<String> str = new ArrayList<String>(element.size());
        for (final Object obj : element) {
            str.add(Strings.asStringEmptyText(obj));
        }
        csvWriter.line(str);
    }

    protected abstract List<String> getHeaders();

    protected abstract List<?> getElement(final E e);

    @Override
    public final void close() throws IOException {
        csvWriter.close();
    }

}
