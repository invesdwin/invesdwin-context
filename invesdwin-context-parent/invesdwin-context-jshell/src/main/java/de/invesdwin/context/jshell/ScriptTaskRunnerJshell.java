package de.invesdwin.context.jshell;

import javax.annotation.concurrent.Immutable;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.integration.script.callback.IScriptTaskCallback;
import de.invesdwin.context.jshell.callback.JshellScriptTaskCallbackContext;
import de.invesdwin.context.jshell.pool.JshellScriptEngineObjectPool;
import de.invesdwin.context.jshell.pool.WrappedJshellScriptEngine;
import de.invesdwin.util.error.Throwables;
import jakarta.inject.Named;

@Immutable
@Named
public final class ScriptTaskRunnerJshell implements IScriptTaskRunnerJshell, FactoryBean<ScriptTaskRunnerJshell> {

    public static final ScriptTaskRunnerJshell INSTANCE = new ScriptTaskRunnerJshell();

    /**
     * public for ServiceLoader support
     */
    public ScriptTaskRunnerJshell() {}

    @Override
    public <T> T run(final AScriptTaskJshell<T> scriptTask) {
        //get session
        final WrappedJshellScriptEngine scriptEngine = JshellScriptEngineObjectPool.INSTANCE.borrowObject();
        final JshellScriptTaskCallbackContext context;
        final IScriptTaskCallback callback = scriptTask.getCallback();
        if (callback != null) {
            context = new JshellScriptTaskCallbackContext(callback);
        } else {
            context = null;
        }
        try {
            //inputs
            final ScriptTaskEngineJshell engine = new ScriptTaskEngineJshell(scriptEngine);
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
            JshellScriptEngineObjectPool.INSTANCE.returnObject(scriptEngine);
            return result;
        } catch (final Throwable t) {
            JshellScriptEngineObjectPool.INSTANCE.invalidateObject(scriptEngine);
            throw Throwables.propagate(t);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    @Override
    public ScriptTaskRunnerJshell getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ScriptTaskRunnerJshell.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
