package de.invesdwin.context.test;

import javax.annotation.concurrent.Immutable;

import org.junit.platform.suite.api.SelectClasses;

import de.invesdwin.util.test.ParallelSuite;

@ParallelSuite
@SelectClasses({ ParallelTestSuiteChild1.class, ParallelTestSuiteChild2.class })
@Immutable
public class ParallelTestSuiteParent {

}
