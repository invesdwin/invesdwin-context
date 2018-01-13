package de.invesdwin.context.integration.csv;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.lang.Strings;

@NotThreadSafe
public class CsvWriter implements Closeable {

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

    private final OutputStream out;
    private byte[] quoteBytes;
    private byte[] columnSeparatorBytes;
    private byte[] newlineBytes;

    private final List<Object> currentLine = new ArrayList<Object>();
    private Integer assertColumnCount;

    public CsvWriter(final OutputStream out) {
        this.out = out;
        withQuote(DEFAULT_QUOTE);
        withColumnSeparator(DEFAULT_COLUMN_SEPARATOR);
        withNewLine(DEFAULT_NEWLINE);
    }

    public CsvWriter withAssertColumnCount(final Integer assertColumnCount) {
        this.assertColumnCount = assertColumnCount;
        return this;
    }

    public CsvWriter withQuote(final String quote) {
        if (Strings.isBlank(quote)) {
            quoteBytes = null;
        } else {
            quoteBytes = STR_BYTES.get(quote);
        }
        return this;
    }

    public CsvWriter withColumnSeparator(final String columnSeparator) {
        Assertions.assertThat(columnSeparator).isNotEmpty();
        columnSeparatorBytes = STR_BYTES.get(columnSeparator);
        return this;
    }

    public CsvWriter withNewLine(final String newline) {
        Assertions.assertThat(newline).isNotEmpty();
        newlineBytes = STR_BYTES.get(newline);
        return this;
    }

    public Integer getAssertColumnCount() {
        return assertColumnCount;
    }

    public void column(final Object column) {
        currentLine.add(Strings.asString(column));
    }

    public void newLine() throws IOException {
        line(currentLine);
        currentLine.clear();
    }

    public void line(final List<?> columns) throws IOException {
        assertColumnCount(columns.size());
        for (int i = 0; i < columns.size(); i++) {
            final Object column = columns.get(i);
            if (column != null) {
                if (quoteBytes != null) {
                    out.write(quoteBytes);
                }
                out.write(Strings.asStringEmptyText(column).getBytes());
                if (quoteBytes != null) {
                    out.write(quoteBytes);
                }
            }
            if (i < columns.size() - 1) {
                out.write(columnSeparatorBytes);
            }
        }
        out.write(newlineBytes);
    }

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
        out.close();
    }

    public void flush() throws IOException {
        out.flush();
    }

}
