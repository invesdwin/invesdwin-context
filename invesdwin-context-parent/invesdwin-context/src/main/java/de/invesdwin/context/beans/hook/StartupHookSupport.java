package de.invesdwin.context.beans.hook;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

@Named
@Immutable
public class StartupHookSupport implements IStartupHook {

    @Override
    public void startup() throws Exception {}

}
