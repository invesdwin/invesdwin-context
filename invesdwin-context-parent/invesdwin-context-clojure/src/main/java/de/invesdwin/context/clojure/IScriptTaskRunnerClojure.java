package de.invesdwin.context.clojure;

import de.invesdwin.context.log.Log;

public interface IScriptTaskRunnerClojure {

    Log LOG = new Log(IScriptTaskRunnerClojure.class);

    <T> T run(AScriptTaskClojure<T> scriptTask);

}
