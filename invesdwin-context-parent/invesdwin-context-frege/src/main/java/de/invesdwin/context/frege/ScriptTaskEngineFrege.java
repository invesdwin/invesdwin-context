package de.invesdwin.context.frege;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.frege.pool.FregeScriptEngineObjectPool;
import de.invesdwin.context.frege.pool.WrappedFregeScriptEngine;
import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;

@NotThreadSafe
public class ScriptTaskEngineFrege implements IScriptTaskEngine {

    private WrappedFregeScriptEngine scriptEngine;
    private final ScriptTaskInputsFrege inputs;
    private final ScriptTaskResultsFrege results;

    public ScriptTaskEngineFrege(final WrappedFregeScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
        this.inputs = new ScriptTaskInputsFrege(this);
        this.results = new ScriptTaskResultsFrege(this);
    }

    @Override
    public void eval(final String expression) {
        scriptEngine.eval(expression);
    }

    @Override
    public ScriptTaskInputsFrege getInputs() {
        return inputs;
    }

    @Override
    public ScriptTaskResultsFrege getResults() {
        return results;
    }

    @Override
    public void close() {
        scriptEngine = null;
    }

    @Override
    public WrappedFregeScriptEngine unwrap() {
        return scriptEngine;
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

    public static ScriptTaskEngineFrege newInstance() {
        return new ScriptTaskEngineFrege(FregeScriptEngineObjectPool.INSTANCE.borrowObject()) {
            @Override
            public void close() {
                final WrappedFregeScriptEngine unwrap = unwrap();
                if (unwrap != null) {
                    FregeScriptEngineObjectPool.INSTANCE.returnObject(unwrap);
                }
                super.close();
            }
        };
    }

}
