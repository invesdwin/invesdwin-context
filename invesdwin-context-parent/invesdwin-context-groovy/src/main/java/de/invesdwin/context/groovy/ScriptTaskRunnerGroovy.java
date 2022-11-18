package de.invesdwin.context.groovy;

import javax.annotation.concurrent.Immutable;
import jakarta.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.groovy.pool.WrappedGroovyShell;
import de.invesdwin.util.concurrent.pool.IObjectPool;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class ScriptTaskRunnerGroovy implements IScriptTaskRunnerGroovy, FactoryBean<ScriptTaskRunnerGroovy> {

    public static final ScriptTaskRunnerGroovy INSTANCE = new ScriptTaskRunnerGroovy();

    /**
     * public for ServiceLoader support
     */
    public ScriptTaskRunnerGroovy() {
    }

    @Override
    public <T> T run(final AScriptTaskGroovy<T> scriptTask) {
        //get session
        final IObjectPool<WrappedGroovyShell> enginePool = ScriptTaskEngineGroovy.getEnginePool();
        final WrappedGroovyShell groovyEngine = enginePool.borrowObject();
        try {
            //inputs
            final ScriptTaskEngineGroovy engine = new ScriptTaskEngineGroovy(groovyEngine);
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
