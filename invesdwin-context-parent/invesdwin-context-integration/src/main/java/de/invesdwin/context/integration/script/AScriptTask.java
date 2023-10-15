package de.invesdwin.context.integration.script;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.script.callback.IScriptTaskCallback;

@NotThreadSafe
public abstract class AScriptTask<V, R> {

    public IScriptTaskCallback newCallback() {
        return null;
    }

    public abstract void populateInputs(IScriptTaskInputs inputs);

    public abstract void executeScript(IScriptTaskEngine engine);

    public abstract V extractResults(IScriptTaskResults results);

    public abstract V run(R runner);

    /**
     * Runs this script task with the provided/preferred/default runner or throws an exception when none can be
     * identified uniquely
     */
    public abstract V run();

}
