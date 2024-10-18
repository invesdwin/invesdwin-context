package de.invesdwin.context.ruby.truffleruby;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.IScriptTaskEngine;
import de.invesdwin.context.ruby.truffleruby.pool.TrufflerubyScriptEngineObjectPool;
import de.invesdwin.context.ruby.truffleruby.pool.WrappedTrufflerubyScriptEngine;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.concurrent.lock.disabled.DisabledLock;

@NotThreadSafe
public class TrufflerubyScriptTaskEngineRuby implements IScriptTaskEngine {

    private WrappedTrufflerubyScriptEngine javascriptScriptEngine;
    private final TrufflerubyScriptTaskInputsRuby inputs;
    private final TrufflerubyScriptTaskResultsRuby results;

    public TrufflerubyScriptTaskEngineRuby(final WrappedTrufflerubyScriptEngine javascriptScriptEngine) {
        this.javascriptScriptEngine = javascriptScriptEngine;
        this.inputs = new TrufflerubyScriptTaskInputsRuby(this);
        this.results = new TrufflerubyScriptTaskResultsRuby(this);
    }

    @Override
    public void eval(final String expression) {
        javascriptScriptEngine.eval(expression);
    }

    @Override
    public TrufflerubyScriptTaskInputsRuby getInputs() {
        return inputs;
    }

    @Override
    public TrufflerubyScriptTaskResultsRuby getResults() {
        return results;
    }

    @Override
    public void close() {
        javascriptScriptEngine = null;
    }

    @Override
    public WrappedTrufflerubyScriptEngine unwrap() {
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

    public static TrufflerubyScriptTaskEngineRuby newInstance() {
        return new TrufflerubyScriptTaskEngineRuby(TrufflerubyScriptEngineObjectPool.INSTANCE.borrowObject()) {
            @Override
            public void close() {
                final WrappedTrufflerubyScriptEngine unwrap = unwrap();
                if (unwrap != null) {
                    TrufflerubyScriptEngineObjectPool.INSTANCE.returnObject(unwrap);
                }
                super.close();
            }
        };
    }

}
