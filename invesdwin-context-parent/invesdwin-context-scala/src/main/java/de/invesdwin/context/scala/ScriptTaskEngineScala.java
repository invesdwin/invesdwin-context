package de.invesdwin.context.scala;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.scala.pool.ScalaScriptEngineObjectPool;
import de.invesdwin.context.scala.pool.WrappedScalaScriptEngine;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;

@NotThreadSafe
public class ScriptTaskEngineScala implements IScriptTaskEngine {

    private WrappedScalaScriptEngine scalaScriptEngine;
    private final ScriptTaskInputsScala inputs;
    private final ScriptTaskResultsScala results;

    public ScriptTaskEngineScala(final WrappedScalaScriptEngine javascriptScriptEngine) {
        this.scalaScriptEngine = javascriptScriptEngine;
        this.inputs = new ScriptTaskInputsScala(this);
        this.results = new ScriptTaskResultsScala(this);
    }

    @Override
    public void eval(final String expression) {
        scalaScriptEngine.eval(expression);
    }

    @Override
    public ScriptTaskInputsScala getInputs() {
        return inputs;
    }

    @Override
    public ScriptTaskResultsScala getResults() {
        return results;
    }

    @Override
    public void close() {
        scalaScriptEngine = null;
    }

    @Override
    public WrappedScalaScriptEngine unwrap() {
        return scalaScriptEngine;
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

    public static ScriptTaskEngineScala newInstance() {
        return new ScriptTaskEngineScala(ScalaScriptEngineObjectPool.INSTANCE.borrowObject()) {
            @Override
            public void close() {
                final WrappedScalaScriptEngine unwrap = unwrap();
                if (unwrap != null) {
                    ScalaScriptEngineObjectPool.INSTANCE.returnObject(unwrap);
                }
                super.close();
            }
        };
    }

}
