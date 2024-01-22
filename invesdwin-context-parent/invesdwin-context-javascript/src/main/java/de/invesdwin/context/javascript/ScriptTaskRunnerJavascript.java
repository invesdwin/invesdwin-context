package de.invesdwin.context.javascript;

import javax.annotation.concurrent.Immutable;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.integration.script.callback.IScriptTaskCallback;
import de.invesdwin.context.integration.script.callback.LoggingDelegateScriptTaskCallback;
import de.invesdwin.context.javascript.callback.JavascriptScriptTaskCallbackContext;
import de.invesdwin.context.javascript.pool.JavascriptScriptEngineObjectPool;
import de.invesdwin.context.javascript.pool.WrappedJavascriptScriptEngine;
import de.invesdwin.util.error.Throwables;
import jakarta.inject.Named;

@Immutable
@Named
public final class ScriptTaskRunnerJavascript
        implements IScriptTaskRunnerJavascript, FactoryBean<ScriptTaskRunnerJavascript> {

    public static final ScriptTaskRunnerJavascript INSTANCE = new ScriptTaskRunnerJavascript();

    /**
     * public for ServiceLoader support
     */
    public ScriptTaskRunnerJavascript() {}

    @Override
    public <T> T run(final AScriptTaskJavascript<T> scriptTask) {
        //get session
        final WrappedJavascriptScriptEngine scriptEngine = JavascriptScriptEngineObjectPool.INSTANCE.borrowObject();
        final JavascriptScriptTaskCallbackContext context;
        final IScriptTaskCallback callback = scriptTask.getCallback();
        if (callback != null) {
            context = new JavascriptScriptTaskCallbackContext(
                    LoggingDelegateScriptTaskCallback.maybeWrap(LOG, callback));
        } else {
            context = null;
        }
        try {
            //inputs
            final ScriptTaskEngineJavascript engine = new ScriptTaskEngineJavascript(scriptEngine);
            if (context != null) {
                context.init(engine);
            }
            scriptTask.populateInputs(engine.getInputs());

            //execute
            scriptTask.executeScript(engine);

            //results
            final T result = scriptTask.extractResults(engine.getResults());
            engine.close();

            //return
            JavascriptScriptEngineObjectPool.INSTANCE.returnObject(scriptEngine);
            return result;
        } catch (final Throwable t) {
            JavascriptScriptEngineObjectPool.INSTANCE.invalidateObject(scriptEngine);
            throw Throwables.propagate(t);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    @Override
    public ScriptTaskRunnerJavascript getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ScriptTaskRunnerJavascript.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
