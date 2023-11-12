package de.invesdwin.context.mvel;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.mvel.tests.InputsAndResultsTests;
import de.invesdwin.context.mvel.tests.callback.ParametersAndReturnsTests;
import de.invesdwin.context.mvel.tests.callback.SimpleCallbackTest;
import de.invesdwin.context.mvel.tests.hello.StrictHelloWorldScript;
import de.invesdwin.context.test.ATest;
import jakarta.inject.Inject;

@NotThreadSafe
public class ScriptTaskRunnerMvelTest extends ATest {

    @Inject
    private ScriptTaskRunnerMvel runner;

    @Test
    public void test() {
        new InputsAndResultsTests(runner).test();
    }

    @Test
    public void testStrict() {
        final Boolean strictOverrideBefore = MvelProperties.setStrictOverride(true);
        try {
            new StrictHelloWorldScript(runner).testHelloWorld();
        } finally {
            MvelProperties.setStrictOverride(strictOverrideBefore);
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

    //    @Test
    //    public void testSimpleCallbackStrict() {
    //        final Boolean strictOverrideBefore = MvelProperties.setStrictOverride(true);
    //        try {
    //            testSimpleCallback();
    //        } finally {
    //            MvelProperties.setStrictOverride(strictOverrideBefore);
    //        }
    //    }

}
