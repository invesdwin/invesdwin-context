package de.invesdwin.context.scala.pool;

import javax.annotation.concurrent.ThreadSafe;
import jakarta.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
@Named
public final class ScalaScriptEngineObjectPool extends ATimeoutObjectPool<WrappedScalaScriptEngine>
        implements FactoryBean<ScalaScriptEngineObjectPool> {

    public static final ScalaScriptEngineObjectPool INSTANCE = new ScalaScriptEngineObjectPool();

    private ScalaScriptEngineObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void invalidateObject(final WrappedScalaScriptEngine obj) {
        obj.close();
    }

    @Override
    protected WrappedScalaScriptEngine newObject() {
        return new WrappedScalaScriptEngine();
    }

    @Override
    protected void passivateObject(final WrappedScalaScriptEngine obj) {
        obj.reset();
    }

    @Override
    public ScalaScriptEngineObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ScalaScriptEngineObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
