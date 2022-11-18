package de.invesdwin.context.clojure;

import javax.annotation.concurrent.NotThreadSafe;
import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.clojure.tests.InputsAndResultsTests;
import de.invesdwin.context.test.ATest;

@NotThreadSafe
public class ScriptTaskRunnerClojureTest extends ATest {

    @Inject
    private ScriptTaskRunnerClojure runner;

    @Test
    public void test() {
        new InputsAndResultsTests(runner).test();
    }

    @Test
    public void testParallel() {
        new InputsAndResultsTests(runner).testParallel();
    }

}
