package de.invesdwin.context.beans.internal;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;
import jakarta.inject.Named;

import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.TestContext;
import de.invesdwin.context.test.stub.StubSupport;
import de.invesdwin.util.lang.uri.URIs;

@Immutable
@Named
public class URIsConnectStub extends StubSupport {

    @Override
    public void tearDown(final ATest test, final TestContext ctx) throws IOException {
        URIs.getDefaultUrisConnectFactory().reset();
    }

}
