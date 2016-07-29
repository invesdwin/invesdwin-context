package de.invesdwin.context.integration.csv;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Strings;

@NotThreadSafe
public class CsvWriter implements Closeable {

    public static final String DEFAULT_QUOTE = "\"";
    public static final String DEFAULT_COLUMN_SEPARATOR = ",";
    public static final String DEFAULT_NEWLINE = "\n";

    private final OutputStream out;
    private byte[] quoteBytes = DEFAULT_QUOTE.getBytes();
    private byte[] columnSeparatorBytes = DEFAULT_COLUMN_SEPARATOR.getBytes();
    private byte[] newlineBytes = DEFAULT_NEWLINE.getBytes();

    private final List<String> currentLine = new ArrayList<String>();
    private Integer assertColumnCount;

    public CsvWriter(final OutputStream out) {
        this.out = out;
    }

    public CsvWriter withAssertColumnCount(final Integer assertColumnCount) {
        this.assertColumnCount = assertColumnCount;
        return this;
    }

    public CsvWriter withQuote(final String quote) {
        if (Strings.isBlank(quote)) {
            quoteBytes = null;
        } else {
            quoteBytes = quote.getBytes();
        }
        return this;
    }

    public CsvWriter withColumnSeparator(final String columnSeparator) {
        Assertions.assertThat(columnSeparator).isNotEmpty();
        columnSeparatorBytes = columnSeparator.getBytes();
        return this;
    }

    public CsvWriter withNewLine(final String newline) {
        Assertions.assertThat(newline).isNotBlank();
        newlineBytes = newline.getBytes();
        return this;
    }

    public Integer getAssertColumnCount() {
        return assertColumnCount;
    }

    public void column(final String column) {
        currentLine.add(column);
    }

    public void newLine() throws IOException {
        line(currentLine);
        currentLine.clear();
    }

    public void line(final List<String> columns) throws IOException {
        assertColumnCount(columns.size());
        for (int i = 0; i < columns.size(); i++) {
            final String column = columns.get(i);
            if (column != null) {
                if (quoteBytes != null) {
                    out.write(quoteBytes);
                }
                out.write(column.getBytes());
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

    public void line(final String... columns) throws IOException {
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

}
