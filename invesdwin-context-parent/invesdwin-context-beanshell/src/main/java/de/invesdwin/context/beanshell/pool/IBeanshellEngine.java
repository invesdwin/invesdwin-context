package de.invesdwin.context.beanshell.pool;

import java.io.Closeable;

public interface IBeanshellEngine extends Closeable {

    void put(String variable, Object value);

    Object get(String variable);

    void remove(String variable);

    boolean contains(String variable);

    void reset();

    Object eval(String expression);

}
