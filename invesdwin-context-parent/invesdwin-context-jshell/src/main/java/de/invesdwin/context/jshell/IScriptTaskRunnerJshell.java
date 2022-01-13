package de.invesdwin.context.jshell;

import de.invesdwin.context.log.Log;

public interface IScriptTaskRunnerJshell {

    Log LOG = new Log(IScriptTaskRunnerJshell.class);

    <T> T run(AScriptTaskJshell<T> scriptTask);

}
