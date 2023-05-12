package de.invesdwin.context.groovy.pool;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;
import jakarta.inject.Named;

@ThreadSafe
@Named
public final class StrictGroovyShellObjectPool extends ATimeoutObjectPool<WrappedGroovyShell>
        implements FactoryBean<StrictGroovyShellObjectPool> {

    public static final StrictGroovyShellObjectPool INSTANCE = new StrictGroovyShellObjectPool();

    private StrictGroovyShellObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void invalidateObject(final WrappedGroovyShell obj) {
        obj.close();
    }

    @Override
    protected WrappedGroovyShell newObject() {
        return new WrappedGroovyShell(true);
    }

    @Override
    protected boolean passivateObject(final WrappedGroovyShell obj) {
        obj.reset();
        return true;
    }

    @Override
    public StrictGroovyShellObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return StrictGroovyShellObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
