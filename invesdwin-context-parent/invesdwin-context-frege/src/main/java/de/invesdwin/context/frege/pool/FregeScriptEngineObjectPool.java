package de.invesdwin.context.frege.pool;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.AInvalidatingObjectPool;
import jakarta.inject.Named;

/**
 * We need to always invalidate, otherwise the classloader that keeps the class per statement gets full and each
 * internal map access grinds to a halt. Thus it is cheaper to NOT reuse jshell instances.
 */
@ThreadSafe
@Named
public final class FregeScriptEngineObjectPool extends AInvalidatingObjectPool<WrappedFregeScriptEngine>
        implements FactoryBean<FregeScriptEngineObjectPool> {

    public static final FregeScriptEngineObjectPool INSTANCE = new FregeScriptEngineObjectPool();

    private FregeScriptEngineObjectPool() {
        super();
    }

    @Override
    public void invalidateObject(final WrappedFregeScriptEngine obj) {
        obj.close();
    }

    @Override
    protected WrappedFregeScriptEngine newObject() {
        return new WrappedFregeScriptEngine();
    }

    @Override
    public FregeScriptEngineObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return FregeScriptEngineObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
