package de.invesdwin.context.beans.init.autowirestrategies.processor;

import javax.annotation.concurrent.Immutable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import de.invesdwin.context.beans.init.ApplicationContexts;

@Immutable
public class ActivateBeanProcessor implements BeanFactoryPostProcessor {

    private final Class<?> bean;

    public ActivateBeanProcessor(final Class<?> bean) {
        this.bean = bean;
    }

    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ApplicationContexts.activateBean(beanFactory, bean);
    }

}
