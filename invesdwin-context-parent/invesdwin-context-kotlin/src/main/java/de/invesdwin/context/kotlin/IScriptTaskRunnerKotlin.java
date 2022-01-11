package de.invesdwin.context.kotlin;

import de.invesdwin.context.log.Log;

public interface IScriptTaskRunnerKotlin {

    Log LOG = new Log(IScriptTaskRunnerKotlin.class);

    <T> T run(AScriptTaskKotlin<T> scriptTask);

}
