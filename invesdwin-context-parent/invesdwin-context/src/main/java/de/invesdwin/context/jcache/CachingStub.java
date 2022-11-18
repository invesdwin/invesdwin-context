package de.invesdwin.context.jcache;

import javax.annotation.concurrent.Immutable;
import javax.cache.Caching;
import jakarta.inject.Named;

import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.stub.StubSupport;

@Immutable
@Named
public class CachingStub extends StubSupport {

    @Override
    public void tearDownOnce(final ATest test) {
        Caching.getCachingProvider().close();
    }

}
