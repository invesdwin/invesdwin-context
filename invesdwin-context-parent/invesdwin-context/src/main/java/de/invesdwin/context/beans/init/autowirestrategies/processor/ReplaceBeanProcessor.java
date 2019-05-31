package de.invesdwin.context.beans.init.autowirestrategies.processor;

import javax.annotation.concurrent.Immutable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import de.invesdwin.context.beans.init.ApplicationContexts;

@Immutable
public class ReplaceBeanProcessor implements BeanFactoryPostProcessor {

    private final Class<?> bean;
    private final Class<?> withBean;

    public ReplaceBeanProcessor(final Class<?> bean, final Class<?> withBean) {
        this.bean = bean;
        this.withBean = withBean;
    }

    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ApplicationContexts.replaceBean(beanFactory, bean, withBean);
    }

}
