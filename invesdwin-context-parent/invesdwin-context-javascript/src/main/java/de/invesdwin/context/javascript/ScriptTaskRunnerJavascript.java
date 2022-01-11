package de.invesdwin.context.javascript;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import org.springframework.beans.factory.FactoryBean;

import de.invesdwin.context.javascript.pool.JavascriptScriptEngineObjectPool;
import de.invesdwin.context.javascript.pool.WrappedJavascriptScriptEngine;
import de.invesdwin.util.error.Throwables;

@Immutable
@Named
public final class ScriptTaskRunnerJavascript implements IScriptTaskRunnerJavascript, FactoryBean<ScriptTaskRunnerJavascript> {

    public static final ScriptTaskRunnerJavascript INSTANCE = new ScriptTaskRunnerJavascript();

    /**
     * public for ServiceLoader support
     */
    public ScriptTaskRunnerJavascript() {
    }

    @Override
    public <T> T run(final AScriptTaskJavascript<T> scriptTask) {
        //get session
        final WrappedJavascriptScriptEngine pyScriptEngine = JavascriptScriptEngineObjectPool.INSTANCE.borrowObject();
        try {
            //inputs
            final ScriptTaskEngineJavascript engine = new ScriptTaskEngineJavascript(pyScriptEngine);
            scriptTask.populateInputs(engine.getInputs());

            //execute
            scriptTask.executeScript(engine);

            //results
            final T result = scriptTask.extractResults(engine.getResults());
            engine.close();

            //return
            JavascriptScriptEngineObjectPool.INSTANCE.returnObject(pyScriptEngine);
            return result;
        } catch (final Throwable t) {
            JavascriptScriptEngineObjectPool.INSTANCE.destroyObject(pyScriptEngine);
            throw Throwables.propagate(t);
        }
    }

    @Override
    public ScriptTaskRunnerJavascript getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ScriptTaskRunnerJavascript.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
