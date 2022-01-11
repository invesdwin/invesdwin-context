package de.invesdwin.context.javascript;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.AScriptTask;

@NotThreadSafe
public abstract class AScriptTaskJavascript<V> extends AScriptTask<V, IScriptTaskRunnerJavascript> {

    @Override
    public V run(final IScriptTaskRunnerJavascript runner) {
        return runner.run(this);
    }

    @Override
    public V run() {
        return run(ScriptTaskRunnerJavascript.INSTANCE);
    }

}
