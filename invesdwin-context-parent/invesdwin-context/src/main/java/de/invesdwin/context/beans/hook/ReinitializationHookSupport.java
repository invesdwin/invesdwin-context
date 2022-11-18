package de.invesdwin.context.beans.hook;

import javax.annotation.concurrent.Immutable;
import jakarta.inject.Named;

@Immutable
@Named
public class ReinitializationHookSupport implements IReinitializationHook {

    @Override
    public void reinitializationStarted() {}

    @Override
    public void reinitializationFinished() {}

    @Override
    public void reinitializationFailed() {}

}
