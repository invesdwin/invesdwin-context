package de.invesdwin.context.test.stub;

import java.util.List;

import javax.annotation.concurrent.Immutable;
import jakarta.inject.Named;

import de.invesdwin.context.beans.init.locations.PositionedResource;
import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.TestContext;

@Named
@Immutable
public class StubSupport implements IStub {

    @Override
    public void setUpContextLocations(final ATest test, final List<PositionedResource> locations) throws Exception {}

    @Override
    public void setUpContext(final ATest test, final TestContext ctx) throws Exception {}

    @Override
    public void setUpOnce(final ATest test, final TestContext ctx) throws Exception {}

    @Override
    public void setUp(final ATest test, final TestContext ctx) throws Exception {}

    @Override
    public void tearDown(final ATest test, final TestContext ctx) throws Exception {}

    @Override
    public void tearDownOnce(final ATest test, final TestContext ctx) throws Exception {}

}
