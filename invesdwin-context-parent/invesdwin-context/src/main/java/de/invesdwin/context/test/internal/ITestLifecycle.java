package de.invesdwin.context.test.internal;

import java.util.List;

import de.invesdwin.context.beans.init.locations.PositionedResource;
import de.invesdwin.context.test.TestContext;

/**
 * Interface to curcumvent circular dependency in ATest.
 */
public interface ITestLifecycle {

    void setUpContextLocations(List<PositionedResource> contextLocations) throws Exception;

    void setUpContextBeforeLoading() throws Exception;

    void setUpContext(TestContext ctx) throws Exception;

    void setUpOnce() throws Exception;

    void setUp() throws Exception;

    void tearDown() throws Exception;

    void tearDownOnce() throws Exception;

}
