package de.invesdwin.context.test;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import de.invesdwin.context.beans.init.ADelegateContext;
import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public class TestContext extends ADelegateContext {

    TestContext(final ConfigurableApplicationContext ctx) {
        super(ctx);
    }

    public void replace(final Class<?> bean, final Class<?> withBean) {
        Assertions.assertThat(bean.isAssignableFrom(withBean)).isTrue();
        deactivate(bean);
        activate(withBean);
    }

    public void activate(final Class<?> bean) {
        final BeanDefinition def = BeanDefinitionBuilder.genericBeanDefinition(bean).getBeanDefinition();
        delegate.registerBeanDefinition(bean.getName(), def);
    }

    public void deactivate(final Class<?> bean) {
        removeBeanDefinitionsOfType(bean);
    }

}
