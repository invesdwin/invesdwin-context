package de.invesdwin.context.beans.init.internal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.beans.hook.IBasePackageDefinitionHook;

@Immutable
public class InvesdwinBasePackageDefinitionHook implements IBasePackageDefinitionHook {

    @Override
    public String getBasePackage() {
        return "de.invesdwin";
    }
}
