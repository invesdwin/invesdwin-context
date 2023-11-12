package de.invesdwin.context.mvel.pool;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;
import jakarta.inject.Named;

@ThreadSafe
@Named
public final class MvelScriptEngineObjectPool extends ATimeoutObjectPool<WrappedMvelScriptEngine>
        implements FactoryBean<MvelScriptEngineObjectPool> {

    public static final MvelScriptEngineObjectPool INSTANCE = new MvelScriptEngineObjectPool();

    private MvelScriptEngineObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void invalidateObject(final WrappedMvelScriptEngine obj) {
        obj.close();
    }

    @Override
    protected boolean passivateObject(final WrappedMvelScriptEngine obj) {
        obj.reset();
        return true;
    }

    @Override
    protected WrappedMvelScriptEngine newObject() {
        return new WrappedMvelScriptEngine();
    }

    @Override
    public MvelScriptEngineObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return MvelScriptEngineObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
