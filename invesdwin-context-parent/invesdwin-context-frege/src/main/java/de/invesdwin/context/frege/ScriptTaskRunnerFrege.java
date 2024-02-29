package de.invesdwin.context.frege;

import javax.annotation.concurrent.Immutable;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.frege.callback.FregeScriptTaskCallbackContext;
import de.invesdwin.context.frege.pool.FregeScriptEngineObjectPool;
import de.invesdwin.context.frege.pool.WrappedFregeScriptEngine;
import de.invesdwin.context.integration.script.callback.IScriptTaskCallback;
import de.invesdwin.context.integration.script.callback.LoggingDelegateScriptTaskCallback;
import de.invesdwin.util.error.Throwables;
import jakarta.inject.Named;

@Immutable
@Named
public final class ScriptTaskRunnerFrege implements IScriptTaskRunnerFrege, FactoryBean<ScriptTaskRunnerFrege> {

    public static final ScriptTaskRunnerFrege INSTANCE = new ScriptTaskRunnerFrege();

    /**
     * public for ServiceLoader support
     */
    public ScriptTaskRunnerFrege() {}

    @Override
    public <T> T run(final AScriptTaskFrege<T> scriptTask) {
        //get session
        final WrappedFregeScriptEngine scriptEngine = FregeScriptEngineObjectPool.INSTANCE.borrowObject();
        final FregeScriptTaskCallbackContext context;
        final IScriptTaskCallback callback = scriptTask.getCallback();
        if (callback != null) {
            context = new FregeScriptTaskCallbackContext(LoggingDelegateScriptTaskCallback.maybeWrap(LOG, callback));
        } else {
            context = null;
        }
        try {
            //inputs
            final ScriptTaskEngineFrege engine = new ScriptTaskEngineFrege(scriptEngine);
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
            FregeScriptEngineObjectPool.INSTANCE.returnObject(scriptEngine);
            return result;
        } catch (final Throwable t) {
            FregeScriptEngineObjectPool.INSTANCE.invalidateObject(scriptEngine);
            throw Throwables.propagate(t);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    @Override
    public ScriptTaskRunnerFrege getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ScriptTaskRunnerFrege.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
