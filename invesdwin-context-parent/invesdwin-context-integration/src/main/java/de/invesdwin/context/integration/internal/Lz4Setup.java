package de.invesdwin.context.integration.internal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.beans.hook.IPreStartupHook;
import jakarta.inject.Named;
import net.jpountz.xxhash.XXHashFactory;

@Immutable
@Named
public class Lz4Setup implements IPreStartupHook {

    @Override
    public void preStartup() throws Exception {
        //prevent segmentation fault because the lib is loaded at the wrong time on higher cpu counts
        XXHashFactory.fastestInstance();
    }

}
