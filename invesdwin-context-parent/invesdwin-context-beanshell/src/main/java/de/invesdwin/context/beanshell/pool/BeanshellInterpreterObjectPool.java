package de.invesdwin.context.beanshell.pool;

import javax.annotation.concurrent.ThreadSafe;
import jakarta.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
@Named
public final class BeanshellInterpreterObjectPool extends ATimeoutObjectPool<WrappedBeanshellInterpreter>
        implements FactoryBean<BeanshellInterpreterObjectPool> {

    public static final BeanshellInterpreterObjectPool INSTANCE = new BeanshellInterpreterObjectPool();

    private BeanshellInterpreterObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void invalidateObject(final WrappedBeanshellInterpreter obj) {
        obj.close();
    }

    @Override
    protected void passivateObject(final WrappedBeanshellInterpreter obj) {
        obj.reset();
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
