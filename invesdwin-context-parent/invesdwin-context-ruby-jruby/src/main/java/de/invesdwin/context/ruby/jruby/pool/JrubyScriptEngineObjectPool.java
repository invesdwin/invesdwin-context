package de.invesdwin.context.ruby.jruby.pool;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;
import jakarta.inject.Named;

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
    protected boolean passivateObject(final WrappedJrubyScriptEngine obj) {
        obj.reset();
        return true;
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
