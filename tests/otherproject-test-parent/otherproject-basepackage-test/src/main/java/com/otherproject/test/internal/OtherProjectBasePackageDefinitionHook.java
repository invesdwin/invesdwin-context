package com.otherproject.test.internal;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.beans.hook.IBasePackageDefinitionHook;

@Immutable
public class OtherProjectBasePackageDefinitionHook implements IBasePackageDefinitionHook {

    @Override
    public String getBasePackage() {
        return "com.otherproject";
    }

}
