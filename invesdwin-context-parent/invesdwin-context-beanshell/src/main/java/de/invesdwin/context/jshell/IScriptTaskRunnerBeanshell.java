package de.invesdwin.context.jshell;

import de.invesdwin.context.log.Log;

public interface IScriptTaskRunnerBeanshell {

    Log LOG = new Log(IScriptTaskRunnerBeanshell.class);

    <T> T run(AScriptTaskBeanshell<T> scriptTask);

}
