package de.invesdwin.context.test;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class PretendingTestContextSetup implements ITestContextSetup {

    private final TestContext delegate;
    private final Set<String> contextModifications = new LinkedHashSet<>();

    public PretendingTestContextSetup(final TestContext delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isFinishedContext() {
        return delegate.isFinishedContext();
    }

    @Override
    public boolean isFinishedGlobal() {
        return delegate.isFinishedGlobal();
    }

    @Override
    public boolean beanExists(final Class<?> beanType) {
        return delegate.beanExists(beanType);
    }

    @Override
    public void replaceBean(final Class<?> bean, final Class<?> withBean, final boolean record) {
        if (record) {
            getContextModifications().add("replace[" + bean.getName() + "," + withBean.getName() + "]");
        }
    }

    @Override
    public void activateBean(final Class<?> bean, final boolean record) {
        if (record) {
            getContextModifications().add("activate[" + bean.getName() + "]");
        }
    }

    @Override
    public void deactivateBean(final Class<?> bean, final boolean record) {
        if (record) {
            getContextModifications().add("deactivate[" + bean.getName() + "]");
        }
    }

    @Override
    public boolean isPreMergedContext() {
        return true;
    }

    @Override
    public Set<String> getContextModifications() {
        return contextModifications;
    }

}
