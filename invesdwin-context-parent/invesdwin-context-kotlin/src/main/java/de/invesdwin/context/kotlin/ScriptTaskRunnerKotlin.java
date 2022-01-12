package de.invesdwin.context.kotlin;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.kotlin.pool.KotlinScriptEngineObjectPool;
import de.invesdwin.context.kotlin.pool.WrappedKotlinScriptEngine;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class ScriptTaskRunnerKotlin implements IScriptTaskRunnerKotlin, FactoryBean<ScriptTaskRunnerKotlin> {

    public static final ScriptTaskRunnerKotlin INSTANCE = new ScriptTaskRunnerKotlin();

    /**
     * public for ServiceLoader support
     */
    public ScriptTaskRunnerKotlin() {
    }

    @Override
    public <T> T run(final AScriptTaskKotlin<T> scriptTask) {
        //get session
        final WrappedKotlinScriptEngine scriptEngine = KotlinScriptEngineObjectPool.INSTANCE.borrowObject();
        try {
            //inputs
            final ScriptTaskEngineKotlin engine = new ScriptTaskEngineKotlin(scriptEngine);
            scriptTask.populateInputs(engine.getInputs());

            //execute
            scriptTask.executeScript(engine);

            //results
            final T result = scriptTask.extractResults(engine.getResults());
            engine.close();

            //return
            KotlinScriptEngineObjectPool.INSTANCE.returnObject(scriptEngine);
            return result;
        } catch (final Throwable t) {
            KotlinScriptEngineObjectPool.INSTANCE.invalidateObject(scriptEngine);
            throw Throwables.propagate(t);
        }
    }

    @Override
    public ScriptTaskRunnerKotlin getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ScriptTaskRunnerKotlin.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
