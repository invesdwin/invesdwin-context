package de.invesdwin.context.frege.tests.hello;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.frege.AScriptTaskFrege;
import de.invesdwin.context.frege.IScriptTaskRunnerFrege;
import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.integration.script.IScriptTaskInputs;
import de.invesdwin.context.integration.script.IScriptTaskResults;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class HelloWorldScript {

    private final IScriptTaskRunnerFrege runner;

    public HelloWorldScript(final IScriptTaskRunnerFrege runner) {
        this.runner = runner;
    }

    public void testHelloWorld() {
        final AScriptTaskFrege<String> script = new AScriptTaskFrege<String>() {

            @Override
            public void populateInputs(final IScriptTaskInputs inputs) {
                inputs.putString("hello", "World");
            }

            @Override
            public void executeScript(final IScriptTaskEngine engine) {
                //execute this script inline:
                //                engine.eval("world = \"Hello \" + hello + \"!\"");
                //or run it from a file:
                engine.eval(new ClassPathResource(HelloWorldScript.class.getSimpleName() + ".fr", getClass()));
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
