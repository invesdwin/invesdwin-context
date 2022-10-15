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

@NotThreadSafe
public class HtmlTableWriter implements ITableWriter {

    private final Appendable out;
    private final List<Object> currentLine = new ArrayList<Object>();
    private Integer assertColumnCount;
    private boolean firstLine = true;
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
        if (firstLine) {
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
        for (int i = 0; i < columns.size(); i++) {
            final Object column = columns.get(i);
            if (firstLine && headerRowEnabled) {
                out.append(theme.thOpenTag());
            } else {
                out.append(theme.tdOpenTag());
            }
            final String content = Strings.asStringEmptyText(column);
            out.append(content);
            if (firstLine && headerRowEnabled) {
                out.append(theme.thCloseTag());
            } else {
                out.append(theme.tdCloseTag());
            }
            out.append(theme.lineFeed());
        }
        out.append(theme.trCloseTag());
        out.append(theme.lineFeed());
        if (firstLine) {
            if (headerRowEnabled) {
                out.append(theme.theadCloseTag());
                out.append(theme.lineFeed());
                out.append(theme.tbodyOpenTag());
                out.append(theme.lineFeed());
            }
            firstLine = false;
        }
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
        firstLine = true;
        if (isCloseOut()) {
            Closeables.closeQuietly(out);
        }
    }

    @Override
    public void flush() throws IOException {
        //noop
    }

}
