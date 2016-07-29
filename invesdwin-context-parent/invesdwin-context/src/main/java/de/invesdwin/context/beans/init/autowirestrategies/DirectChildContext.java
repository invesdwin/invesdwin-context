package de.invesdwin.context.beans.init.autowirestrategies;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.context.ApplicationContext;

import de.invesdwin.context.beans.init.ADelegateContext;

@NotThreadSafe
public class DirectChildContext extends ADelegateContext {

    public DirectChildContext(final ApplicationContext ctx) {
        super(ctx);
    }

}
