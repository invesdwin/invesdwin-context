package de.invesdwin.context.integration.script.callback;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
public final class ObjectScriptTaskReturnsPool extends AAgronaObjectPool<ObjectScriptTaskReturns> {

    public static final ObjectScriptTaskReturnsPool INSTANCE = new ObjectScriptTaskReturnsPool();

    private ObjectScriptTaskReturnsPool() {}

    @Override
    protected ObjectScriptTaskReturns newObject() {
        return new ObjectScriptTaskReturns();
    }

    @Override
    protected boolean passivateObject(final ObjectScriptTaskReturns element) {
        element.close();
        return true;
    }

}
