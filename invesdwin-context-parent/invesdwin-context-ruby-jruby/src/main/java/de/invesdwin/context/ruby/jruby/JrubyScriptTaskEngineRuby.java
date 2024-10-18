package de.invesdwin.context.ruby.jruby;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.ruby.jruby.pool.JrubyScriptEngineObjectPool;
import de.invesdwin.context.ruby.jruby.pool.WrappedJrubyScriptEngine;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;

@NotThreadSafe
public class JrubyScriptTaskEngineRuby implements IScriptTaskEngine {

    private WrappedJrubyScriptEngine javascriptScriptEngine;
    private final JrubyScriptTaskInputsRuby inputs;
    private final JrubyScriptTaskResultsRuby results;

    public JrubyScriptTaskEngineRuby(final WrappedJrubyScriptEngine javascriptScriptEngine) {
        this.javascriptScriptEngine = javascriptScriptEngine;
        this.inputs = new JrubyScriptTaskInputsRuby(this);
        this.results = new JrubyScriptTaskResultsRuby(this);
    }

    @Override
    public void eval(final String expression) {
        javascriptScriptEngine.eval(expression);
    }

    @Override
    public JrubyScriptTaskInputsRuby getInputs() {
        return inputs;
    }

    @Override
    public JrubyScriptTaskResultsRuby getResults() {
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

    public static JrubyScriptTaskEngineRuby newInstance() {
        return new JrubyScriptTaskEngineRuby(JrubyScriptEngineObjectPool.INSTANCE.borrowObject()) {
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
