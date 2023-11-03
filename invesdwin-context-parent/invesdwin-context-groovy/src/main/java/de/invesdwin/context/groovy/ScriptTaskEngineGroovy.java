package de.invesdwin.context.groovy;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.groovy.pool.GroovyShellObjectPool;
import de.invesdwin.context.groovy.pool.StrictGroovyShellObjectPool;
import de.invesdwin.context.groovy.pool.WrappedGroovyShell;
import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;
import de.invesdwin.util.concurrent.pool.IObjectPool;

@NotThreadSafe
public class ScriptTaskEngineGroovy implements IScriptTaskEngine {

    private WrappedGroovyShell groovyEngine;
    private final ScriptTaskInputsGroovy inputs;
    private final ScriptTaskResultsGroovy results;

    public ScriptTaskEngineGroovy(final WrappedGroovyShell groovyEngine) {
        this.groovyEngine = groovyEngine;
        this.inputs = new ScriptTaskInputsGroovy(this);
        this.results = new ScriptTaskResultsGroovy(this);
    }

    /**
     * https://github.com/mrj0/GroovyScriptEngine/issues/55
     */
    @Override
    public void eval(final String expression) {
        groovyEngine.eval(expression);
    }

    @Override
    public ScriptTaskInputsGroovy getInputs() {
        return inputs;
    }

    @Override
    public ScriptTaskResultsGroovy getResults() {
        return results;
    }

    @Override
    public void close() {
        groovyEngine = null;
    }

    @Override
    public WrappedGroovyShell unwrap() {
        return groovyEngine;
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

    public static ScriptTaskEngineGroovy newInstance() {
        return newInstance(GroovyProperties.isStrict());
    }

    public static ScriptTaskEngineGroovy newInstance(final boolean strict) {
        final IObjectPool<WrappedGroovyShell> pool = getEnginePool(strict);
        return new ScriptTaskEngineGroovy(pool.borrowObject()) {
            @Override
            public void close() {
                final WrappedGroovyShell unwrap = unwrap();
                if (unwrap != null) {
                    pool.returnObject(unwrap);
                }
                super.close();
            }
        };
    }

    public static IObjectPool<WrappedGroovyShell> getEnginePool() {
        return getEnginePool(GroovyProperties.isStrict());
    }

    public static IObjectPool<WrappedGroovyShell> getEnginePool(final boolean strict) {
        if (strict) {
            return StrictGroovyShellObjectPool.INSTANCE;
        } else {
            return GroovyShellObjectPool.INSTANCE;
        }
    }

}
