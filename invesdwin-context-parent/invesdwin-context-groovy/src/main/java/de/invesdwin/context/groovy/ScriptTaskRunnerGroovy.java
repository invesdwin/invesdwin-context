package de.invesdwin.context.groovy;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.groovy.pool.GroovyShellObjectPool;
import de.invesdwin.context.groovy.pool.WrappedGroovyShell;
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
        final WrappedGroovyShell pyScriptEngine = GroovyShellObjectPool.INSTANCE.borrowObject();
        try {
            //inputs
            final ScriptTaskEngineGroovy engine = new ScriptTaskEngineGroovy(pyScriptEngine);
            scriptTask.populateInputs(engine.getInputs());

            //execute
            scriptTask.executeScript(engine);

            //results
            final T result = scriptTask.extractResults(engine.getResults());
            engine.close();

            //return
            GroovyShellObjectPool.INSTANCE.returnObject(pyScriptEngine);
            return result;
        } catch (final Throwable t) {
            GroovyShellObjectPool.INSTANCE.destroyObject(pyScriptEngine);
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
