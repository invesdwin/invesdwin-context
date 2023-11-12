package de.invesdwin.context.mvel;

import javax.annotation.concurrent.Immutable;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.integration.script.callback.IScriptTaskCallback;
import de.invesdwin.context.mvel.callback.MvelScriptTaskCallbackContext;
import de.invesdwin.context.mvel.pool.WrappedMvelScriptEngine;
import de.invesdwin.util.concurrent.pool.IObjectPool;
import de.invesdwin.util.error.Throwables;
import jakarta.inject.Named;

@Immutable
@Named
public final class ScriptTaskRunnerMvel implements IScriptTaskRunnerMvel, FactoryBean<ScriptTaskRunnerMvel> {

    public static final ScriptTaskRunnerMvel INSTANCE = new ScriptTaskRunnerMvel();

    /**
     * public for ServiceLoader support
     */
    public ScriptTaskRunnerMvel() {}

    @Override
    public <T> T run(final AScriptTaskMvel<T> scriptTask) {
        //get session
        final IObjectPool<WrappedMvelScriptEngine> enginePool = ScriptTaskEngineMvel.getEnginePool();
        final WrappedMvelScriptEngine scriptEngine = enginePool.borrowObject();
        final MvelScriptTaskCallbackContext context;
        final IScriptTaskCallback callback = scriptTask.getCallback();
        if (callback != null) {
            context = new MvelScriptTaskCallbackContext(callback);
        } else {
            context = null;
        }
        try {
            //inputs
            final ScriptTaskEngineMvel engine = new ScriptTaskEngineMvel(scriptEngine);
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
            enginePool.returnObject(scriptEngine);
            return result;
        } catch (final Throwable t) {
            enginePool.invalidateObject(scriptEngine);
            throw Throwables.propagate(t);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    @Override
    public ScriptTaskRunnerMvel getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ScriptTaskRunnerMvel.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
