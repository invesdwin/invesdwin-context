package de.invesdwin.context.test;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.context.ConfigurableApplicationContext;

import de.invesdwin.context.beans.init.ADelegateContext;
import de.invesdwin.context.beans.init.ApplicationContexts;
import de.invesdwin.util.collections.attributes.EmptyAttributesMap;
import de.invesdwin.util.collections.attributes.IAttributesMap;

@ThreadSafe
public class TestContext extends ADelegateContext {

    private final TestContextState state;

    TestContext(final ConfigurableApplicationContext ctx, final TestContextState state) {
        super(ctx);
        this.state = state;
        //temporary pre merged context has null state
        if (state != null) {
            state.setContext(this);
        }
    }

    TestContextState getState() {
        return state;
    }

    public boolean beanExists(final Class<?> beanType) {
        return ApplicationContexts.beanExists(this, beanType);
    }

    public void replaceBean(final Class<?> bean, final Class<?> withBean) {
        ApplicationContexts.replaceBean(this, bean, withBean);
    }

    public void activateBean(final Class<?> bean) {
        ApplicationContexts.activateBean(this, bean);
    }

    public void deactivateBean(final Class<?> bean) {
        ApplicationContexts.deactivateBean(this, bean);
    }

    public boolean isPreMergedContext() {
        return state == null;
    }

    public boolean isFinished() {
        if (isPreMergedContext()) {
            return false;
        } else {
            return state.isFinished();
        }
    }

    public IAttributesMap getAttributes() {
        if (isPreMergedContext()) {
            return EmptyAttributesMap.INSTANCE;
        } else {
            return state.getAttributes();
        }
    }

}
