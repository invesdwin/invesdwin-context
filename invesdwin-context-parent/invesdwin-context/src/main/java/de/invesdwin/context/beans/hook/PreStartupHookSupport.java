package de.invesdwin.context.beans.hook;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

@Immutable
@Named
public class PreStartupHookSupport implements IPreStartupHook {

    @Override
    public void preStartup() throws Exception {}

}
