package de.invesdwin.context.beans.init;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.Resource;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.context.beans.hook.StartupHookManager;
import de.invesdwin.context.beans.init.locations.IContextLocation;
import de.invesdwin.context.beans.init.locations.PositionedResource;
import de.invesdwin.context.beans.init.platform.IPlatformInitializer;
import de.invesdwin.context.beans.init.platform.util.ComponentScanConfigurer;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.assertions.Assertions;

/**
 * This should only be used by infrastructure classes.
 */
@ThreadSafe
public final class PreMergedContext extends ADelegateContext {

    @GuardedBy("PreMergedContext.class")
    private static PreMergedContext instance;

    static {
        if (PlatformInitializerProperties.isAllowed()) {
            try {
                //jigsaw needs to be disabled before instrumentation is loaded because spring will otherwise throw exceptions
                PlatformInitializerProperties.getInitializer().initDisableJavaModuleSystemRestrictions();
                PlatformInitializerProperties.getInitializer().initInstrumentation();
                Assertions.assertThat(ContextProperties.TEMP_CLASSPATH_DIRECTORY).isNotNull();
                Assertions.assertThat(StartupHookManager.isAlreadyStarted()).isFalse();
                //reload initializer after instrumentation was done, since a hook might have changed the initializer
                final IPlatformInitializer initializer = PlatformInitializerProperties.getInitializer();
                //needs to happen after properties have been loaded
                if (!ContextProperties.KEEP_JDK_DEEP_CLONE_PROVIDER) {
                    initializer.initFstDeepCloneProvider();
                }
                initializer.initAgronaBoundsChecks();
                initializer.initNettyBoundsChecks();
                initializer.initClassPathScanner();
                initializer.registerTypesForSerialization();
                Assertions.assertThat(Err.UNCAUGHT_EXCEPTION_HANDLER).isNotNull();
                initializer.initProtocolRegistration();
                initializer.initDefaultTimezoneConfigurer();
                /*
                 * this must happen after properties have been loaded so that an overwritten property gets detected
                 */
                initializer.initFileEncodingChecker();
            } catch (final Throwable t) {
                PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
            }
        }
    }

    private PreMergedContext(final GenericApplicationContext delegate) {
        super(delegate);
    }

    @Override
    public GenericXmlApplicationContext getDelegate() {
        return (GenericXmlApplicationContext) delegate;
    }

    public static PreMergedContext getInstance() {
        return getInstance(false);
    }

    public static synchronized PreMergedContext getInstance(final boolean reset) {
        if ((instance == null || reset)) {
            PlatformInitializerProperties.assertInitializationNotSkipped();
            final GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
            ctx.registerShutdownHook();
            final PreMergedContext newInstance = new PreMergedContext(ctx);
            instance = newInstance;
            for (final Entry<String, Resource> e : new ComponentScanConfigurer().getApplicationContextXmlConfigs(true)
                    .entrySet()) {
                instance.getDelegate().load(e.getValue());
            }
            disableConfigurationAnnotationProcessing(ctx);
            instance.refresh();
        }
        return instance;
    }

    /**
     * This should only be used by infrastructure classes.
     */
    public static synchronized List<PositionedResource> collectMergedContexts() {
        /*
         * First only collect the context files, don't allow eager init or else the result will be cached and unit tests
         * will fail to register new beans during configuration phase
         */
        final Map<String, IContextLocation> mergers = getInstance().getBeansOfType(IContextLocation.class, true, false);
        final List<PositionedResource> contexts = new ArrayList<PositionedResource>();
        for (final IContextLocation merger : mergers.values()) {
            final List<PositionedResource> contextResources = merger.getContextResources();
            if (contextResources != null) {
                for (final PositionedResource contextResource : contextResources) {
                    if (contextResource != null) {
                        contexts.add(contextResource);
                    }
                }
            }
        }
        PositionedResource.COMPARATOR.asAscending().asNotNullSafe().sort(contexts);
        return contexts;
    }

    /**
     * Deactivates the loading of @Configuration classes.
     */
    private static void disableConfigurationAnnotationProcessing(final ConfigurableApplicationContext ctx) {
        Assertions.assertThat(ctx).isInstanceOf(BeanDefinitionRegistry.class);
        final String[] matches = ctx.getBeanFactory()
                .getBeanNamesForType(ConfigurationClassPostProcessor.class, false, false);
        final BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) ctx;
        for (final String beanName : matches) {
            beanDefinitionRegistry.removeBeanDefinition(beanName);
        }
    }

}
