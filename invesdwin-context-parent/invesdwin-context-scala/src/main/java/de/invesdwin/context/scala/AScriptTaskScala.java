package de.invesdwin.context.scala;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.AScriptTask;

@NotThreadSafe
public abstract class AScriptTaskScala<V> extends AScriptTask<V, IScriptTaskRunnerScala> {

    @Override
    public V run(final IScriptTaskRunnerScala runner) {
        return runner.run(this);
    }

    @Override
    public V run() {
        return run(ScriptTaskRunnerScala.INSTANCE);
    }

}
