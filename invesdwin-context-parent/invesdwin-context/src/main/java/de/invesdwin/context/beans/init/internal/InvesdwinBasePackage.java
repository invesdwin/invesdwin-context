package de.invesdwin.context.beans.init.internal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.IBasePackageDefinition;

@Immutable
public class InvesdwinBasePackage implements IBasePackageDefinition {

    @Override
    public String getBasePackage() {
        return "de.invesdwin";
    }
}
