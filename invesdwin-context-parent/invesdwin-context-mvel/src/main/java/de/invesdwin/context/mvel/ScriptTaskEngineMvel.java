package de.invesdwin.context.mvel;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.mvel.pool.MvelScriptEngineObjectPool;
import de.invesdwin.context.mvel.pool.StrictMvelScriptEngineObjectPool;
import de.invesdwin.context.mvel.pool.WrappedMvelScriptEngine;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;
import de.invesdwin.util.concurrent.pool.IObjectPool;

@NotThreadSafe
public class ScriptTaskEngineMvel implements IScriptTaskEngine {

    private WrappedMvelScriptEngine scriptEngine;
    private final ScriptTaskInputsMvel inputs;
    private final ScriptTaskResultsMvel results;

    public ScriptTaskEngineMvel(final WrappedMvelScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
        this.inputs = new ScriptTaskInputsMvel(this);
        this.results = new ScriptTaskResultsMvel(this);
    }

    @Override
    public void eval(final String expression) {
        scriptEngine.eval(expression);
    }

    @Override
    public ScriptTaskInputsMvel getInputs() {
        return inputs;
    }

    @Override
    public ScriptTaskResultsMvel getResults() {
        return results;
    }

    @Override
    public void close() {
        scriptEngine = null;
    }

    @Override
    public WrappedMvelScriptEngine unwrap() {
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

    public static ScriptTaskEngineMvel newInstance() {
        return newInstance(MvelProperties.isStrict());
    }

    public static ScriptTaskEngineMvel newInstance(final boolean strict) {
        final IObjectPool<WrappedMvelScriptEngine> pool = getEnginePool(strict);
        return new ScriptTaskEngineMvel(pool.borrowObject()) {
            @Override
            public void close() {
                final WrappedMvelScriptEngine unwrap = unwrap();
                if (unwrap != null) {
                    pool.returnObject(unwrap);
                }
                super.close();
            }
        };
    }

    public static IObjectPool<WrappedMvelScriptEngine> getEnginePool() {
        return getEnginePool(MvelProperties.isStrict());
    }

    public static IObjectPool<WrappedMvelScriptEngine> getEnginePool(final boolean strict) {
        if (strict) {
            return StrictMvelScriptEngineObjectPool.INSTANCE;
        } else {
            return MvelScriptEngineObjectPool.INSTANCE;
        }
    }

}
