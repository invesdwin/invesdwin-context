package de.invesdwin.context.test.stub;

import java.util.List;

import de.invesdwin.context.beans.init.locations.PositionedResource;
import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.TestContext;

/**
 * This interface can be used to let other classes participate in the lifecycle of tests or to replace objects with
 * stubs (fake objects) for easier testing.
 * 
 * Stub get automatically removed before the context gets loaded in an production environment. Only when started from
 * the IDE these mocks will be active.
 * 
 * @see <a href="http://stackoverflow.com/questions/3459287/whats-the-difference-between-a-mock-stub">Definition</a>
 * 
 */
public interface IStub {

    void setUpContextLocations(ATest test, List<PositionedResource> locations) throws Exception;

    void setUpContextBeforeLoading(ATest test) throws Exception;

    void setUpContext(ATest test, TestContext ctx) throws Exception;

    void setUpOnce(ATest test, TestContext ctx) throws Exception;

    void setUp(ATest test, TestContext ctx) throws Exception;

    void tearDown(ATest test, TestContext ctx) throws Exception;

    /**
     * This normally gets called after the tests finished, but also gets called when test context failed to initialize
     * in order to clean up the mess.
     */
    void tearDownOnce(ATest test, TestContext ctx) throws Exception;

}
