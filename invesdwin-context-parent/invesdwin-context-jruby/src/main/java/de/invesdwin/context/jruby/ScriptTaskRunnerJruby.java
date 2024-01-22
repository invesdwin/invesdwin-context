package de.invesdwin.context.jruby;

import javax.annotation.concurrent.Immutable;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.integration.script.callback.IScriptTaskCallback;
import de.invesdwin.context.integration.script.callback.LoggingDelegateScriptTaskCallback;
import de.invesdwin.context.jruby.callback.JrubyScriptTaskCallbackContext;
import de.invesdwin.context.jruby.pool.JrubyScriptEngineObjectPool;
import de.invesdwin.context.jruby.pool.WrappedJrubyScriptEngine;
import de.invesdwin.util.error.Throwables;
import jakarta.inject.Named;

@Immutable
@Named
public final class ScriptTaskRunnerJruby implements IScriptTaskRunnerJruby, FactoryBean<ScriptTaskRunnerJruby> {

    public static final ScriptTaskRunnerJruby INSTANCE = new ScriptTaskRunnerJruby();

    /**
     * public for ServiceLoader support
     */
    public ScriptTaskRunnerJruby() {}

    @Override
    public <T> T run(final AScriptTaskJruby<T> scriptTask) {
        //get session
        final WrappedJrubyScriptEngine scriptEngine = JrubyScriptEngineObjectPool.INSTANCE.borrowObject();
        final JrubyScriptTaskCallbackContext context;
        final IScriptTaskCallback callback = scriptTask.getCallback();
        if (callback != null) {
            context = new JrubyScriptTaskCallbackContext(LoggingDelegateScriptTaskCallback.maybeWrap(LOG, callback));
        } else {
            context = null;
        }
        try {
            //inputs
            final ScriptTaskEngineJruby engine = new ScriptTaskEngineJruby(scriptEngine);
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
            JrubyScriptEngineObjectPool.INSTANCE.returnObject(scriptEngine);
            return result;
        } catch (final Throwable t) {
            JrubyScriptEngineObjectPool.INSTANCE.invalidateObject(scriptEngine);
            throw Throwables.propagate(t);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    @Override
    public ScriptTaskRunnerJruby getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ScriptTaskRunnerJruby.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
