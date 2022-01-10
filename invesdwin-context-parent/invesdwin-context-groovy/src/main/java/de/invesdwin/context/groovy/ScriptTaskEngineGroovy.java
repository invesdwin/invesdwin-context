package de.invesdwin.context.groovy;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.groovy.pool.GroovyShellObjectPool;
import de.invesdwin.context.groovy.pool.WrappedGroovyShell;
import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;

@NotThreadSafe
public class ScriptTaskEngineGroovy implements IScriptTaskEngine {

    private WrappedGroovyShell groovyScriptEngine;
    private final ScriptTaskInputsGroovy inputs;
    private final ScriptTaskResultsGroovy results;

    public ScriptTaskEngineGroovy(final WrappedGroovyShell groovyScriptEngine) {
        this.groovyScriptEngine = groovyScriptEngine;
        this.inputs = new ScriptTaskInputsGroovy(this);
        this.results = new ScriptTaskResultsGroovy(this);
    }

    /**
     * https://github.com/mrj0/GroovyScriptEngine/issues/55
     */
    @Override
    public void eval(final String expression) {
        groovyScriptEngine.eval(expression);
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
        groovyScriptEngine = null;
    }

    @Override
    public WrappedGroovyShell unwrap() {
        return groovyScriptEngine;
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
        return new ScriptTaskEngineGroovy(GroovyShellObjectPool.INSTANCE.borrowObject()) {
            @Override
            public void close() {
                final WrappedGroovyShell unwrap = unwrap();
                if (unwrap != null) {
                    GroovyShellObjectPool.INSTANCE.returnObject(unwrap);
                }
                super.close();
            }
        };
    }

}
