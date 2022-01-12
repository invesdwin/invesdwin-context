package de.invesdwin.context.javascript;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.javascript.pool.JavascriptScriptEngineObjectPool;
import de.invesdwin.context.javascript.pool.WrappedJavascriptScriptEngine;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;

@NotThreadSafe
public class ScriptTaskEngineJavascript implements IScriptTaskEngine {

    private WrappedJavascriptScriptEngine javascriptScriptEngine;
    private final ScriptTaskInputsJavascript inputs;
    private final ScriptTaskResultsJavascript results;

    public ScriptTaskEngineJavascript(final WrappedJavascriptScriptEngine javascriptScriptEngine) {
        this.javascriptScriptEngine = javascriptScriptEngine;
        this.inputs = new ScriptTaskInputsJavascript(this);
        this.results = new ScriptTaskResultsJavascript(this);
    }

    @Override
    public void eval(final String expression) {
        javascriptScriptEngine.eval(expression);
    }

    @Override
    public ScriptTaskInputsJavascript getInputs() {
        return inputs;
    }

    @Override
    public ScriptTaskResultsJavascript getResults() {
        return results;
    }

    @Override
    public void close() {
        javascriptScriptEngine = null;
    }

    @Override
    public WrappedJavascriptScriptEngine unwrap() {
        return javascriptScriptEngine;
    }

    /**
     * Each instance has its own engine, so no shared locking required.
     */
    @Override
    public ILock getSharedLock() {
        return DisabledLock.INSTANCE;
    }

    @Override
    public WrappedExecutorService getSharedExecutor() {
        return null;
    }

    public static ScriptTaskEngineJavascript newInstance() {
        return new ScriptTaskEngineJavascript(JavascriptScriptEngineObjectPool.INSTANCE.borrowObject()) {
            @Override
            public void close() {
                final WrappedJavascriptScriptEngine unwrap = unwrap();
                if (unwrap != null) {
                    JavascriptScriptEngineObjectPool.INSTANCE.returnObject(unwrap);
                }
                super.close();
            }
        };
    }

}
