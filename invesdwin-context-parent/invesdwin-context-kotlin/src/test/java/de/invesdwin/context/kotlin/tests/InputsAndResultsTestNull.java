package de.invesdwin.context.kotlin.tests;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.integration.script.IScriptTaskInputs;
import de.invesdwin.context.integration.script.IScriptTaskResults;
import de.invesdwin.context.kotlin.AScriptTaskKotlin;
import de.invesdwin.context.kotlin.IScriptTaskRunnerKotlin;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class InputsAndResultsTestNull {

    private final IScriptTaskRunnerKotlin runner;

    public InputsAndResultsTestNull(final IScriptTaskRunnerKotlin runner) {
        this.runner = runner;
    }

    public void testNull() {
        new AScriptTaskKotlin<Void>() {

            @Override
            public void populateInputs(final IScriptTaskInputs inputs) {
            }

            @Override
            public void executeScript(final IScriptTaskEngine engine) {
                Assertions.checkTrue(engine.getResults().isNotDefined("testVariable"));
                Assertions.checkFalse(engine.getResults().isDefined("testVariable"));
                engine.getInputs().putNull("testVariable");
                Assertions.checkFalse(engine.getResults().isNotDefined("testVariable"));
                Assertions.checkTrue(engine.getResults().isDefined("testVariable"));
                Assertions.checkTrue(engine.getResults().isNull("testVariable"));
                Assertions.checkFalse(engine.getResults().isNotNull("testVariable"));
                engine.getInputs().putString("testVariable", "value");
                Assertions.checkFalse(engine.getResults().isNotDefined("testVariable"));
                Assertions.checkTrue(engine.getResults().isDefined("testVariable"));
                Assertions.checkFalse(engine.getResults().isNull("testVariable"));
                Assertions.checkTrue(engine.getResults().isNotNull("testVariable"));
                engine.getInputs().remove("testVariable");
                //kotlin has no real remove, we just null the value and remove it from the binding instead
                Assertions.checkFalse(engine.getResults().isNotDefined("testVariable"));
                Assertions.checkTrue(engine.getResults().isDefined("testVariable"));
            }

            @Override
            public Void extractResults(final IScriptTaskResults results) {
                return null;
            }
        }.run(runner);
    }

}
