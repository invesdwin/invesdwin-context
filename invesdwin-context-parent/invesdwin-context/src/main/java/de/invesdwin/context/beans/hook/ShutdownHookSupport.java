package de.invesdwin.context.beans.hook;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import de.invesdwin.util.shutdown.IShutdownHook;

@Immutable
@Named
public class ShutdownHookSupport implements IShutdownHook {

    @Override
    public void shutdown() throws Exception {}

}
