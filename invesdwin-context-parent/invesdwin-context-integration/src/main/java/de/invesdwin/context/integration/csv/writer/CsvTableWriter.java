package de.invesdwin.context.integration.csv.writer;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.output.AppendableOutputStream;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.lang.finalizer.AFinalizer;

@NotThreadSafe
public class CsvTableWriter implements Closeable, ITableWriter {

    public static final String DEFAULT_QUOTE = "\"";
    public static final String DEFAULT_COLUMN_SEPARATOR = ",";
    public static final String DEFAULT_NEWLINE = "\n";
    /**
     * Cache the bytes so multiple csv writers share the same byte arrays to preserve memory
     */
    private static final ALoadingCache<String, byte[]> STR_BYTES = new ALoadingCache<String, byte[]>() {

        @Override
        protected Integer getInitialMaximumSize() {
            return 100;
        }

        @Override
        protected byte[] loadValue(final String key) {
            return key.getBytes();
        }
    };

    private final CsvTableWriterFinalizer finalizer;
    private byte[] quoteBytes;
    private byte[] columnSeparatorBytes;
    private byte[] newlineBytes;

    private final List<Object> currentLine = new ArrayList<Object>();
    private Integer assertColumnCount;

    public CsvTableWriter(final Appendable out) {
        this(new AppendableOutputStream<>(out));
    }

    public CsvTableWriter(final OutputStream out) {
        this.finalizer = new CsvTableWriterFinalizer();
        this.finalizer.out = out;
        this.finalizer.register(this);
        setQuote(DEFAULT_QUOTE);
        setColumnSeparator(DEFAULT_COLUMN_SEPARATOR);
        setNewLine(DEFAULT_NEWLINE);
    }

    @Override
    public CsvTableWriter setAssertColumnCount(final Integer assertColumnCount) {
        this.assertColumnCount = assertColumnCount;
        return this;
    }

    public CsvTableWriter setQuote(final String quote) {
        if (Strings.isBlank(quote)) {
            quoteBytes = null;
        } else {
            quoteBytes = STR_BYTES.get(quote);
        }
        return this;
    }

    public CsvTableWriter setColumnSeparator(final String columnSeparator) {
        Assertions.assertThat(columnSeparator).isNotEmpty();
        columnSeparatorBytes = STR_BYTES.get(columnSeparator);
        return this;
    }

    public CsvTableWriter setNewLine(final String newline) {
        Assertions.assertThat(newline).isNotEmpty();
        newlineBytes = STR_BYTES.get(newline);
        return this;
    }

    @Override
    public Integer getAssertColumnCount() {
        return assertColumnCount;
    }

    @Override
    public void column(final Object column) {
        currentLine.add(Strings.asString(column));
    }

    @Override
    public void newLine() throws IOException {
        line(currentLine);
        currentLine.clear();
    }

    @Override
    public void line(final List<?> columns) throws IOException {
        assertColumnCount(columns.size());
        for (int i = 0; i < columns.size(); i++) {
            final Object column = columns.get(i);
            if (column != null) {
                if (quoteBytes != null) {
                    finalizer.out.write(quoteBytes);
                }
                finalizer.out.write(Strings.asStringEmptyText(column).getBytes());
                if (quoteBytes != null) {
                    finalizer.out.write(quoteBytes);
                }
            }
            if (i < columns.size() - 1) {
                finalizer.out.write(columnSeparatorBytes);
            }
        }
        finalizer.out.write(newlineBytes);
    }

    @Override
    public void line(final Object... columns) throws IOException {
        line(Arrays.asList(columns));
    }

    private void assertColumnCount(final int curColumnCount) {
        if (assertColumnCount != null) {
            Assertions.assertThat(curColumnCount)
                    .as("Current column count [%s] does not match expected column count [%s].", curColumnCount,
                            assertColumnCount)
                    .isEqualTo(assertColumnCount);
        }
    }

    @Override
    public final void close() throws IOException {
        finalizer.close();
    }

    @Override
    public void flush() throws IOException {
        finalizer.out.flush();
    }

    private static final class CsvTableWriterFinalizer extends AFinalizer {

        private OutputStream out;

        @Override
        protected void clean() {
            try {
                out.close();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            out = null;
        }

        @Override
        protected boolean isCleaned() {
            return out == null;
        }

        @Override
        public boolean isThreadLocal() {
            return true;
        }

    }

}
