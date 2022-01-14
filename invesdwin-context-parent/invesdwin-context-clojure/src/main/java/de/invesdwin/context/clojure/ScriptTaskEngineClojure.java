package de.invesdwin.context.clojure;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.clojure.pool.ClojureScriptEngineObjectPool;
import de.invesdwin.context.clojure.pool.WrappedClojureScriptEngine;
import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;

@NotThreadSafe
public class ScriptTaskEngineClojure implements IScriptTaskEngine {

    private WrappedClojureScriptEngine javascriptScriptEngine;
    private final ScriptTaskInputsClojure inputs;
    private final ScriptTaskResultsClojure results;

    public ScriptTaskEngineClojure(final WrappedClojureScriptEngine javascriptScriptEngine) {
        this.javascriptScriptEngine = javascriptScriptEngine;
        this.inputs = new ScriptTaskInputsClojure(this);
        this.results = new ScriptTaskResultsClojure(this);
    }

    @Override
    public void eval(final String expression) {
        javascriptScriptEngine.eval(expression);
    }

    @Override
    public ScriptTaskInputsClojure getInputs() {
        return inputs;
    }

    @Override
    public ScriptTaskResultsClojure getResults() {
        return results;
    }

    @Override
    public void close() {
        javascriptScriptEngine = null;
    }

    @Override
    public WrappedClojureScriptEngine unwrap() {
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

    public static ScriptTaskEngineClojure newInstance() {
        return new ScriptTaskEngineClojure(ClojureScriptEngineObjectPool.INSTANCE.borrowObject()) {
            @Override
            public void close() {
                final WrappedClojureScriptEngine unwrap = unwrap();
                if (unwrap != null) {
                    ClojureScriptEngineObjectPool.INSTANCE.returnObject(unwrap);
                }
                super.close();
            }
        };
    }

}
