package de.invesdwin.context.jcache;

import javax.annotation.concurrent.Immutable;
import javax.cache.CacheManager;
import javax.cache.Caching;

import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.ITestContext;
import de.invesdwin.context.test.stub.StubSupport;
import jakarta.inject.Named;

@Immutable
@Named
public class CachingStub extends StubSupport {

    @Override
    public void tearDownOnce(final ATest test, final ITestContext ctx) {
        if (!ctx.isFinishedGlobal()) {
            return;
        }
        final CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
        for (final String cacheName : cacheManager.getCacheNames()) {
            cacheManager.getCache(cacheName).clear();
        }
    }

    @Override
    public void setUpContextBeforeLoading(final ATest test) throws Exception {
        Caching.getCachingProvider().getCacheManager().close();
    }

}
