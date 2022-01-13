package de.invesdwin.context.jshell.pool;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
@Named
public final class JshellScriptEngineObjectPool extends ATimeoutObjectPool<WrappedJshellScriptEngine>
        implements FactoryBean<JshellScriptEngineObjectPool> {

    public static final JshellScriptEngineObjectPool INSTANCE = new JshellScriptEngineObjectPool();

    private JshellScriptEngineObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void invalidateObject(final WrappedJshellScriptEngine obj) {
        obj.close();
    }

    @Override
    protected WrappedJshellScriptEngine newObject() {
        return new WrappedJshellScriptEngine();
    }

    @Override
    protected void passivateObject(final WrappedJshellScriptEngine obj) {
        obj.reset();
    }

    @Override
    public JshellScriptEngineObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return JshellScriptEngineObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
