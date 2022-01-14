package de.invesdwin.context.jruby.pool;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
@Named
public final class JrubyScriptEngineObjectPool extends ATimeoutObjectPool<WrappedJrubyScriptEngine>
        implements FactoryBean<JrubyScriptEngineObjectPool> {

    public static final JrubyScriptEngineObjectPool INSTANCE = new JrubyScriptEngineObjectPool();

    private JrubyScriptEngineObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void invalidateObject(final WrappedJrubyScriptEngine obj) {
        obj.close();
    }

    @Override
    protected WrappedJrubyScriptEngine newObject() {
        return new WrappedJrubyScriptEngine();
    }

    @Override
    protected void passivateObject(final WrappedJrubyScriptEngine obj) {
        obj.reset();
    }

    @Override
    public JrubyScriptEngineObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return JrubyScriptEngineObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
