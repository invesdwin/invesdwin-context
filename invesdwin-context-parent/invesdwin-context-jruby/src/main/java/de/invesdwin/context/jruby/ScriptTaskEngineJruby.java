package de.invesdwin.context.jruby;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.jruby.pool.JrubyScriptEngineObjectPool;
import de.invesdwin.context.jruby.pool.WrappedJrubyScriptEngine;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;

@NotThreadSafe
public class ScriptTaskEngineJruby implements IScriptTaskEngine {

    private WrappedJrubyScriptEngine javascriptScriptEngine;
    private final ScriptTaskInputsJruby inputs;
    private final ScriptTaskResultsJruby results;

    public ScriptTaskEngineJruby(final WrappedJrubyScriptEngine javascriptScriptEngine) {
        this.javascriptScriptEngine = javascriptScriptEngine;
        this.inputs = new ScriptTaskInputsJruby(this);
        this.results = new ScriptTaskResultsJruby(this);
    }

    @Override
    public void eval(final String expression) {
        javascriptScriptEngine.eval(expression);
    }

    @Override
    public ScriptTaskInputsJruby getInputs() {
        return inputs;
    }

    @Override
    public ScriptTaskResultsJruby getResults() {
        return results;
    }

    @Override
    public void close() {
        javascriptScriptEngine = null;
    }

    @Override
    public WrappedJrubyScriptEngine unwrap() {
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

    public static ScriptTaskEngineJruby newInstance() {
        return new ScriptTaskEngineJruby(JrubyScriptEngineObjectPool.INSTANCE.borrowObject()) {
            @Override
            public void close() {
                final WrappedJrubyScriptEngine unwrap = unwrap();
                if (unwrap != null) {
                    JrubyScriptEngineObjectPool.INSTANCE.returnObject(unwrap);
                }
                super.close();
            }
        };
    }

}
