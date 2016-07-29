package de.invesdwin.context.beans.internal;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.collections.loadingcache.historical.refresh.HistoricalCacheRefreshManager;

@Named
@ThreadSafe
public final class HistoricalCacheRefreshManagerFactoryBean implements FactoryBean<HistoricalCacheRefreshManager> {

    private static final HistoricalCacheRefreshManager INSTANCE = HistoricalCacheRefreshManager.INSTANCE;

    public HistoricalCacheRefreshManagerFactoryBean() {}

    @Override
    public HistoricalCacheRefreshManager getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return INSTANCE.getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
