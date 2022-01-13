package de.invesdwin.context.jshell;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.jshell.pool.JshellScriptEngineObjectPool;
import de.invesdwin.context.jshell.pool.WrappedJshellScriptEngine;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;

@NotThreadSafe
public class ScriptTaskEngineJshell implements IScriptTaskEngine {

    private WrappedJshellScriptEngine javascriptScriptEngine;
    private final ScriptTaskInputsJshell inputs;
    private final ScriptTaskResultsJshell results;

    public ScriptTaskEngineJshell(final WrappedJshellScriptEngine javascriptScriptEngine) {
        this.javascriptScriptEngine = javascriptScriptEngine;
        this.inputs = new ScriptTaskInputsJshell(this);
        this.results = new ScriptTaskResultsJshell(this);
    }

    @Override
    public void eval(final String expression) {
        javascriptScriptEngine.eval(expression);
    }

    @Override
    public ScriptTaskInputsJshell getInputs() {
        return inputs;
    }

    @Override
    public ScriptTaskResultsJshell getResults() {
        return results;
    }

    @Override
    public void close() {
        javascriptScriptEngine = null;
    }

    @Override
    public WrappedJshellScriptEngine unwrap() {
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

    public static ScriptTaskEngineJshell newInstance() {
        return new ScriptTaskEngineJshell(JshellScriptEngineObjectPool.INSTANCE.borrowObject()) {
            @Override
            public void close() {
                final WrappedJshellScriptEngine unwrap = unwrap();
                if (unwrap != null) {
                    JshellScriptEngineObjectPool.INSTANCE.returnObject(unwrap);
                }
                super.close();
            }
        };
    }

}
