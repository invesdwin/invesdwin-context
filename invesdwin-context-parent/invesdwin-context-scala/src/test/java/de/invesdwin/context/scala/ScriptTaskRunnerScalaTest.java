package de.invesdwin.context.scala;

import javax.annotation.concurrent.NotThreadSafe;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.scala.tests.InputsAndResultsTests;
import de.invesdwin.context.test.ATest;

@NotThreadSafe
public class ScriptTaskRunnerScalaTest extends ATest {

    @Inject
    private ScriptTaskRunnerScala runner;

    @Test
    public void test() {
        new InputsAndResultsTests(runner).test();
    }

    @Test
    public void testParallel() {
        new InputsAndResultsTests(runner).testParallel();
    }

}
