package de.invesdwin.context.ruby.truffleruby.pool;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;
import jakarta.inject.Named;

@ThreadSafe
@Named
public final class TrufflerubyScriptEngineObjectPool extends ATimeoutObjectPool<WrappedTrufflerubyScriptEngine>
        implements FactoryBean<TrufflerubyScriptEngineObjectPool> {

    public static final TrufflerubyScriptEngineObjectPool INSTANCE = new TrufflerubyScriptEngineObjectPool();

    private TrufflerubyScriptEngineObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void invalidateObject(final WrappedTrufflerubyScriptEngine obj) {
        obj.close();
    }

    @Override
    protected WrappedTrufflerubyScriptEngine newObject() {
        return new WrappedTrufflerubyScriptEngine();
    }

    @Override
    protected boolean passivateObject(final WrappedTrufflerubyScriptEngine obj) {
        obj.reset();
        return true;
    }

    @Override
    public TrufflerubyScriptEngineObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return TrufflerubyScriptEngineObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
