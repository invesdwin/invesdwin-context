package de.invesdwin.context.kotlin;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.kotlin.pool.KotlinScriptEngineObjectPool;
import de.invesdwin.context.kotlin.pool.WrappedKotlinScriptEngine;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;

@NotThreadSafe
public class ScriptTaskEngineKotlin implements IScriptTaskEngine {

    private WrappedKotlinScriptEngine javascriptScriptEngine;
    private final ScriptTaskInputsKotlin inputs;
    private final ScriptTaskResultsKotlin results;

    public ScriptTaskEngineKotlin(final WrappedKotlinScriptEngine javascriptScriptEngine) {
        this.javascriptScriptEngine = javascriptScriptEngine;
        this.inputs = new ScriptTaskInputsKotlin(this);
        this.results = new ScriptTaskResultsKotlin(this);
    }

    /**
     * https://github.com/mrj0/GroovyScriptEngine/issues/55
     */
    @Override
    public void eval(final String expression) {
        javascriptScriptEngine.eval(expression);
    }

    @Override
    public ScriptTaskInputsKotlin getInputs() {
        return inputs;
    }

    @Override
    public ScriptTaskResultsKotlin getResults() {
        return results;
    }

    @Override
    public void close() {
        javascriptScriptEngine = null;
    }

    @Override
    public WrappedKotlinScriptEngine unwrap() {
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

    public static ScriptTaskEngineKotlin newInstance() {
        return new ScriptTaskEngineKotlin(KotlinScriptEngineObjectPool.INSTANCE.borrowObject()) {
            @Override
            public void close() {
                final WrappedKotlinScriptEngine unwrap = unwrap();
                if (unwrap != null) {
                    KotlinScriptEngineObjectPool.INSTANCE.returnObject(unwrap);
                }
                super.close();
            }
        };
    }

}
