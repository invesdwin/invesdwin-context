package de.invesdwin.context.jruby;

import javax.annotation.concurrent.NotThreadSafe;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.jruby.tests.InputsAndResultsTests;
import de.invesdwin.context.test.ATest;

@NotThreadSafe
public class ScriptTaskRunnerJrubyTest extends ATest {

    @Inject
    private ScriptTaskRunnerJruby runner;

    @Test
    public void test() {
        new InputsAndResultsTests(runner).test();
    }

    @Test
    public void testParallel() {
        new InputsAndResultsTests(runner).testParallel();
    }

}
