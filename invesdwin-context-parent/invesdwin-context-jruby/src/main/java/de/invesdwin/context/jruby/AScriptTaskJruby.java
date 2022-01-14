package de.invesdwin.context.jruby;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.AScriptTask;

@NotThreadSafe
public abstract class AScriptTaskJruby<V> extends AScriptTask<V, IScriptTaskRunnerJruby> {

    @Override
    public V run(final IScriptTaskRunnerJruby runner) {
        return runner.run(this);
    }

    @Override
    public V run() {
        return run(ScriptTaskRunnerJruby.INSTANCE);
    }

}
