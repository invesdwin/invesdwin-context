package de.invesdwin.context.jshell;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.jshell.pool.BeanshellScriptEngineObjectPool;
import de.invesdwin.context.jshell.pool.WrappedBeanshellScriptEngine;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;

@NotThreadSafe
public class ScriptTaskEngineBeanshell implements IScriptTaskEngine {

    private WrappedBeanshellScriptEngine scriptEngine;
    private final ScriptTaskInputsBeanshell inputs;
    private final ScriptTaskResultsBeanshell results;

    public ScriptTaskEngineBeanshell(final WrappedBeanshellScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
        this.inputs = new ScriptTaskInputsBeanshell(this);
        this.results = new ScriptTaskResultsBeanshell(this);
    }

    @Override
    public void eval(final String expression) {
        scriptEngine.eval(expression);
    }

    @Override
    public ScriptTaskInputsBeanshell getInputs() {
        return inputs;
    }

    @Override
    public ScriptTaskResultsBeanshell getResults() {
        return results;
    }

    @Override
    public void close() {
        scriptEngine = null;
    }

    @Override
    public WrappedBeanshellScriptEngine unwrap() {
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

    public static ScriptTaskEngineBeanshell newInstance() {
        return new ScriptTaskEngineBeanshell(BeanshellScriptEngineObjectPool.INSTANCE.borrowObject()) {
            @Override
            public void close() {
                final WrappedBeanshellScriptEngine unwrap = unwrap();
                if (unwrap != null) {
                    BeanshellScriptEngineObjectPool.INSTANCE.returnObject(unwrap);
                }
                super.close();
            }
        };
    }

}
