package de.invesdwin.context.beans.init;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.test.stub.IStub;

@ThreadSafe
public abstract class ADelegateContext implements ConfigurableApplicationContext, BeanDefinitionRegistry {

    protected GenericApplicationContext delegate;

    public ADelegateContext(final ApplicationContext delegate) {
        if (delegate instanceof ADelegateContext) {
            final ADelegateContext aDelegate = (ADelegateContext) delegate;
            this.delegate = aDelegate.delegate;
        } else if (delegate instanceof GenericApplicationContext) {
            this.delegate = (GenericApplicationContext) delegate;
        } else {
            final GenericApplicationContext wrapperCtx = new GenericApplicationContext();
            wrapperCtx.setParent(delegate);
            this.delegate = wrapperCtx;
            refresh();
        }
    }

    public ADelegateContext(final GenericApplicationContext delegate) {
        this.delegate = delegate;
    }

    public ADelegateContext(final ADelegateContext ctx) {
        this.delegate = ctx.delegate;
    }

    public GenericApplicationContext getDelegate() {
        return delegate;
    }

    public void registerBean(final String beanName, final Object bean) {
        final BeanDefinition def = BeanDefinitionBuilder.genericBeanDefinition(bean.getClass()).getBeanDefinition();
        registerBeanDefinition(beanName, def);
        getBeanFactory().registerSingleton(beanName, bean);
    }

    @Override
    public void addApplicationListener(final ApplicationListener<?> listener) {
        delegate.addApplicationListener(listener);
    }

    @Override
    public String[] getBeanNamesForType(final Class<?> type) {
        return delegate.getBeanNamesForType(type);
    }

    @Override
    public String[] getBeanNamesForType(final Class<?> type, final boolean includeNonSingletons,
            final boolean allowEagerInit) {
        //prevent exception about bean factory not having been refreshed yet by going directly against the bean factory
        return delegate.getBeanFactory().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public boolean isTypeMatch(final String name, final Class<?> targetType) {
        return delegate.isTypeMatch(name, targetType);
    }

    @Override
    public Resource getResource(final String location) {
        return delegate.getResource(location);
    }

    @Override
    public ClassLoader getClassLoader() {
        return delegate.getClassLoader();
    }

    @Override
    public Resource[] getResources(final String locationPattern) throws IOException {
        return delegate.getResources(locationPattern);
    }

    @Override
    public Object getBean(final String name) {
        return delegate.getBean(name);
    }

    @Override
    public <T> T getBean(final String name, final Class<T> requiredType) {
        return delegate.getBean(name, requiredType);
    }

    @Override
    public <T> T getBean(final Class<T> requiredType) {
        return delegate.getBean(requiredType);
    }

    @Override
    public Object getBean(final String name, final Object... args) {
        return delegate.getBean(name, args);
    }

    @Override
    public boolean containsBean(final String name) {
        return delegate.containsBean(name);
    }

    @Override
    public boolean isSingleton(final String name) {
        return delegate.isSingleton(name);
    }

    @Override
    public boolean isPrototype(final String name) {
        return delegate.isPrototype(name);
    }

    @Override
    public Class<?> getType(final String name) {
        return delegate.getType(name);
    }

    @Override
    public String[] getAliases(final String name) {
        return delegate.getAliases(name);
    }

    @Override
    public boolean containsBeanDefinition(final String beanName) {
        return delegate.containsBeanDefinition(beanName);
    }

    @Override
    public int getBeanDefinitionCount() {
        return delegate.getBeanDefinitionCount();
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return delegate.getBeanDefinitionNames();
    }

    @Override
    public <T> Map<String, T> getBeansOfType(final Class<T> type) {
        return delegate.getBeansOfType(type);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(final Class<T> type, final boolean includeNonSingletons,
            final boolean allowEagerInit) {
        return delegate.getBeansOfType(type, includeNonSingletons, allowEagerInit);
    }

    @Override
    public Map<String, Object> getBeansWithAnnotation(final Class<? extends Annotation> annotationType) {
        return delegate.getBeansWithAnnotation(annotationType);
    }

    @Override
    public <A extends Annotation> A findAnnotationOnBean(final String beanName, final Class<A> annotationType) {
        return delegate.findAnnotationOnBean(beanName, annotationType);
    }

    @Override
    public BeanFactory getParentBeanFactory() {
        return delegate.getParentBeanFactory();
    }

    @Override
    public boolean containsLocalBean(final String name) {
        return delegate.containsLocalBean(name);
    }

    @Override
    public String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale) {
        return delegate.getMessage(code, args, locale);
    }

    @Override
    public String getMessage(final String code, final Object[] args, final Locale locale) {
        return delegate.getMessage(code, args, locale);
    }

    @Override
    public String getMessage(final MessageSourceResolvable resolvable, final Locale locale) {
        return delegate.getMessage(resolvable, locale);
    }

    @Override
    public void publishEvent(final ApplicationEvent event) {
        delegate.publishEvent(event);
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public String getDisplayName() {
        return delegate.getDisplayName();
    }

    @Override
    public long getStartupDate() {
        return delegate.getStartupDate();
    }

    @Override
    public ApplicationContext getParent() {
        return delegate.getParent();
    }

    @Override
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() {
        return delegate.getAutowireCapableBeanFactory();
    }

    @Override
    public void registerAlias(final String name, final String alias) {
        delegate.registerAlias(name, alias);
    }

    @Override
    public void removeAlias(final String alias) {
        delegate.removeAlias(alias);
    }

    @Override
    public boolean isAlias(final String beanName) {
        return delegate.isAlias(beanName);
    }

    @Override
    public void registerBeanDefinition(final String beanName, final BeanDefinition beanDefinition) {
        delegate.registerBeanDefinition(beanName, beanDefinition);
    }

    @Override
    public void removeBeanDefinition(final String beanName) {
        delegate.removeBeanDefinition(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(final String beanName) {
        return delegate.getBeanDefinition(beanName);
    }

    @Override
    public boolean isBeanNameInUse(final String beanName) {
        return delegate.isBeanNameInUse(beanName);
    }

    @Override
    public void start() {
        delegate.start();
    }

    @Override
    public void stop() {
        delegate.stop();
    }

    @Override
    public boolean isRunning() {
        return delegate.isRunning();
    }

    @Override
    public void setId(final String id) {
        delegate.setId(id);
    }

    @Override
    public void setParent(final ApplicationContext parent) {
        delegate.setParent(parent);
    }

    @Override
    public void addBeanFactoryPostProcessor(final BeanFactoryPostProcessor beanFactoryPostProcessor) {
        delegate.addBeanFactoryPostProcessor(beanFactoryPostProcessor);
    }

    @Override
    public void refresh() {
        if (!ContextProperties.IS_TEST_ENVIRONMENT) {
            //disable mock beans if this is in the production environment
            ApplicationContexts.deactivateBean(this, IStub.class);
        }
        delegate.refresh();
    }

    @Override
    public void registerShutdownHook() {
        delegate.registerShutdownHook();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public boolean isActive() {
        return delegate.isActive();
    }

    @Override
    public ConfigurableListableBeanFactory getBeanFactory() {
        return delegate.getBeanFactory();
    }

    @Override
    public ConfigurableEnvironment getEnvironment() {
        return delegate.getEnvironment();
    }

    @Override
    public void setEnvironment(final ConfigurableEnvironment environment) {
        delegate.setEnvironment(environment);
    }

    @Override
    public String getApplicationName() {
        return delegate.getApplicationName();
    }

    @Override
    public String[] getBeanNamesForAnnotation(final Class<? extends Annotation> annotationType) {
        return delegate.getBeanNamesForAnnotation(annotationType);
    }

    @Override
    public <T> T getBean(final Class<T> requiredType, final Object... args) {
        return delegate.getBean(requiredType, args);
    }

    @Override
    public String[] getBeanNamesForType(final ResolvableType type) {
        return delegate.getBeanNamesForType(type);
    }

    @Override
    public boolean isTypeMatch(final String name, final ResolvableType typeToMatch)
            throws NoSuchBeanDefinitionException {
        return delegate.isTypeMatch(name, typeToMatch);
    }

    @Override
    public void publishEvent(final Object event) {
        delegate.publishEvent(event);
    }

    @Override
    public void addProtocolResolver(final ProtocolResolver resolver) {
        delegate.addProtocolResolver(resolver);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(final Class<T> requiredType) {
        return delegate.getBeanProvider(requiredType);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(final ResolvableType requiredType) {
        return delegate.getBeanProvider(requiredType);
    }

}
