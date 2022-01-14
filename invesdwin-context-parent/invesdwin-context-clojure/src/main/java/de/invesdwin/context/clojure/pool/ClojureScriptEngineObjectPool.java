package de.invesdwin.context.clojure.pool;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
@Named
public final class ClojureScriptEngineObjectPool extends ATimeoutObjectPool<WrappedClojureScriptEngine>
        implements FactoryBean<ClojureScriptEngineObjectPool> {

    public static final ClojureScriptEngineObjectPool INSTANCE = new ClojureScriptEngineObjectPool();

    private ClojureScriptEngineObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void invalidateObject(final WrappedClojureScriptEngine obj) {
        obj.close();
    }

    @Override
    protected WrappedClojureScriptEngine newObject() {
        return new WrappedClojureScriptEngine();
    }

    @Override
    protected void passivateObject(final WrappedClojureScriptEngine obj) {
        obj.reset();
    }

    @Override
    public ClojureScriptEngineObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ClojureScriptEngineObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
