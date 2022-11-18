package de.invesdwin.context.groovy.pool;

import javax.annotation.concurrent.ThreadSafe;
import jakarta.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
@Named
public final class GroovyShellObjectPool extends ATimeoutObjectPool<WrappedGroovyShell>
        implements FactoryBean<GroovyShellObjectPool> {

    public static final GroovyShellObjectPool INSTANCE = new GroovyShellObjectPool();

    private GroovyShellObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void invalidateObject(final WrappedGroovyShell obj) {
        obj.close();
    }

    @Override
    protected WrappedGroovyShell newObject() {
        return new WrappedGroovyShell(false);
    }

    @Override
    protected void passivateObject(final WrappedGroovyShell obj) {
        obj.reset();
    }

    @Override
    public GroovyShellObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return GroovyShellObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
