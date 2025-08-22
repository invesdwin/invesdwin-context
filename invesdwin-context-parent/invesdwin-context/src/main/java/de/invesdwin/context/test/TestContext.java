package de.invesdwin.context.test;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.context.ConfigurableApplicationContext;

import de.invesdwin.context.beans.init.ADelegateContext;
import de.invesdwin.context.beans.init.ApplicationContexts;
import de.invesdwin.util.collections.attributes.EmptyAttributesMap;
import de.invesdwin.util.collections.attributes.IAttributesMap;

@ThreadSafe
public class TestContext extends ADelegateContext implements ITestContextState {

    private TestContextState state;
    private volatile boolean closeRequested;

    TestContext(final ConfigurableApplicationContext ctx, final TestContextState state) {
        super(ctx);
        this.state = state;
        //temporary pre merged context has null state
        if (state != null) {
            state.setContext(this);
        }
    }

    @Override
    public synchronized void close() {
        if (TestContextState.isFinishedGlobal()) {
            if (delegate != null) {
                closeAndEvict();
            }
        } else {
            closeRequested = true;
        }
    }

    public boolean isCloseRequested() {
        return closeRequested;
    }

    TestContextState getState() {
        return state;
    }

    synchronized void closeAndEvict() {
        super.close();
        delegate = null;
        state = null;
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

    @Override
    public boolean isFinished() {
        if (isPreMergedContext()) {
            return false;
        } else {
            return state.isFinished();
        }
    }

    @Override
    public boolean isFinishedGlobal() {
        return TestContextState.isFinishedGlobal();
    }

    public IAttributesMap getAttributes() {
        if (isPreMergedContext()) {
            return EmptyAttributesMap.INSTANCE;
        } else {
            return state.getAttributes();
        }
    }

}
