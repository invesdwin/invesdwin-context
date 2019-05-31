package de.invesdwin.context.beans.init;

import javax.annotation.concurrent.Immutable;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;

import de.invesdwin.util.assertions.Assertions;

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

    public static boolean beanExists(final ListableBeanFactory factory, final Class<?> beanType) {
        final String[] beans = factory.getBeanNamesForType(beanType, false, false);
        return beans.length != 0;
    }

    public static void replaceBean(final ListableBeanFactory factory, final Class<?> bean, final Class<?> withBean) {
        Assertions.assertThat(bean.isAssignableFrom(withBean)).isTrue();
        deactivateBean(factory, bean);
        activateBean(factory, withBean);
    }

    public static void activateBean(final ListableBeanFactory factory, final Class<?> bean) {
        final BeanDefinition def = BeanDefinitionBuilder.genericBeanDefinition(bean).getBeanDefinition();
        final BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory;
        registry.registerBeanDefinition(bean.getName(), def);
    }

    public static void deactivateBean(final ListableBeanFactory factory, final Class<?> bean) {
        final BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory;
        final String[] matches = factory.getBeanNamesForType(bean, false, false);
        for (final String beanName : matches) {
            registry.removeBeanDefinition(beanName);
        }
    }

}
