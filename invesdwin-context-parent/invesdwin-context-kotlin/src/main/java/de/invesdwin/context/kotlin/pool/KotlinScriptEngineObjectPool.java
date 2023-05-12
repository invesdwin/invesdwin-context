package de.invesdwin.context.kotlin.pool;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;
import jakarta.inject.Named;

@ThreadSafe
@Named
public final class KotlinScriptEngineObjectPool extends ATimeoutObjectPool<WrappedKotlinScriptEngine>
        implements FactoryBean<KotlinScriptEngineObjectPool> {

    public static final KotlinScriptEngineObjectPool INSTANCE = new KotlinScriptEngineObjectPool();

    private KotlinScriptEngineObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void invalidateObject(final WrappedKotlinScriptEngine obj) {
        obj.close();
    }

    @Override
    protected WrappedKotlinScriptEngine newObject() {
        return new WrappedKotlinScriptEngine();
    }

    @Override
    protected boolean passivateObject(final WrappedKotlinScriptEngine obj) {
        obj.reset();
        return true;
    }

    @Override
    public KotlinScriptEngineObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return KotlinScriptEngineObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
