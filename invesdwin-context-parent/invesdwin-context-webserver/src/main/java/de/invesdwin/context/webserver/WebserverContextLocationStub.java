package de.invesdwin.context.webserver;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.stub.StubSupport;

@Named
@Immutable
public class WebserverContextLocationStub extends StubSupport {

    @Override
    public void tearDownOnce(final ATest test) {
        WebserverContextLocation.deactivate();
    }

}
