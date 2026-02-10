package de.invesdwin.context.integration.csv.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.streams.closeable.Closeables;

@NotThreadSafe
public class HtmlTableWriter implements ITableWriter {

    private final Appendable out;
    private final List<Object> currentLine = new ArrayList<Object>();
    private int currentLineColumns = 0;
    private Integer assertColumnCount;
    private int lineIdx = 0;
    private boolean headerRowEnabled = true;
    private HtmlTableTheme theme = HtmlTableTheme.DEFAULT;
    private boolean closeOut = true;

    public HtmlTableWriter(final OutputStream out) {
        this(new OutputStreamWriter(out));
    }

    public HtmlTableWriter(final Appendable out) {
        this.out = out;
    }

    public HtmlTableWriter setCloseOut(final boolean closeOut) {
        this.closeOut = closeOut;
        return this;
    }

    public boolean isCloseOut() {
        return closeOut;
    }

    @Override
    public HtmlTableWriter setAssertColumnCount(final Integer assertColumnCount) {
        this.assertColumnCount = assertColumnCount;
        return this;
    }

    public HtmlTableWriter setHeaderRowEnabled(final boolean headerRowEnabled) {
        this.headerRowEnabled = headerRowEnabled;
        return this;
    }

    public HtmlTableTheme getTheme() {
        return theme;
    }

    public HtmlTableWriter setTheme(final HtmlTableTheme theme) {
        this.theme = theme;
        return this;
    }

    public boolean isHeaderRowEnabled() {
        return headerRowEnabled;
    }

    @Override
    public Integer getAssertColumnCount() {
        return assertColumnCount;
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
    public void newLine() throws IOException {
        line(currentLine, currentLineColumns);
        currentLineColumns = 0;
    }

    @Override
    public void line(final List<?> columns) throws IOException {
        line(columns, columns.size());
    }

    @Override
    public void line(final List<?> columns, final int length) throws IOException {
        assertColumnCount(length);
        if (lineIdx == 0) {
            out.append(theme.tableOpenTag());
            out.append(theme.lineFeed());
            out.append(theme.styleOpenCloseTag());
            out.append(theme.lineFeed());
            if (headerRowEnabled) {
                out.append(theme.theadOpenTag());
            } else {
                out.append(theme.tbodyOpenTag());
            }
            out.append(theme.lineFeed());
        }
        out.append(theme.trOpenTag());
        out.append(theme.lineFeed());
        for (int colIdx = 0; colIdx < length; colIdx++) {
            final Object column = columns.get(colIdx);
            if (lineIdx == 0 && headerRowEnabled) {
                out.append(theme.thOpenTag());
            } else {
                out.append(theme.tdOpenTag(headerRowEnabled, lineIdx, colIdx));
            }
            final String columnStr;
            if (column instanceof String) {
                columnStr = (String) column;
            } else {
                columnStr = Strings.asStringEmptyText(column);
            }
            out.append(columnStr);
            if (lineIdx == 0 && headerRowEnabled) {
                out.append(theme.thCloseTag());
            } else {
                out.append(theme.tdCloseTag(headerRowEnabled, lineIdx, colIdx));
            }
            out.append(theme.lineFeed());
        }
        out.append(theme.trCloseTag());
        out.append(theme.lineFeed());
        if (lineIdx == 0) {
            if (headerRowEnabled) {
                out.append(theme.theadCloseTag());
                out.append(theme.lineFeed());
                out.append(theme.tbodyOpenTag());
                out.append(theme.lineFeed());
            }
        }
        lineIdx++;
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
        out.append(theme.tbodyCloseTag());
        out.append(theme.lineFeed());
        out.append(theme.tableCloseTag());
        currentLine.clear();
        lineIdx = 0;
        if (isCloseOut()) {
            Closeables.closeQuietly(out);
        }
    }

    @Override
    public void flush() throws IOException {
        //noop
    }

    public void appendCustomLine(final String line) throws IOException {
        out.append(line);
    }
}
