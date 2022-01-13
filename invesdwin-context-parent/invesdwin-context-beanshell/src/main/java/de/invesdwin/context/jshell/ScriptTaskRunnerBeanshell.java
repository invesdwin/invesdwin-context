package de.invesdwin.context.jshell;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.jshell.pool.BeanshellInterpreterObjectPool;
import de.invesdwin.context.jshell.pool.WrappedBeanshellInterpreter;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class ScriptTaskRunnerBeanshell implements IScriptTaskRunnerBeanshell, FactoryBean<ScriptTaskRunnerBeanshell> {

    public static final ScriptTaskRunnerBeanshell INSTANCE = new ScriptTaskRunnerBeanshell();

    /**
     * public for ServiceLoader support
     */
    public ScriptTaskRunnerBeanshell() {
    }

    @Override
    public <T> T run(final AScriptTaskBeanshell<T> scriptTask) {
        //get session
        final WrappedBeanshellInterpreter scriptEngine = BeanshellInterpreterObjectPool.INSTANCE.borrowObject();
        try {
            //inputs
            final ScriptTaskEngineBeanshell engine = new ScriptTaskEngineBeanshell(scriptEngine);
            scriptTask.populateInputs(engine.getInputs());

            //execute
            scriptTask.executeScript(engine);

            //results
            final T result = scriptTask.extractResults(engine.getResults());
            engine.close();

            //return
            BeanshellInterpreterObjectPool.INSTANCE.returnObject(scriptEngine);
            return result;
        } catch (final Throwable t) {
            BeanshellInterpreterObjectPool.INSTANCE.invalidateObject(scriptEngine);
            throw Throwables.propagate(t);
        }
    }

    @Override
    public ScriptTaskRunnerBeanshell getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ScriptTaskRunnerBeanshell.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
