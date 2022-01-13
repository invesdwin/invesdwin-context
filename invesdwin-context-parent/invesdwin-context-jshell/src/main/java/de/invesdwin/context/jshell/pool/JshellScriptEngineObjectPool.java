package de.invesdwin.context.jshell.pool;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.AInvalidatingObjectPool;

/**
 * We need to always invalidate, otherwise the classloader that keeps the class per statement gets full and each
 * internal map access grinds to a halt. Thus it is cheaper to NOT reuse jshell instances.
 */
@ThreadSafe
@Named
public final class JshellScriptEngineObjectPool extends AInvalidatingObjectPool<WrappedJshellScriptEngine>
        implements FactoryBean<JshellScriptEngineObjectPool> {

    public static final JshellScriptEngineObjectPool INSTANCE = new JshellScriptEngineObjectPool();

    private JshellScriptEngineObjectPool() {
        super();
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
