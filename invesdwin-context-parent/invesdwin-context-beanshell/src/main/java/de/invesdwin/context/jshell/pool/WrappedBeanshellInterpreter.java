package de.invesdwin.context.jshell.pool;

import java.io.Closeable;
import java.util.Arrays;

import javax.annotation.concurrent.NotThreadSafe;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.UtilEvalError;

@NotThreadSafe
public class WrappedBeanshellInterpreter implements Closeable {

    private final Interpreter interpreter;
    private final NameSpace nameSpace;

    public WrappedBeanshellInterpreter() {
        this.interpreter = new Interpreter();
        this.nameSpace = interpreter.getNameSpace();
        reset();
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public NameSpace getNameSpace() {
        return nameSpace;
    }

    public Object eval(final String expression) {
        try {
            return interpreter.eval(expression);
        } catch (final EvalError e) {
            throw new RuntimeException(e);
        }
    }

    public void reset() {
        nameSpace.clear();
    }

    @Override
    public void close() {
        reset();
    }

    public void put(final String variable, final Object value) {
        if (value == null) {
            eval(variable + " = null");
        } else {
            try {
                nameSpace.setVariable(variable, value, false);
            } catch (final UtilEvalError e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Object get(final String variable) {
        return eval(variable);
    }

    public void remove(final String variable) {
        nameSpace.unsetVariable(variable);
    }

    public boolean contains(final String variable) {
        return Arrays.binarySearch(nameSpace.getVariableNames(), variable) >= 0;
    }

}
