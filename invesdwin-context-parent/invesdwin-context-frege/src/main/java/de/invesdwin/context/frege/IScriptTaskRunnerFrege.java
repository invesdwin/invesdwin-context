package de.invesdwin.context.frege;

import de.invesdwin.context.log.Log;

public interface IScriptTaskRunnerFrege {

    Log LOG = new Log(IScriptTaskRunnerFrege.class);

    <T> T run(AScriptTaskFrege<T> scriptTask);

}
