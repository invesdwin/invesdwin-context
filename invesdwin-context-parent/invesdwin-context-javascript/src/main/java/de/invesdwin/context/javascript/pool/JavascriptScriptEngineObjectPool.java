package de.invesdwin.context.javascript.pool;

import javax.annotation.concurrent.ThreadSafe;
import jakarta.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.util.concurrent.pool.timeout.ATimeoutObjectPool;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
@Named
public final class JavascriptScriptEngineObjectPool extends ATimeoutObjectPool<WrappedJavascriptScriptEngine>
        implements FactoryBean<JavascriptScriptEngineObjectPool> {

    public static final JavascriptScriptEngineObjectPool INSTANCE = new JavascriptScriptEngineObjectPool();

    private JavascriptScriptEngineObjectPool() {
        super(Duration.ONE_MINUTE, new Duration(10, FTimeUnit.SECONDS));
    }

    @Override
    public void invalidateObject(final WrappedJavascriptScriptEngine obj) {
        obj.close();
    }

    @Override
    protected WrappedJavascriptScriptEngine newObject() {
        return new WrappedJavascriptScriptEngine();
    }

    @Override
    protected void passivateObject(final WrappedJavascriptScriptEngine obj) {
        obj.reset();
    }

    @Override
    public JavascriptScriptEngineObjectPool getObject() {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return JavascriptScriptEngineObjectPool.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
