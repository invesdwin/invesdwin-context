package de.invesdwin.context.clojure;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.clojure.pool.ClojureEngineObjectPool;
import de.invesdwin.context.clojure.pool.WrappedClojureEngine;
import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;

@NotThreadSafe
public class ScriptTaskEngineClojure implements IScriptTaskEngine {

    private WrappedClojureEngine clojureScriptEngine;
    private final ScriptTaskInputsClojure inputs;
    private final ScriptTaskResultsClojure results;

    public ScriptTaskEngineClojure(final WrappedClojureEngine javascriptScriptEngine) {
        this.clojureScriptEngine = javascriptScriptEngine;
        this.inputs = new ScriptTaskInputsClojure(this);
        this.results = new ScriptTaskResultsClojure(this);
    }

    @Override
    public void eval(final String expression) {
        clojureScriptEngine.eval(expression);
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
        clojureScriptEngine = null;
    }

    @Override
    public WrappedClojureEngine unwrap() {
        return clojureScriptEngine;
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
        return new ScriptTaskEngineClojure(ClojureEngineObjectPool.INSTANCE.borrowObject()) {
            @Override
            public void close() {
                final WrappedClojureEngine unwrap = unwrap();
                if (unwrap != null) {
                    ClojureEngineObjectPool.INSTANCE.returnObject(unwrap);
                }
                super.close();
            }
        };
    }

}
