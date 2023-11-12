package de.invesdwin.context.mvel.tests.hello;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.integration.script.IScriptTaskInputs;
import de.invesdwin.context.integration.script.IScriptTaskResults;
import de.invesdwin.context.mvel.AScriptTaskMvel;
import de.invesdwin.context.mvel.IScriptTaskRunnerMvel;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class StrictHelloWorldScript {

    private final IScriptTaskRunnerMvel runner;

    public StrictHelloWorldScript(final IScriptTaskRunnerMvel runner) {
        this.runner = runner;
    }

    public void testHelloWorld() {
        final AScriptTaskMvel<String> script = new AScriptTaskMvel<String>() {

            @Override
            public void populateInputs(final IScriptTaskInputs inputs) {
                inputs.putString("hello", "World");
            }

            @Override
            public void executeScript(final IScriptTaskEngine engine) {
                engine.eval(new ClassPathResource(StrictHelloWorldScript.class.getSimpleName() + ".mvel", getClass()));
            }

            @Override
            public String extractResults(final IScriptTaskResults results) {
                return results.getString("world");
            }
        };
        final String result = script.run(runner);
        Assertions.assertThat(result).isEqualTo("Hello World!");
    }

}
