package de.invesdwin.context.test;

import javax.annotation.concurrent.Immutable;

import org.junit.platform.suite.api.SelectClasses;

import de.invesdwin.util.test.ParallelSuite;

@ParallelSuite
@SelectClasses({ ParallelTest3.class, ParallelTest4.class })
@Immutable
public class ParallelTestSuiteChild2 {

}
