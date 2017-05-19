package de.invesdwin;

import javax.annotation.concurrent.Immutable;
import javax.inject.Named;

import de.invesdwin.context.beans.init.locations.IBasePackageDefinition;

@Named
@Immutable
public class OtherProjectBasePackageDefinition implements IBasePackageDefinition {

    @Override
    public String getBasePackage() {
        return "com.otherproject";
    }

}
