package de.invesdwin.context.beans.init;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.context.beans.hook.PreStartupHookManager;
import de.invesdwin.context.beans.hook.StartupHookManager;
import de.invesdwin.context.beans.init.autowirestrategies.DirectChildContext;
import de.invesdwin.context.beans.init.autowirestrategies.ParentContext;
import de.invesdwin.context.beans.init.autowirestrategies.processor.ActivateBeanProcessor;
import de.invesdwin.context.beans.init.autowirestrategies.processor.DeactivateBeanProcessor;
import de.invesdwin.context.beans.init.locations.IContextLocationValidator;
import de.invesdwin.context.beans.init.locations.PositionedResource;
import de.invesdwin.context.beans.init.platform.util.ComponentScanConfigurer;
import de.invesdwin.context.log.Log;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.streams.resource.Resources;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

/**
 * This should only be used by infrastructure classes.
 */
@ThreadSafe
public final class MergedContext extends ADelegateContext {

    private static final Log LOG = new Log(MergedContext.class);

    /*
     * Order must be preserved, thus LinkedHashSet.
     */
    @GuardedBy("MergedContext.class")
    private static final Set<ParentContext> TO_BE_SET_PARENTS = new LinkedHashSet<ParentContext>();
    /*
     * Order must be preserved, thus LinkedHashSet.
     */
    @GuardedBy("MergedContext.class")
    private static final Set<BeanFactoryPostProcessor> TO_BET_SET_BEAN_FACTORY_POST_PROCESSORS = new LinkedHashSet<BeanFactoryPostProcessor>();
    @GuardedBy("MergedContext.class")
    private static MergedContext instance;
    private static volatile boolean bootstrapRunning;
    private static volatile boolean bootstrapFinished;

    static {
        Assertions.assertThat(PreMergedContext.getInstance())
                .as("%s failied initialization!", PreMergedContext.class.getSimpleName())
                .isNotNull();
    }

    private MergedContext(final ConfigurableApplicationContext ctx) {
        super(ctx);
    }

    public static MergedContext getInstance() {
        return instance;
    }

    public static boolean isBootstrapRunning() {
        return bootstrapRunning;
    }

    public static boolean isBootstrapFinished() {
        return bootstrapFinished;
    }

    public static void deactivateBean(final Class<?> bean) {
        autowire(new DeactivateBeanProcessor(bean));
    }

    public static void activateBean(final Class<?> bean) {
        autowire(new ActivateBeanProcessor(bean));
    }

    /**
     * Should only be used by infrastructure classes.
     */
    public static synchronized void autowire(final Object target) {
        if (target instanceof BeanFactoryPostProcessor) {
            final BeanFactoryPostProcessor beanFactoryPostProcessor = (BeanFactoryPostProcessor) target;
            if (instance == null) {
                TO_BET_SET_BEAN_FACTORY_POST_PROCESSORS.add(beanFactoryPostProcessor);
            } else {
                autowireBeanFactoryPostProcessor(beanFactoryPostProcessor);
            }
        } else if (target instanceof ADelegateContext) {
            if (target instanceof ParentContext) {
                //For example for CXF
                final ParentContext parentContext = (ParentContext) target;
                if (instance == null) {
                    TO_BE_SET_PARENTS.add(parentContext);
                } else {
                    autowireParentContext(parentContext);
                }
            } else {
                //For example TestContext is being used directly instead of injecting something into it
                final ADelegateContext delegateCtx = (ADelegateContext) target;
                autowireReplacement(delegateCtx);
            }
        } else {
            if (instance == null) {
                //If started productive, bootstrap must be called
                bootstrap();
            }
            if (target instanceof ApplicationContext) {
                //If called with another Context make MergedContext available to it
                autowireChildContext((ApplicationContext) target);
            } else {
                //If called with a normal class, do dependency injection on it
                autowireBean(target);
            }
        }
    }

    private static void autowireBeanFactoryPostProcessor(final BeanFactoryPostProcessor beanFactoryPostProcessor) {
        instance.addBeanFactoryPostProcessor(beanFactoryPostProcessor);
    }

    private static void autowireReplacement(final ADelegateContext delegateCtx) {
        final MergedContext prevInstance = instance;
        instance = new MergedContext(delegateCtx);
        configureInstance();
        if (prevInstance != null) {
            //during tests the transaction manager cache inside spring needs to be reset
            try {
                final TransactionAspectSupport transactionAspectSupport = prevInstance
                        .getBean(TransactionAspectSupport.class);
                final ConcurrentMap<?, ?> transactionManagerCache = Reflections.field("transactionManagerCache")
                        .ofType(ConcurrentMap.class)
                        .in(transactionAspectSupport)
                        .get();
                transactionManagerCache.clear();
            } catch (final NoSuchBeanDefinitionException e) {
                //end reached
            }
            if (prevInstance instanceof ADelegateContext) {
                final ADelegateContext cPrevInstance = prevInstance;
                if (cPrevInstance.getDelegate() == PreMergedContext.getInstance().getDelegate()) {
                    //don't close premergedcontext
                    return;
                }
            }
            prevInstance.close();
        }
    }

    private static void autowireParentContext(final ParentContext target) {
        Assertions.assertThat(instance).as("Bootstrap must be finished before parents can be set!").isNotNull();
        final ConfigurableApplicationContext rootCtx = (ConfigurableApplicationContext) ApplicationContexts
                .getRootContext(instance);
        rootCtx.setParent(target);
    }

    private static void autowireChildContext(final ApplicationContext target) {
        Assertions.assertThat(target)
                .as("%s must be a %s, so that a parent context can be set on it!",
                        ApplicationContext.class.getSimpleName(), ConfigurableApplicationContext.class.getSimpleName())
                .isInstanceOf(ConfigurableApplicationContext.class);
        ConfigurableApplicationContext parent = (ConfigurableApplicationContext) target;
        while (parent != null) {
            Assertions.assertThat(parent)
                    .as("%s already has this context as a parent!", MergedContext.class.getSimpleName())
                    .isNotSameAs(instance.delegate);
            parent = (ConfigurableApplicationContext) parent.getParent();
        }
        final ConfigurableApplicationContext targetCtx;
        if (target instanceof DirectChildContext) {
            targetCtx = (ConfigurableApplicationContext) target;
        } else {
            targetCtx = (ConfigurableApplicationContext) ApplicationContexts.getRootContext(target);
        }
        targetCtx.setParent(instance.delegate);
    }

    private static void autowireBean(final Object target) {
        if (target == null) {
            return;
        }
        instance.getAutowireCapableBeanFactory()
                .autowireBeanProperties(target, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
    }

    public static void logBootstrapFinished() {
        instance.getBean(StartupHookManager.class).start();
        LOG.info("Bootstrap finished after: %s",
                new Instant(PlatformInitializerProperties.START_OF_APPLICATION_CPU_TIME_NANOS).toDuration());
        bootstrapFinished = true;
        bootstrapRunning = false;
    }

    public static void logContextsBeingLoaded(final List<PositionedResource> contexts) {
        bootstrapRunning = true;
        //validate locations
        final Map<String, IContextLocationValidator> validators = PreMergedContext.getInstance()
                .getBeansOfType(IContextLocationValidator.class);
        for (final IContextLocationValidator validator : validators.values()) {
            validator.validateContextLocations(contexts);
        }

        if (LOG.isInfoEnabled()) {
            final String config;
            if (ContextProperties.IS_TEST_ENVIRONMENT) {
                config = "TEST";
            } else {
                config = "PROD";
            }
            String contexteSingularPlural = "context";
            if (contexts.size() != 1) {
                contexteSingularPlural += "s";
            }
            final List<String> locationStrings = Resources.extractMetaInfResourceLocations(contexts);
            LOG.info("Loading " + locationStrings.size() + " spring " + contexteSingularPlural + " in " + config
                    + " config from classpath " + locationStrings);
        }
    }

    private static synchronized void bootstrap() {
        PlatformInitializerProperties.assertInitializationNotSkipped();
        Assertions.assertThat(instance).as("Bootstrap can only run once!").isNull();
        Assertions.assertThat(ContextProperties.getBasePackages())
                .as("Did not detect any base packages, thus cannot bootstrap!")
                .isNotEmpty();

        PreMergedContext.getInstance().getBean(PreStartupHookManager.class).start();

        //First work with temp context
        final List<PositionedResource> contexts = PreMergedContext.collectMergedContexts();
        //Now create the real context
        final GenericXmlApplicationContext delegate = new GenericXmlApplicationContext();
        //so that the reference is already set while beans get initialized
        delegate.registerShutdownHook();
        for (final Entry<String, Resource> e : new ComponentScanConfigurer().getApplicationContextXmlConfigs(false)
                .entrySet()) {
            contexts.add(PositionedResource.of(e.getValue(), null));
        }

        logContextsBeingLoaded(contexts);

        delegate.load(contexts.toArray(Resources.EMPTY_ARRAY));
        //set the reference
        instance = new MergedContext(delegate);

        configureInstance();

        //now load the beans
        instance.refresh();

        logBootstrapFinished();
    }

    private static void configureInstance() {
        for (final ParentContext parentCtx : TO_BE_SET_PARENTS) {
            autowireParentContext(parentCtx);
        }
        for (final BeanFactoryPostProcessor beanFactoryPostProcessor : TO_BET_SET_BEAN_FACTORY_POST_PROCESSORS) {
            autowireBeanFactoryPostProcessor(beanFactoryPostProcessor);
        }
    }

    public static void assertBootstrapFinished() {
        Assertions.assertThat(isBootstrapFinished())
                .as("This function is only available after invesdwin bootstrap has finished!")
                .isTrue();
    }

    public static void awaitBootstrapFinished() throws InterruptedException {
        while (!isBootstrapFinished()) {
            Duration.ONE_MILLISECOND.sleep();
        }
    }

    public static void awaitBootstrapFinishedIfRunning() throws InterruptedException {
        while (isBootstrapRunning()) {
            Duration.ONE_MILLISECOND.sleep();
        }
    }

}
