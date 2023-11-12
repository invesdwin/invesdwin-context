package de.invesdwin.context.groovy;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.groovy.tests.InputsAndResultsTests;
import de.invesdwin.context.groovy.tests.callback.ParametersAndReturnsTests;
import de.invesdwin.context.groovy.tests.callback.SimpleCallbackTest;
import de.invesdwin.context.groovy.tests.callback.hello.CallbackStrictHelloWorldScript;
import de.invesdwin.context.groovy.tests.hello.FileStrictHelloWorldScript;
import de.invesdwin.context.groovy.tests.hello.StrictHelloWorldScript;
import de.invesdwin.context.test.ATest;
import jakarta.inject.Inject;

@NotThreadSafe
public class ScriptTaskRunnerGroovyTest extends ATest {

    @Inject
    private ScriptTaskRunnerGroovy runner;

    @Test
    public void test() {
        new InputsAndResultsTests(runner).test();
    }

    @Test
    public void testStrict() {
        final Boolean strictOverrideBefore = GroovyProperties.setStrictOverride(true);
        try {
            new StrictHelloWorldScript(runner).testHelloWorld();
        } finally {
            GroovyProperties.setStrictOverride(strictOverrideBefore);
        }
    }

    @Test
    public void testFileStrict() {
        final Boolean strictOverrideBefore = GroovyProperties.setStrictOverride(false);
        try {
            new FileStrictHelloWorldScript(runner).testHelloWorld();
        } finally {
            GroovyProperties.setStrictOverride(strictOverrideBefore);
        }
    }

    @Test
    public void testParallel() {
        new InputsAndResultsTests(runner).testParallel();
    }

    @Test
    public void testCallback() {
        new ParametersAndReturnsTests(runner).test();
    }

    @Test
    public void testCallbackParallel() {
        new ParametersAndReturnsTests(runner).testParallel();
    }

    @Test
    public void testSimpleCallback() {
        new SimpleCallbackTest(runner).testSimpleCallback();
    }

    @Test
    public void testCallbackStrict() {
        final Boolean strictOverrideBefore = GroovyProperties.setStrictOverride(true);
        try {
            new CallbackStrictHelloWorldScript(runner).testHelloWorld();
        } finally {
            GroovyProperties.setStrictOverride(strictOverrideBefore);
        }
    }

}
