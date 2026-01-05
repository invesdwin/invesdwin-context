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
    public boolean isFinished() {
        return delegate.isFinished();
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
    public void replaceBean(final Class<?> bean, final Class<?> withBean) {
        getContextModifications().add("replace[" + bean.getName() + "," + withBean.getName() + "]");
    }

    @Override
    public void activateBean(final Class<?> bean) {
        getContextModifications().add("activate[" + bean.getName() + "]");
    }

    @Override
    public void deactivateBean(final Class<?> bean) {
        getContextModifications().add("deactivate[" + bean.getName() + "]");
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
