package de.invesdwin.context.integration.csv.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.lang.Closeables;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.Integers;

@NotThreadSafe
public class AsciiTableWriter implements ITableWriter {

    protected final List<String[]> rows = new ArrayList<>();
    private final Appendable out;
    private final List<Object> currentLine = new ArrayList<Object>();
    private int currentLineColumns = 0;
    private Integer assertColumnCount;
    private AsciiTableTheme theme = AsciiTableTheme.DEFAULT;

    public AsciiTableWriter(final OutputStream out) {
        this(new OutputStreamWriter(out));
    }

    public AsciiTableWriter(final Appendable out) {
        this.out = out;
    }

    @Override
    public AsciiTableWriter setAssertColumnCount(final Integer assertColumnCount) {
        this.assertColumnCount = assertColumnCount;
        return this;
    }

    @Override
    public Integer getAssertColumnCount() {
        return assertColumnCount;
    }

    public AsciiTableTheme getTheme() {
        return theme;
    }

    public AsciiTableWriter setTheme(final AsciiTableTheme theme) {
        this.theme = theme;
        return this;
    }

    @Override
    public void column(final Object column) {
        final String columnStr = Strings.asString(column);
        if (currentLine.size() > currentLineColumns) {
            currentLine.set(currentLineColumns, columnStr);
        } else {
            currentLine.add(columnStr);
        }
        currentLineColumns++;
    }

    @Override
    public void newLine() {
        line(currentLine, currentLineColumns);
        currentLineColumns = 0;
    }

    @Override
    public void line(final List<?> columns) {
        line(columns, columns.size());
    }

    @Override
    public void line(final List<?> columns, final int length) {
        assertColumnCount(length);
        final String[] row = new String[length];
        for (int i = 0; i < length; i++) {
            final Object column = columns.get(i);
            final String columnStr;
            if (column instanceof String) {
                columnStr = (String) column;
            } else {
                columnStr = Strings.asStringEmptyText(column);
            }
            row[i] = columnStr;
        }
        rows.add(row);
    }

    @Override
    public void line(final Object... columns) {
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
        currentLine.clear();
        currentLineColumns = 0;
        if (rows.size() <= 1) {
            return;
        }

        final int[] columnWidths = calculateColumnWidths();
        theme.renderOuterBorderForTop(columnWidths, out);
        theme.renderHeaderLine(rows.get(0), columnWidths, out);
        theme.renderHeaderForSeparator(columnWidths, out);
        for (int i = 1; i < rows.size() - 1; i++) {
            theme.renderContentLine(rows.get(i), columnWidths, out);
            theme.renderBodyRowSeparator(columnWidths, out);
        }
        theme.renderContentLine(rows.get(rows.size() - 1), columnWidths, out);
        theme.renderOuterBorderForBottom(columnWidths, out);

        rows.clear();

        Closeables.closeQuietly(out);
    }

    protected int[] calculateColumnWidths() {
        final int[] columnWidths = new int[rows.get(0).length];
        for (int c = 0; c < columnWidths.length; c++) {
            int maxColumnWidth = getMinimumColumnWidth();
            for (int r = getColumnWidthFirstRow(); r < rows.size(); r++) {
                final String content = rows.get(r)[c];
                maxColumnWidth = Integers.max(maxColumnWidth, content.length());
            }
            columnWidths[c] = maxColumnWidth;
        }
        return columnWidths;
    }

    /**
     * Provide a higher value to adjust wrapping of headers
     */
    protected int getMinimumColumnWidth() {
        return 0;
    }

    /**
     * Return 0 to include header in column width calculation
     */
    protected int getColumnWidthFirstRow() {
        return 1;
    }

    @Override
    public void flush() throws IOException {
        //noop
    }

}
