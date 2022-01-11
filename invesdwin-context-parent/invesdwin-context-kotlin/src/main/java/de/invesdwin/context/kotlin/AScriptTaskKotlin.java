package de.invesdwin.context.kotlin;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.AScriptTask;

@NotThreadSafe
public abstract class AScriptTaskKotlin<V> extends AScriptTask<V, IScriptTaskRunnerKotlin> {

    @Override
    public V run(final IScriptTaskRunnerKotlin runner) {
        return runner.run(this);
    }

    @Override
    public V run() {
        return run(ScriptTaskRunnerKotlin.INSTANCE);
    }

}
