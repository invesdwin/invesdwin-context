package de.invesdwin.context.beans.init.internal;

import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;
import javax.inject.Named;

import de.invesdwin.context.beans.hook.IStartupHook;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.loadingcache.historical.refresh.HistoricalCacheRefreshManager;
import de.invesdwin.util.concurrent.WrappedScheduledExecutorService;

@Named
@Immutable
public class HistoricalCacheRefreshManagerStartupHook implements IStartupHook {

    @Inject
    @Named("scheduledExecutorService")
    private WrappedScheduledExecutorService scheduledExecutorService;

    @Override
    public void startup() throws Exception {
        Assertions.assertThat(HistoricalCacheRefreshManager.startRefreshScheduler(scheduledExecutorService)).isTrue();
    }

}
