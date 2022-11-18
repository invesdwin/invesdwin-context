package de.invesdwin.context.jruby;

import javax.annotation.concurrent.Immutable;
import jakarta.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.jruby.pool.JrubyScriptEngineObjectPool;
import de.invesdwin.context.jruby.pool.WrappedJrubyScriptEngine;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class ScriptTaskRunnerJruby
        implements IScriptTaskRunnerJruby, FactoryBean<ScriptTaskRunnerJruby> {

    public static final ScriptTaskRunnerJruby INSTANCE = new ScriptTaskRunnerJruby();

    /**
     * public for ServiceLoader support
     */
    public ScriptTaskRunnerJruby() {
    }

    @Override
    public <T> T run(final AScriptTaskJruby<T> scriptTask) {
        //get session
        final WrappedJrubyScriptEngine scriptEngine = JrubyScriptEngineObjectPool.INSTANCE.borrowObject();
        try {
            //inputs
            final ScriptTaskEngineJruby engine = new ScriptTaskEngineJruby(scriptEngine);
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
