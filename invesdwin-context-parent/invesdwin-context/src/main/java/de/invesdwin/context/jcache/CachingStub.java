package de.invesdwin.context.jcache;

import javax.annotation.concurrent.Immutable;
import javax.cache.Caching;

import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.TestContext;
import de.invesdwin.context.test.stub.StubSupport;
import jakarta.inject.Named;

@Immutable
@Named
public class CachingStub extends StubSupport {

    @Override
    public void tearDownOnce(final ATest test, final TestContext ctx) {
        if (!ctx.isFinishedGlobal()) {
            return;
        }
        Caching.getCachingProvider().close();
    }

}
