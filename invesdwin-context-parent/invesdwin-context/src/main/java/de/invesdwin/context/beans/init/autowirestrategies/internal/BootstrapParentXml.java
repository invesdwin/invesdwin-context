package de.invesdwin.context.beans.init.autowirestrategies.internal;

import javax.annotation.concurrent.Immutable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.invesdwin.context.beans.init.MergedContext;
import de.invesdwin.context.beans.init.autowirestrategies.ParentContext;

@Immutable
public class BootstrapParentXml implements ApplicationContextAware {

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        MergedContext.autowire(new ParentContext(applicationContext));
    }

}
