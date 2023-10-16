package de.invesdwin.context.integration.script.callback;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.pool.AAgronaObjectPool;

@ThreadSafe
public final class ObjectScriptTaskParametersPool extends AAgronaObjectPool<ObjectScriptTaskParameters> {

    public static final ObjectScriptTaskParametersPool INSTANCE = new ObjectScriptTaskParametersPool();

    private ObjectScriptTaskParametersPool() {}

    @Override
    protected ObjectScriptTaskParameters newObject() {
        return new ObjectScriptTaskParameters();
    }

    @Override
    protected boolean passivateObject(final ObjectScriptTaskParameters element) {
        element.close();
        return true;
    }

}
