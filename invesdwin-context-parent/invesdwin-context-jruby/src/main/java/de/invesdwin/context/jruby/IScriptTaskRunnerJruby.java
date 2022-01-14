package de.invesdwin.context.jruby;

import de.invesdwin.context.log.Log;

public interface IScriptTaskRunnerJruby {

    Log LOG = new Log(IScriptTaskRunnerJruby.class);

    <T> T run(AScriptTaskJruby<T> scriptTask);

}
