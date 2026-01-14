package de.invesdwin.context.test;

import java.util.Set;

public interface ITestContextSetup extends ITestContextState {

    boolean beanExists(Class<?> beanType);

    default void replaceBean(final Class<?> bean, final Class<?> withBean) {
        replaceBean(bean, withBean, true);
    }

    void replaceBean(Class<?> bean, Class<?> withBean, boolean record);

    default void activateBean(final Class<?> bean) {
        activateBean(bean, true);
    }

    void activateBean(Class<?> bean, boolean record);

    default void deactivateBean(final Class<?> bean) {
        deactivateBean(bean, true);
    }

    void deactivateBean(Class<?> bean, boolean record);

    boolean isPreMergedContext();

    Set<String> getContextModifications();

}
