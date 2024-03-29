package de.invesdwin.context.test;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.support.GenericXmlContextLoader;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.beans.hook.PreStartupHookManager;
import de.invesdwin.context.beans.hook.ReinitializationHookManager;
import de.invesdwin.context.beans.init.MergedContext;
import de.invesdwin.context.beans.init.PreMergedContext;
import de.invesdwin.context.beans.init.locations.PositionedResource;
import de.invesdwin.context.beans.init.platform.util.ComponentScanConfigurer;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.test.stub.IStub;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.lang.string.Strings;

@NotThreadSafe
public class TestContextLoader implements ContextLoader {

    public static final String CTX_DUMMY = "CTX_DUMMY";
    private static final AtomicBoolean FIRST_INITIALIZATION = new AtomicBoolean(true);
    private static final AtomicBoolean PRESTARTUP_HOOKS_STARTED = new AtomicBoolean(false);

    private static volatile ATest currentTest;

    static {
        Assertions.checkNotNull(PreMergedContext.getInstance());
    }

    private static final GenericXmlContextLoader PARENT = new GenericXmlContextLoader() {
        @Override
        protected void customizeContext(final GenericApplicationContext ctx) {
            try {
                configureContext(new TestContext(ctx));
            } catch (final Exception e) {
                throw Err.process(e);
            }
        };
    };

    static void setCurrentTest(final ATest currentTest) {
        TestContextLoader.currentTest = currentTest;
    }

    static ATest getCurrentTest() {
        return currentTest;
    }

    protected List<PositionedResource> configureContextLocations() throws Exception {
        ReinitializationHookManager.reinitializationStarted();
        final TestContext premergedContext = new TestContext(PreMergedContext.getInstance(true));
        final List<PositionedResource> preMergedContexts = PreMergedContext.collectMergedContexts();
        currentTest.setUpContextLocations(preMergedContexts);
        for (final IStub testHook : getTestHooks(PreMergedContext.getInstance())) {
            testHook.setUpContextLocations(currentTest, preMergedContexts);
        }
        configureContext(premergedContext);
        final List<PositionedResource> mergedContexts = PreMergedContext.collectMergedContexts();
        currentTest.setUpContextLocations(mergedContexts);
        for (final IStub testHook : getTestHooks(PreMergedContext.getInstance())) {
            testHook.setUpContextLocations(currentTest, mergedContexts);
        }
        PositionedResource.COMPARATOR.asAscending().asNotNullSafe().sort(mergedContexts);
        return mergedContexts;
    }

    protected static void configureContext(final TestContext ctx) throws Exception {
        MergedContext.autowire(ctx);
        currentTest.setUpContext(ctx);
        for (final IStub testHook : getTestHooks(ctx)) {
            testHook.setUpContext(currentTest, ctx);
        }
    }

    /**
     * Load instances manually so that they dont get injection and thus dont get their dependencies initilized too early
     * when they cannot be resolved yet
     */
    private static List<IStub> getTestHooks(final ConfigurableApplicationContext ctx) {
        final String[] testHookNames = ctx.getBeanFactory().getBeanNamesForType(IStub.class);
        final List<IStub> testHooks = new ArrayList<IStub>();
        for (final String testHookName : testHookNames) {
            final BeanDefinition testHookBeanDef = ctx.getBeanFactory().getBeanDefinition(testHookName);
            final Class<IStub> testHookClass = Reflections.classForName(testHookBeanDef.getBeanClassName());
            final IStub testHook = Reflections.constructor().in(testHookClass).newInstance();
            testHooks.add(testHook);
        }
        return testHooks;
    }

    @Override
    public ApplicationContext loadContext(final String... locations) throws Exception {
        try {
            if (!PRESTARTUP_HOOKS_STARTED.getAndSet(true)) {
                PreMergedContext.getInstance().getBean(PreStartupHookManager.class).start();
            }

            final List<PositionedResource> newLocations = new ArrayList<PositionedResource>();
            for (final String location : removeLocationsUUID(locations)) {
                if (!location.trim().endsWith(CTX_DUMMY)) {
                    //"classpath:" can be added when contexts get specified in @ContextConfiguration
                    newLocations.add(0,
                            PositionedResource.of(new ClassPathResource(location.replace("classpath:", "")), null));
                }
            }
            newLocations.addAll(configureContextLocations());

            for (final Entry<String, Resource> e : new ComponentScanConfigurer().getApplicationContextXmlConfigs(false)
                    .entrySet()) {
                final File xmlFile = new File(ContextProperties.TEMP_DIRECTORY,
                        "ctx.component.scan_" + e.getKey() + ".xml");
                final InputStream in = e.getValue().getInputStream();
                Files.write(xmlFile, IOUtils.toString(in, Charset.defaultCharset()), Charset.defaultCharset());
                in.close();
                final FileSystemResource fsResource = new FileSystemResource(xmlFile);
                newLocations.add(PositionedResource.of(fsResource, null));
                xmlFile.deleteOnExit();
            }

            MergedContext.logContextsBeingLoaded(newLocations);
            final List<String> locationStrings = new ArrayList<String>();
            for (final Resource resource : newLocations) {
                final String resourceString = resource.getURI().toString();
                locationStrings.add(resourceString);
            }
            final ConfigurableApplicationContext delegate = PARENT
                    .loadContext(locationStrings.toArray(Strings.EMPTY_ARRAY));
            final TestContext ctx = new TestContext(delegate);
            if (FIRST_INITIALIZATION.getAndSet(false)) {
                MergedContext.logBootstrapFinished();
            }
            ReinitializationHookManager.reinitializationFinished();
            return ctx;
        } catch (final Throwable t) {
            try {
                //need to clean up the mess, but cannot use currentTest since hooks are not initialized there
                for (final IStub hook : getTestHooks(PreMergedContext.getInstance())) {
                    hook.tearDownOnce(currentTest);
                }
                ReinitializationHookManager.reinitializationFailed();
            } catch (final Throwable tInner) {
                Err.process(tInner);
            }
            throw t;
        }
    }

    /**
     * Removes invalid and marker locations.
     */
    private String[] removeLocationsUUID(final String... rawLocations) {
        final List<String> locationsList = new ArrayList<String>();
        for (final String rawLocation : rawLocations) {
            if (!rawLocation.startsWith(CTX_DUMMY)) {
                locationsList.add(rawLocation);
            }
        }
        return locationsList.toArray(Strings.EMPTY_ARRAY);
    }

    /**
     * Forces loadContext to be called, by having a unique locations list every time.
     */
    @Override
    public String[] processLocations(final Class<?> clazz, final String... locations) {
        final List<String> list = new ArrayList<String>(Arrays.asList(locations));
        list.add(0, CTX_DUMMY + UUID.randomUUID().toString());
        return list.toArray(Strings.EMPTY_ARRAY);
    }
}
