package de.invesdwin.context.integration.csv.writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.string.Strings;

/**
 * https://github.com/iNamik/java_text_tables/blob/dc624fdf7b03ca20aa070c5af5f5fb86fba1e0c5/src/main/java/com/inamik/text/tables/grid/Border.java#L185
 *
 */
@Immutable
public class AsciiTableTheme {

    public static final AsciiTableTheme SPACE = AsciiTableTheme.of(' ');
    public static final AsciiTableTheme STAR = AsciiTableTheme.of('*');
    public static final AsciiTableTheme MATH = AsciiTableTheme.of('+', '-', '|');
    public static final AsciiTableTheme DOUBLE_LINE = new AsciiTableTheme('╬', '═', '║', '╔', '╦', '╗', '╠', '╣', '╚',
            '╩', '╝');
    public static final AsciiTableTheme SINGLE_LINE = new AsciiTableTheme('┼', '─', '│', '┌', '┬', '┐', '├', '┤', '└',
            '┴', '┘');

    //CHECKSTYLE:OFF
    public static final AsciiTableTheme DEFAULT = AsciiTableTheme.SINGLE_LINE.withoutBodyRowSeparator();
    //CHECKSTYLE:ON
    private static final char NONE = Character.MIN_VALUE;

    private final char intersect;
    private final char horizontal;
    private final char vertical;
    private final char topLeft;
    private final char topIntersect;
    private final char topRight;
    private final char leftIntersect;
    private final char rightIntersect;
    private final char bottomLeft;
    private final char bottomIntersect;
    private final char bottomRight;
    private final String verticalSeparator;
    private final char space;
    private final String lineFeed;
    private final boolean headerRowSeparatorEnabled;
    private final boolean bodyRowSeparatorEnabled;
    private final boolean outerBorderEnabled;

    public AsciiTableTheme(final AsciiTableTheme copyOf, final boolean headerRowSeparatorEnabled,
            final boolean bodyRowSeparatorEnabled, final boolean outerBorderEnabled) {
        this.intersect = copyOf.intersect;
        this.horizontal = copyOf.horizontal;
        this.vertical = copyOf.vertical;
        this.topLeft = copyOf.topLeft;
        this.topIntersect = copyOf.topIntersect;
        this.topRight = copyOf.topRight;
        this.leftIntersect = copyOf.leftIntersect;
        this.rightIntersect = copyOf.rightIntersect;
        this.bottomLeft = copyOf.bottomLeft;
        this.bottomIntersect = copyOf.bottomIntersect;
        this.bottomRight = copyOf.bottomRight;
        this.space = copyOf.space;
        this.lineFeed = copyOf.lineFeed;
        this.headerRowSeparatorEnabled = headerRowSeparatorEnabled;
        this.bodyRowSeparatorEnabled = bodyRowSeparatorEnabled;
        this.outerBorderEnabled = outerBorderEnabled;
        this.verticalSeparator = newVerticalSeparator(vertical, space);
    }

    //CHECKSTYLE:OFF
    public AsciiTableTheme(final char intersect, final char horizontal, final char vertical, final char topLeft,
            final char topIntersect, final char topRight, final char leftIntersect, final char rightIntersect,
            final char bottomLeft, final char bottomIntersect, final char bottomRight) {
        //CHECKSTYLE:ON
        this(intersect, horizontal, vertical, topLeft, topIntersect, topRight, leftIntersect, rightIntersect,
                bottomLeft, bottomIntersect, bottomRight, true, true, true);
    }

    //CHECKSTYLE:OFF
    public AsciiTableTheme(final char intersect, final char horizontal, final char vertical, final char topLeft,
            final char topIntersect, final char topRight, final char leftIntersect, final char rightIntersect,
            final char bottomLeft, final char bottomIntersect, final char bottomRight,
            final boolean headerRowSeparatorEnabled, final boolean bodyRowSeparatorEnabled,
            final boolean outerBorderEnabled) {
        //CHECKSTYLE:ON
        this.intersect = intersect;
        this.horizontal = horizontal;
        this.vertical = vertical;
        this.topLeft = topLeft;
        this.topIntersect = topIntersect;
        this.topRight = topRight;
        this.leftIntersect = leftIntersect;
        this.rightIntersect = rightIntersect;
        this.bottomLeft = bottomLeft;
        this.bottomIntersect = bottomIntersect;
        this.bottomRight = bottomRight;
        this.space = newSpace();
        this.lineFeed = newLineFeed();
        this.headerRowSeparatorEnabled = headerRowSeparatorEnabled;
        this.bodyRowSeparatorEnabled = bodyRowSeparatorEnabled;
        this.outerBorderEnabled = outerBorderEnabled;
        this.verticalSeparator = newVerticalSeparator(vertical, space);
    }

    protected String newLineFeed() {
        return "\n";
    }

    protected char newSpace() {
        return ' ';
    }

    /**
     * not for overrides
     */
    private static String newVerticalSeparator(final char vertical, final char space) {
        final StringBuilder sb = new StringBuilder();
        if (vertical == NONE || vertical == space) {
            sb.append(space);
        } else {
            sb.append(space);
            sb.append(vertical);
            sb.append(space);
        }
        return sb.toString();
    }

    private char getHorizontalSeparator(final char verticalSeparatorChar, final char intersect) {
        if (verticalSeparatorChar != space) {
            return intersect;
        } else {
            return horizontal;
        }
    }

    public String newPaddedColumnRight(final String column, final int columnWidth) {
        if (column == null) {
            return newEmptyColumn(columnWidth);
        }
        return column + Strings.repeat(space, columnWidth - column.length());
    }

    public String newPaddedColumnLeft(final String column, final int columnWidth) {
        if (column == null) {
            return newEmptyColumn(columnWidth);
        }
        return Strings.repeat(space, columnWidth - column.length()) + column;
    }

    public String newEmptyColumn(final int columnWidth) {
        return Strings.repeat(space, columnWidth);
    }

    public void renderHeaderForSeparator(final int[] columnWidths, final Appendable out) throws IOException {
        if (!headerRowSeparatorEnabled) {
            return;
        }
        renderHorizontalBorder(columnWidths, out, leftIntersect, intersect, rightIntersect);
        out.append(lineFeed);
    }

    public void renderBodyRowSeparator(final int[] columnWidths, final Appendable out) throws IOException {
        if (!bodyRowSeparatorEnabled) {
            return;
        }
        renderHorizontalBorder(columnWidths, out, leftIntersect, intersect, rightIntersect);
        out.append(lineFeed);
    }

    public void renderOuterBorderForTop(final int[] columnWidths, final Appendable out) throws IOException {
        if (!outerBorderEnabled) {
            return;
        }
        renderHorizontalBorder(columnWidths, out, topLeft, topIntersect, topRight);
        out.append(lineFeed);
    }

    public void renderOuterBorderForBottom(final int[] columnWidths, final Appendable out) throws IOException {
        if (!outerBorderEnabled) {
            return;
        }
        renderHorizontalBorder(columnWidths, out, bottomLeft, bottomIntersect, bottomRight);
    }

    protected void renderHorizontalBorder(final int[] columnWidths, final Appendable out, final char leftIntersect,
            final char intersect, final char rightIntersect) throws IOException {
        final char spaceHorizontalSeparator = getHorizontalSeparator(space, intersect);
        if (outerBorderEnabled) {
            out.append(leftIntersect);
            out.append(spaceHorizontalSeparator);
        }
        for (int i = 0; i < columnWidths[0]; i++) {
            out.append(spaceHorizontalSeparator);
        }
        for (int c = 1; c < columnWidths.length; c++) {
            for (int i = 0; i < verticalSeparator.length(); i++) {
                final char verticalSeparatorChar = verticalSeparator.charAt(i);
                out.append(getHorizontalSeparator(verticalSeparatorChar, intersect));
            }
            final int columnWidth = columnWidths[c];
            for (int i = 0; i < columnWidth; i++) {
                out.append(spaceHorizontalSeparator);
            }
        }
        if (outerBorderEnabled) {
            out.append(spaceHorizontalSeparator);
            out.append(rightIntersect);
        }
    }

    protected void renderHeaderLine(final String[] headerColumns, final int[] columnWidths, final Appendable out)
            throws IOException {
        final List<String[]> rows = new ArrayList<>();
        for (int c = 0; c < headerColumns.length; c++) {
            final String headerColumn = headerColumns[c];
            final int columnWidth = columnWidths[c];
            final String wrapped = Strings.wrap(headerColumn, columnWidth, "\n", true);
            final String[] lines = Strings.splitPreserveAllTokens(wrapped, '\n');
            for (int r = 0; r < lines.length; r++) {
                final String[] row;
                if (rows.size() - 1 < r) {
                    row = new String[headerColumns.length];
                    rows.add(row);
                } else {
                    row = rows.get(r);
                }
                final String headerRowColumn = lines[r];
                row[c] = newPaddedColumnRight(headerRowColumn, columnWidth);
            }
        }
        for (int r = 0; r < rows.size(); r++) {
            renderContentLine(rows.get(r), columnWidths, out);
        }
    }

    protected void renderContentLine(final String[] columns, final int[] columnWidths, final Appendable out)
            throws IOException {
        if (columns.length == 0) {
            return;
        }
        if (outerBorderEnabled) {
            out.append(vertical);
            out.append(space);
        }
        renderContentColumn(columns[0], columnWidths[0], out);
        for (int c = 1; c < columns.length; c++) {
            out.append(verticalSeparator);
            final int columnWidth = columnWidths[c];
            final String content = columns[c];
            renderContentColumn(content, columnWidth, out);
        }
        if (outerBorderEnabled) {
            out.append(space);
            out.append(vertical);
        }
        out.append(lineFeed);
    }

    protected void renderContentColumn(final String content, final int columnWidth, final Appendable out)
            throws IOException {
        out.append(newPaddedColumnLeft(content, columnWidth));
    }

    // *
    public static AsciiTableTheme of(final char intersect) {
        return new AsciiTableTheme(intersect, intersect, intersect, intersect, intersect, intersect, intersect,
                intersect, intersect, intersect, intersect);
    }

    //CHECKSTYLE:OFF
    public AsciiTableTheme withoutOuterBorder() {
        //CHECKSTYLE:ON
        final char intersect = this.intersect;
        final char horizontal = this.horizontal;
        final char vertical = this.vertical;
        final char topLeft = NONE;
        final char topIntersect = NONE;
        final char topRight = NONE;
        final char leftIntersect = NONE;
        final char rightIntersect = NONE;
        final char bottomLeft = NONE;
        final char bottomIntersect = NONE;
        final char bottomRight = NONE;
        return new AsciiTableTheme(intersect, horizontal, vertical, topLeft, topIntersect, topRight, leftIntersect,
                rightIntersect, bottomLeft, bottomIntersect, bottomRight, headerRowSeparatorEnabled,
                bodyRowSeparatorEnabled, false);
    }

    //CHECKSTYLE:OFF
    public AsciiTableTheme withoutBodyRowSeparator() {
        //CHECKSTYLE:ON
        return new AsciiTableTheme(this, headerRowSeparatorEnabled, false, outerBorderEnabled);
    }

    //CHECKSTYLE:OFF
    public AsciiTableTheme withoutHeaderRowSeparator() {
        //CHECKSTYLE:ON
        return new AsciiTableTheme(this, false, bodyRowSeparatorEnabled, outerBorderEnabled);
    }

    // +-|
    public static AsciiTableTheme of(final char intersect, final char horizontal, final char vertical) {
        return new AsciiTableTheme(intersect, horizontal, vertical, intersect, intersect, intersect, intersect,
                intersect, intersect, intersect, intersect);
    }

}