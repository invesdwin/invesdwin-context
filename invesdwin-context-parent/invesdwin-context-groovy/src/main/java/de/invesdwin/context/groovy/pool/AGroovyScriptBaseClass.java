package de.invesdwin.context.groovy.pool;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.groovy.callback.GroovyScriptTaskCallbackContext;
import de.invesdwin.context.integration.script.callback.ObjectScriptTaskReturnValue;
import de.invesdwin.util.lang.Objects;
import groovy.lang.Script;
import groovy.util.Eval;

@NotThreadSafe
public abstract class AGroovyScriptBaseClass extends Script {

    //groovy has similar magic like beanshell regarding lists being exploded as parameters even if unwanted
    public Object callback(final String methodName) {
        return callback(methodName, Objects.EMPTY_ARRAY);
    }

    public Object callback(final String methodName, final Object parameter1) {
        return callback(methodName, new Object[] { parameter1 });
    }

    public Object callback(final String methodName, final Object parameter1, final Object parameter2) {
        return callback(methodName, new Object[] { parameter1, parameter2 });
    }

    public Object callback(final String methodName, final Object parameter1, final Object parameter2,
            final Object parameter3) {
        return callback(methodName, new Object[] { parameter1, parameter2, parameter3 });
    }

    public Object callback(final String methodName, final Object parameter1, final Object parameter2,
            final Object parameter3, final Object parameter4) {
        return callback(methodName, new Object[] { parameter1, parameter2, parameter3, parameter4 });
    }

    public Object callback(final String methodName, final Object parameter1, final Object parameter2,
            final Object parameter3, final Object parameter4, final Object parameter5) {
        return callback(methodName, new Object[] { parameter1, parameter2, parameter3, parameter4, parameter5 });
    }

    public Object callback(final String methodName, final Object parameter1, final Object parameter2,
            final Object parameter3, final Object parameter4, final Object parameter5, final Object parameter6) {
        return callback(methodName,
                new Object[] { parameter1, parameter2, parameter3, parameter4, parameter5, parameter6 });
    }

    public Object callback(final String methodName, final Object parameter1, final Object parameter2,
            final Object parameter3, final Object parameter4, final Object parameter5, final Object parameter6,
            final Object parameter7) {
        return callback(methodName,
                new Object[] { parameter1, parameter2, parameter3, parameter4, parameter5, parameter6, parameter7 });
    }

    public Object callback(final String methodName, final Object parameter1, final Object parameter2,
            final Object parameter3, final Object parameter4, final Object parameter5, final Object parameter6,
            final Object parameter7, final Object parameter8) {
        return callback(methodName, new Object[] { parameter1, parameter2, parameter3, parameter4, parameter5,
                parameter6, parameter7, parameter8 });
    }

    public Object callback(final String methodName, final Object parameter1, final Object parameter2,
            final Object parameter3, final Object parameter4, final Object parameter5, final Object parameter6,
            final Object parameter7, final Object parameter8, final Object parameter9) {
        return callback(methodName, new Object[] { parameter1, parameter2, parameter3, parameter4, parameter5,
                parameter6, parameter7, parameter8, parameter9 });
    }

    public Object callback(final String methodName, final Object parameter1, final Object parameter2,
            final Object parameter3, final Object parameter4, final Object parameter5, final Object parameter6,
            final Object parameter7, final Object parameter8, final Object parameter9, final Object parameter10) {
        return callback(methodName, new Object[] { parameter1, parameter2, parameter3, parameter4, parameter5,
                parameter6, parameter7, parameter8, parameter9, parameter10 });
    }

    public Object callback(final String methodName, final Object... parameters) {
        if (!getBinding().hasVariable("groovyScriptTaskCallbackContext")) {
            if (getBinding().hasVariable("groovyScriptTaskCallbackContextUuid")) {
                final String groovyScriptTaskCallbackContextUuid = (String) getBinding()
                        .getVariable("groovyScriptTaskCallbackContextUuid");
                getBinding().setVariable("groovyScriptTaskCallbackContext",
                        de.invesdwin.context.groovy.callback.GroovyScriptTaskCallbackContext
                                .getContext(groovyScriptTaskCallbackContextUuid));
            } else {
                throw new RuntimeException("IScriptTaskCallback not available");
            }
        }
        final GroovyScriptTaskCallbackContext groovyScriptTaskCallbackContext = (GroovyScriptTaskCallbackContext) getBinding()
                .getVariable("groovyScriptTaskCallbackContext");
        final ObjectScriptTaskReturnValue returnValue = groovyScriptTaskCallbackContext.invoke(methodName, parameters);
        if (returnValue.isReturnExpression()) {
            return Eval.me((String) returnValue.getReturnValue());
        } else {
            return returnValue.getReturnValue();
        }
    }

}
