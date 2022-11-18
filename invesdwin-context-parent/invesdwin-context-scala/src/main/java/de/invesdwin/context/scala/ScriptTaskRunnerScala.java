package de.invesdwin.context.scala;

import javax.annotation.concurrent.Immutable;
import jakarta.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.scala.pool.ScalaScriptEngineObjectPool;
import de.invesdwin.context.scala.pool.WrappedScalaScriptEngine;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class ScriptTaskRunnerScala implements IScriptTaskRunnerScala, FactoryBean<ScriptTaskRunnerScala> {

    public static final ScriptTaskRunnerScala INSTANCE = new ScriptTaskRunnerScala();

    /**
     * public for ServiceLoader support
     */
    public ScriptTaskRunnerScala() {
    }

    @Override
    public <T> T run(final AScriptTaskScala<T> scriptTask) {
        //get session
        final WrappedScalaScriptEngine scriptEngine = ScalaScriptEngineObjectPool.INSTANCE.borrowObject();
        try {
            //inputs
            final ScriptTaskEngineScala engine = new ScriptTaskEngineScala(scriptEngine);
            scriptTask.populateInputs(engine.getInputs());

            //execute
            scriptTask.executeScript(engine);

            //results
            final T result = scriptTask.extractResults(engine.getResults());
            engine.close();

            //return
            ScalaScriptEngineObjectPool.INSTANCE.returnObject(scriptEngine);
            return result;
        } catch (final Throwable t) {
            ScalaScriptEngineObjectPool.INSTANCE.invalidateObject(scriptEngine);
            throw Throwables.propagate(t);
        }
    }

    @Override
    public ScriptTaskRunnerScala getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ScriptTaskRunnerScala.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
