package de.invesdwin.context.beanshell.pool;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;
import jakarta.inject.Named;

@ThreadSafe
@Named
public final class BeanshellScriptEngineObjectPool extends ATimeoutObjectPool<WrappedBeanshellScriptEngine>
        implements FactoryBean<BeanshellScriptEngineObjectPool> {

    public static final BeanshellScriptEngineObjectPool INSTANCE = new BeanshellScriptEngineObjectPool();

    private BeanshellScriptEngineObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void invalidateObject(final WrappedBeanshellScriptEngine obj) {
        obj.close();
    }

    @Override
    protected boolean passivateObject(final WrappedBeanshellScriptEngine obj) {
        obj.reset();
        return true;
    }

    @Override
    protected WrappedBeanshellScriptEngine newObject() {
        return new WrappedBeanshellScriptEngine();
    }

    @Override
    public BeanshellScriptEngineObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return BeanshellScriptEngineObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
