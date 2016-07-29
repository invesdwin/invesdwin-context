package de.invesdwin.context.beans.init.internal;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import de.invesdwin.context.beans.init.locations.IBasePackageDefinition;

@Named
@Immutable
public class InvesdwinBasePackage implements IBasePackageDefinition {

    @Override
    public String getBasePackage() {
        return "de.invesdwin";
    }
}
