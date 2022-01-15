package de.invesdwin.context.clojure.pool;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.AInvalidatingObjectPool;

@ThreadSafe
@Named
public final class ClojureEngineObjectPool extends AInvalidatingObjectPool<WrappedClojureEngine>
        implements FactoryBean<ClojureEngineObjectPool> {

    public static final ClojureEngineObjectPool INSTANCE = new ClojureEngineObjectPool();

    private ClojureEngineObjectPool() {
        super();
    }

    @Override
    public void invalidateObject(final WrappedClojureEngine obj) {
        obj.close();
    }

    @Override
    protected WrappedClojureEngine newObject() {
        return WrappedClojureEngine.getInstance();
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
