package de.invesdwin.context.ruby;

import de.invesdwin.context.integration.script.IScriptTaskInputs;

public interface IScriptTaskInputsRuby extends IScriptTaskInputs {

    @Override
    default void putExpression(final String variable, final String expression) {
        getEngine().eval(variable + " = " + expression);
    }

}
