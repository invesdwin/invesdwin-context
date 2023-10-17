package de.invesdwin.context.integration.script.callback;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class ObjectScriptTaskReturnValue {
    private final boolean returnExpression;
    private final Object returnValue;

    public ObjectScriptTaskReturnValue(final boolean returnExpression, final Object returnValue) {
        this.returnExpression = returnExpression;
        this.returnValue = returnValue;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public boolean isReturnExpression() {
        return returnExpression;
    }

}