package de.invesdwin.context.log.error.hook;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import de.invesdwin.context.log.error.LoggedRuntimeException;

@Named
@Immutable
public class ErrHookSupport implements IErrHook {

    @Override
    public void loggedException(final LoggedRuntimeException e, final boolean uncaughtException) {
    }

}
