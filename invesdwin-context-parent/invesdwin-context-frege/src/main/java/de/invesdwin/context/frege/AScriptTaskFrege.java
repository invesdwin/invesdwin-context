package de.invesdwin.context.frege;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.AScriptTask;

@NotThreadSafe
public abstract class AScriptTaskFrege<V> extends AScriptTask<V, IScriptTaskRunnerFrege> {

    @Override
    public V run(final IScriptTaskRunnerFrege runner) {
        return runner.run(this);
    }

    @Override
    public V run() {
        return run(ScriptTaskRunnerFrege.INSTANCE);
    }

}
