package de.invesdwin.context.groovy.tests.callback.hello;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.ClassPathResource;

import de.invesdwin.context.groovy.AScriptTaskGroovy;
import de.invesdwin.context.groovy.IScriptTaskRunnerGroovy;
import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.integration.script.IScriptTaskInputs;
import de.invesdwin.context.integration.script.IScriptTaskResults;
import de.invesdwin.context.integration.script.callback.IScriptTaskCallback;
import de.invesdwin.context.integration.script.callback.ReflectiveScriptTaskCallback;
import de.invesdwin.util.assertions.Assertions;

@NotThreadSafe
public class CallbackStrictHelloWorldScript {

    private final IScriptTaskRunnerGroovy runner;

    public CallbackStrictHelloWorldScript(final IScriptTaskRunnerGroovy runner) {
        this.runner = runner;
    }

    public void testHelloWorld() {
        final CallbackStrictHelloWorldScriptCallback callback = new CallbackStrictHelloWorldScriptCallback();
        final AScriptTaskGroovy<String> script = new AScriptTaskGroovy<String>() {

            @Override
            public IScriptTaskCallback getCallback() {
                return new ReflectiveScriptTaskCallback(callback);
            }

            @Override
            public void populateInputs(final IScriptTaskInputs inputs) {}

            @Override
            public void executeScript(final IScriptTaskEngine engine) {
                engine.eval(new ClassPathResource(CallbackStrictHelloWorldScript.class.getSimpleName() + ".groovy",
                        getClass()));
            }

            @Override
            public String extractResults(final IScriptTaskResults results) {
                return callback.world;
            }
        };
        final String result = script.run(runner);
        Assertions.assertThat(result).isEqualTo("Hello World!");
    }

    public static class CallbackStrictHelloWorldScriptCallback {

        private String world = null;

        public String hello() {
            return "World";
        }

        public void world(final String world) {
            this.world = world;
        }

    }

}
