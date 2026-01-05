package de.invesdwin.context.test;

import java.util.Set;

public interface ITestContextSetup extends ITestContextState {

    boolean beanExists(Class<?> beanType);

    void replaceBean(Class<?> bean, Class<?> withBean);

    void activateBean(Class<?> bean);

    void deactivateBean(Class<?> bean);

    boolean isPreMergedContext();

    Set<String> getContextModifications();

}
