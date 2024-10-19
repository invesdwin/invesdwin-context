package de.invesdwin.context.ruby.truffleruby.callback;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
public final class TrufflerubyScriptTaskReturnsPool extends AAgronaObjectPool<TrufflerubyScriptTaskReturns> {

    public static final TrufflerubyScriptTaskReturnsPool INSTANCE = new TrufflerubyScriptTaskReturnsPool();

    private TrufflerubyScriptTaskReturnsPool() {}

    @Override
    protected TrufflerubyScriptTaskReturns newObject() {
        return new TrufflerubyScriptTaskReturns();
    }

    @Override
    protected boolean passivateObject(final TrufflerubyScriptTaskReturns element) {
        element.close();
        return true;
    }

}
