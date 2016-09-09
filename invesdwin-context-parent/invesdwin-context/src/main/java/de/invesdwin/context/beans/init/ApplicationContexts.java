package de.invesdwin.context.beans.init;

import javax.annotation.concurrent.Immutable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

@Immutable
public final class ApplicationContexts {

    private ApplicationContexts() {}

    public static ApplicationContext getRootContext(final ApplicationContext ctx) {
        ApplicationContext parent = ctx;
        while (true) {
            final ApplicationContext newParent = parent.getParent();
            if (newParent == null) {
                return parent;
            } else {
                parent = newParent;
            }
        }
    }

    public static boolean beanExists(final GenericApplicationContext ctx, final Class<?> beanType) {
        final String[] beans = ctx.getBeanFactory().getBeanNamesForType(beanType, false, false);
        return beans.length != 0;
    }

}
