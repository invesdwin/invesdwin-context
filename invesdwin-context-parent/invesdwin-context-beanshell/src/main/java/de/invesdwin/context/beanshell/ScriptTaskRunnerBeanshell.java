package de.invesdwin.context.beanshell;

import javax.annotation.concurrent.Immutable;
import jakarta.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.beanshell.pool.BeanshellScriptEngineObjectPool;
import de.invesdwin.context.beanshell.pool.IBeanshellEngine;
import de.invesdwin.util.concurrent.pool.IObjectPool;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class ScriptTaskRunnerBeanshell
        implements IScriptTaskRunnerBeanshell, FactoryBean<ScriptTaskRunnerBeanshell> {

    public static final ScriptTaskRunnerBeanshell INSTANCE = new ScriptTaskRunnerBeanshell();

    /**
     * public for ServiceLoader support
     */
    public ScriptTaskRunnerBeanshell() {
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> T run(final AScriptTaskBeanshell<T> scriptTask) {
        //get session
        final IObjectPool<IBeanshellEngine> pool = (IObjectPool) BeanshellScriptEngineObjectPool.INSTANCE;
        final IBeanshellEngine scriptEngine = pool.borrowObject();
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
            pool.returnObject(scriptEngine);
            return result;
        } catch (final Throwable t) {
            pool.invalidateObject(scriptEngine);
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
