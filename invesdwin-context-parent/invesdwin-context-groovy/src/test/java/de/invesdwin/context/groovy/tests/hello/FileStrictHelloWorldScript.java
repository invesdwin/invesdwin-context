package de.invesdwin.context.groovy.tests.hello;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.groovy.AScriptTaskGroovy;
import de.invesdwin.context.groovy.IScriptTaskRunnerGroovy;
import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.integration.script.IScriptTaskInputs;
import de.invesdwin.context.integration.script.IScriptTaskResults;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class FileStrictHelloWorldScript {

    private final IScriptTaskRunnerGroovy runner;

    public FileStrictHelloWorldScript(final IScriptTaskRunnerGroovy runner) {
        this.runner = runner;
    }

    public void testHelloWorld() {
        final AScriptTaskGroovy<String> script = new AScriptTaskGroovy<String>() {

            @Override
            public void populateInputs(final IScriptTaskInputs inputs) {
                inputs.putString("hello", "World");
            }

            @Override
            public void executeScript(final IScriptTaskEngine engine) {
                //execute this script inline:
                //                engine.eval("world = \"Hello \" + hello + \"!\"");
                //or run it from a file:
                engine.eval(new ClassPathResource(FileStrictHelloWorldScript.class.getSimpleName() + ".groovy",
                        getClass()));
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
