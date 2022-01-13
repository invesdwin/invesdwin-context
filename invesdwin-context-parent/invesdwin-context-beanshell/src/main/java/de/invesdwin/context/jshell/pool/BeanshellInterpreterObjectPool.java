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
public final class BeanshellInterpreterObjectPool extends AInvalidatingObjectPool<WrappedBeanshellInterpreter>
        implements FactoryBean<BeanshellInterpreterObjectPool> {

    public static final BeanshellInterpreterObjectPool INSTANCE = new BeanshellInterpreterObjectPool();

    private BeanshellInterpreterObjectPool() {
        super();
    }

    @Override
    public void invalidateObject(final WrappedBeanshellInterpreter obj) {
        obj.close();
    }

    @Override
    protected WrappedBeanshellInterpreter newObject() {
        return new WrappedBeanshellInterpreter();
    }

    @Override
    public BeanshellInterpreterObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return BeanshellInterpreterObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
