package de.invesdwin.context.clojure;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.clojure.pool.ClojureEngineObjectPool;
import de.invesdwin.context.clojure.pool.WrappedClojureEngine;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class ScriptTaskRunnerClojure
        implements IScriptTaskRunnerClojure, FactoryBean<ScriptTaskRunnerClojure> {

    public static final ScriptTaskRunnerClojure INSTANCE = new ScriptTaskRunnerClojure();

    /**
     * public for ServiceLoader support
     */
    public ScriptTaskRunnerClojure() {
    }

    @Override
    public <T> T run(final AScriptTaskClojure<T> scriptTask) {
        //get session
        final WrappedClojureEngine scriptEngine = ClojureEngineObjectPool.INSTANCE.borrowObject();
        try {
            //inputs
            final ScriptTaskEngineClojure engine = new ScriptTaskEngineClojure(scriptEngine);
            scriptTask.populateInputs(engine.getInputs());

            //execute
            scriptTask.executeScript(engine);

            //results
            final T result = scriptTask.extractResults(engine.getResults());
            engine.close();

            //return
            ClojureEngineObjectPool.INSTANCE.returnObject(scriptEngine);
            return result;
        } catch (final Throwable t) {
            ClojureEngineObjectPool.INSTANCE.invalidateObject(scriptEngine);
            throw Throwables.propagate(t);
        }
    }

    @Override
    public ScriptTaskRunnerClojure getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ScriptTaskRunnerClojure.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
