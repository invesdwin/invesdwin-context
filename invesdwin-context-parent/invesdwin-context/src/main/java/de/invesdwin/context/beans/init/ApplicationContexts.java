package de.invesdwin.context.beans.init;

import javax.annotation.concurrent.Immutable;

import org.springframework.context.ApplicationContext;

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

}
