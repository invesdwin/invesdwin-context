package de.invesdwin.context.integration.csv.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class ABeanTableWriter<E> implements IBeanTableWriter<E> {

    private final ITableWriter csvWriter;
    private boolean headerWritten = false;

    public ABeanTableWriter(final OutputStream out) throws IOException {
        this.csvWriter = newTableWriter(out);
    }

    protected ITableWriter newTableWriter(final OutputStream out) throws IOException {
        final CsvTableWriter csvWriter = new CsvTableWriter(out);
        return csvWriter;
    }

    protected void writeHeaderLine() throws IOException {
        final List<String> headers = getHeaders();
        this.csvWriter.setAssertColumnCount(headers.size());
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
