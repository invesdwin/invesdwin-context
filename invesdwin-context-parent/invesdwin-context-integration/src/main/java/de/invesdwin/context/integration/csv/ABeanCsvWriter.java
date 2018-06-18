package de.invesdwin.context.integration.csv;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ABeanCsvWriter<E> implements IBeanWriter<E> {

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

    @Override
    public void write(final E e) throws IOException {
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

    @Override
    public void flush() throws IOException {
        csvWriter.flush();
    }

}
