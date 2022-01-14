package de.invesdwin.context.clojure.pool;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
@Named
public final class ClojureEngineObjectPool extends ATimeoutObjectPool<WrappedClojureEngine>
        implements FactoryBean<ClojureEngineObjectPool> {

    public static final ClojureEngineObjectPool INSTANCE = new ClojureEngineObjectPool();

    private ClojureEngineObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void invalidateObject(final WrappedClojureEngine obj) {
        obj.close();
    }

    @Override
    protected WrappedClojureEngine newObject() {
        return new WrappedClojureEngine();
    }

    @Override
    protected void passivateObject(final WrappedClojureEngine obj) {
        obj.reset();
    }

    @Override
    public ClojureEngineObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ClojureEngineObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
