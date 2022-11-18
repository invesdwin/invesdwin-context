package de.invesdwin.context.kotlin;

import javax.annotation.concurrent.NotThreadSafe;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.kotlin.tests.InputsAndResultsTests;
import de.invesdwin.context.test.ATest;

@NotThreadSafe
public class ScriptTaskRunnerKotlinTest extends ATest {

    @Inject
    private ScriptTaskRunnerKotlin runner;

    @Test
    public void test() {
        new InputsAndResultsTests(runner).test();
    }

    @Test
    public void testParallel() {
        new InputsAndResultsTests(runner).testParallel();
    }

}
