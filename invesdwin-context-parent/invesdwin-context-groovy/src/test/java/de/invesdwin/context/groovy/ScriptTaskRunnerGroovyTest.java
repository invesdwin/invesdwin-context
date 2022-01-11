package de.invesdwin.context.groovy;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.groovy.tests.InputsAndResultsTests;
import de.invesdwin.context.test.ATest;

@NotThreadSafe
public class ScriptTaskRunnerGroovyTest extends ATest {

    @Inject
    private ScriptTaskRunnerGroovy runner;

    @Test
    public void test() {
        new InputsAndResultsTests(runner).test();
    }

    @Test
    public void testParallel() {
        new InputsAndResultsTests(runner).testParallel();
    }

}
