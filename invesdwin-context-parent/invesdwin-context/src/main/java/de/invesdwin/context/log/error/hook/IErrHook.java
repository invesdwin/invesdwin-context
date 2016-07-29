package de.invesdwin.context.log.error.hook;

import de.invesdwin.context.log.error.LoggedRuntimeException;

public interface IErrHook {

    void loggedException(LoggedRuntimeException e, boolean uncaughtException);

}
