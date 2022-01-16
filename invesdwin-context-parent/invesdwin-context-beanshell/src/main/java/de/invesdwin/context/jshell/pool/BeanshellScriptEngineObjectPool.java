package de.invesdwin.context.jshell.pool;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.AInvalidatingObjectPool;

@ThreadSafe
@Named
public final class BeanshellScriptEngineObjectPool extends AInvalidatingObjectPool<WrappedBeanshellScriptEngine>
        implements FactoryBean<BeanshellScriptEngineObjectPool> {

    public static final BeanshellScriptEngineObjectPool INSTANCE = new BeanshellScriptEngineObjectPool();

    private BeanshellScriptEngineObjectPool() {
        super();
    }

    @Override
    public void invalidateObject(final WrappedBeanshellScriptEngine obj) {
        obj.close();
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
