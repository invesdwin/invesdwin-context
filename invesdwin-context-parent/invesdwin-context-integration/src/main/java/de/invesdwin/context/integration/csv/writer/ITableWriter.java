package de.invesdwin.context.integration.csv.writer;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface ITableWriter extends Closeable {

    void column(Object column);

    void newLine() throws IOException;

    void line(List<?> columns) throws IOException;

    void line(List<?> columns, int length) throws IOException;

    void line(Object... columns) throws IOException;

    void flush() throws IOException;

    ITableWriter setAssertColumnCount(Integer assertColumnCount);

    Integer getAssertColumnCount();

}
