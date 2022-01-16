package de.invesdwin.context.beanshell;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.beanshell.pool.BeanshellInterpreterObjectPool;
import de.invesdwin.context.beanshell.pool.IBeanshellEngine;
import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;
import de.invesdwin.util.concurrent.pool.IObjectPool;

@NotThreadSafe
public class ScriptTaskEngineBeanshell implements IScriptTaskEngine {

    private IBeanshellEngine scriptEngine;
    private final ScriptTaskInputsBeanshell inputs;
    private final ScriptTaskResultsBeanshell results;

    public ScriptTaskEngineBeanshell(final IBeanshellEngine scriptEngine) {
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
    public IBeanshellEngine unwrap() {
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ScriptTaskEngineBeanshell newInstance() {
        final IObjectPool<IBeanshellEngine> pool = (IObjectPool) BeanshellInterpreterObjectPool.INSTANCE;
        return new ScriptTaskEngineBeanshell(pool.borrowObject()) {
            @Override
            public void close() {
                final IBeanshellEngine unwrap = unwrap();
                if (unwrap != null) {
                    pool.returnObject(unwrap);
                }
                super.close();
            }
        };
    }

}
