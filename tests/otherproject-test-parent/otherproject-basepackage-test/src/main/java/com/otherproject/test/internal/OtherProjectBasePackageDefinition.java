package com.otherproject.test.internal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.IBasePackageDefinition;

@Immutable
public class OtherProjectBasePackageDefinition implements IBasePackageDefinition {

    @Override
    public String getBasePackage() {
        return "com.otherproject";
    }

}
