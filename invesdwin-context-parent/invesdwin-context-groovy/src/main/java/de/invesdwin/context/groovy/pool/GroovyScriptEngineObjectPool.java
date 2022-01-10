package de.invesdwin.context.groovy.pool;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
@Named
public final class GroovyScriptEngineObjectPool extends ATimeoutObjectPool<WrappedGroovyScriptEngine>
        implements FactoryBean<GroovyScriptEngineObjectPool> {

    public static final GroovyScriptEngineObjectPool INSTANCE = new GroovyScriptEngineObjectPool();

    private GroovyScriptEngineObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void destroyObject(final WrappedGroovyScriptEngine obj) {
        obj.close();
    }

    @Override
    protected WrappedGroovyScriptEngine newObject() {
        return new WrappedGroovyScriptEngine();
    }

    @Override
    protected void passivateObject(final WrappedGroovyScriptEngine obj) {
        obj.reset();
    }

    @Override
    public GroovyScriptEngineObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return GroovyScriptEngineObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
