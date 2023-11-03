package de.invesdwin.context.groovy;

import javax.annotation.concurrent.Immutable;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.groovy.callback.GroovyScriptTaskCallbackContext;
import de.invesdwin.context.groovy.pool.WrappedGroovyShell;
import de.invesdwin.context.integration.script.callback.IScriptTaskCallback;
import de.invesdwin.util.concurrent.pool.IObjectPool;
import de.invesdwin.util.error.Throwables;
import jakarta.inject.Named;

@Immutable
@Named
public final class ScriptTaskRunnerGroovy implements IScriptTaskRunnerGroovy, FactoryBean<ScriptTaskRunnerGroovy> {

    public static final ScriptTaskRunnerGroovy INSTANCE = new ScriptTaskRunnerGroovy();

    /**
     * public for ServiceLoader support
     */
    public ScriptTaskRunnerGroovy() {}

    @Override
    public <T> T run(final AScriptTaskGroovy<T> scriptTask) {
        //get session
        final IObjectPool<WrappedGroovyShell> enginePool = ScriptTaskEngineGroovy.getEnginePool();
        final WrappedGroovyShell groovyEngine = enginePool.borrowObject();
        final GroovyScriptTaskCallbackContext context;
        final IScriptTaskCallback callback = scriptTask.getCallback();
        if (callback != null) {
            context = new GroovyScriptTaskCallbackContext(callback);
        } else {
            context = null;
        }
        try {
            //inputs
            final ScriptTaskEngineGroovy engine = new ScriptTaskEngineGroovy(groovyEngine);
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
            enginePool.returnObject(groovyEngine);
            return result;
        } catch (final Throwable t) {
            enginePool.invalidateObject(groovyEngine);
            throw Throwables.propagate(t);
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    @Override
    public ScriptTaskRunnerGroovy getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ScriptTaskRunnerGroovy.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
