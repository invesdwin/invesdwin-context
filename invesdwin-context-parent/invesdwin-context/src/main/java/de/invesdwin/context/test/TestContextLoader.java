package de.invesdwin.context.test;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

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
import de.invesdwin.context.log.error.LoggedRuntimeException;
import de.invesdwin.context.test.stub.IStub;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.lang.string.Strings;
import io.netty.util.concurrent.FastThreadLocal;

@ThreadSafe
public class TestContextLoader implements ContextLoader {

    public static final String CTX_DUMMY = "CTX_DUMMY";
    private static final AtomicBoolean FIRST_INITIALIZATION = new AtomicBoolean(true);
    private static final AtomicBoolean PRESTARTUP_HOOKS_STARTED = new AtomicBoolean(false);

    private static final FastThreadLocal<ATest> CURRENT_TEST_HOLDER = new FastThreadLocal<>();
    private static final FastThreadLocal<TestContextState> TEST_CONTEXT_STATE_HOLDER = new FastThreadLocal<>();

    static {
        Assertions.checkNotNull(PreMergedContext.getInstance());
    }

    private static final GenericXmlContextLoader PARENT = new GenericXmlContextLoader() {
        @Override
        protected void customizeContext(final GenericApplicationContext ctx) {
            try {
                final ATest currentTest = getCurrentTest();
                configureContext(currentTest, new TestContext(ctx, TEST_CONTEXT_STATE_HOLDER.get()), true);
            } catch (final Exception e) {
                throw Err.process(e);
            }
        };
    };

    @GuardedBy("this.class")
    private static TestContextState curTestContextState;

    static void setCurrentTest(final ATest currentTest) {
        TestContextLoader.CURRENT_TEST_HOLDER.set(currentTest);
    }

    static ATest getCurrentTest() {
        return TestContextLoader.CURRENT_TEST_HOLDER.get();
    }

    private List<PositionedResource> configureContextLocations() throws Exception {
        final TestContext premergedContext = new TestContext(PreMergedContext.getInstance(true), null);
        final List<PositionedResource> preMergedContexts = PreMergedContext.collectMergedContexts();
        final ATest currentTest = getCurrentTest();
        currentTest.setUpContextLocations(preMergedContexts);
        for (final IStub testHook : getTestHooks(PreMergedContext.getInstance())) {
            testHook.setUpContextLocations(currentTest, preMergedContexts);
        }
        configureContext(currentTest, premergedContext, false);
        final List<PositionedResource> mergedContexts = PreMergedContext.collectMergedContexts();
        currentTest.setUpContextLocations(mergedContexts);
        for (final IStub testHook : getTestHooks(PreMergedContext.getInstance())) {
            testHook.setUpContextLocations(currentTest, mergedContexts);
        }
        PositionedResource.COMPARATOR.asAscending().asNotNullSafe().sort(mergedContexts);
        return mergedContexts;
    }

    static synchronized TestContextState getCurTestContextState() {
        return curTestContextState;
    }

    private static void configureContext(final ATest currentTest, final TestContext ctx,
            final boolean replaceMergedContext) throws Exception {
        if (replaceMergedContext) {
            MergedContext.autowire(ctx);
        }
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
        final ATest currentTest = getCurrentTest();
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
                if (!xmlFile.exists()) {
                    final InputStream in = e.getValue().getInputStream();
                    Files.write(xmlFile, IOUtils.toString(in, Charset.defaultCharset()), Charset.defaultCharset());
                    in.close();
                    xmlFile.deleteOnExit();
                }
                final FileSystemResource fsResource = new FileSystemResource(xmlFile);
                newLocations.add(PositionedResource.of(fsResource, null));
            }
            final List<String> locationStrings = new ArrayList<String>();
            for (final Resource resource : newLocations) {
                final String resourceString = resource.getURI().toString();
                locationStrings.add(resourceString);
            }

            synchronized (TestContextLoader.class) {
                if (curTestContextState != null) {
                    if (curTestContextState.getLocationStrings().equals(locationStrings)) {
                        //return existing MergedContext since locations are the same
                        final TestContext ctx = curTestContextState.getContext();
                        if (ctx != null && ctx.isActive() && !ctx.isCloseRequested()) {
                            curTestContextState.registerTest(currentTest);
                            currentTest.setContext(ctx);
                            return ctx;
                        }
                    } else {
                        //make sure existing test context is not used anymore before replacing MergedContext with a different one
                        curTestContextState.waitForFinished();
                        final TestContext ctx = curTestContextState.getContext();
                        if (ctx != null && ctx.isActive()) {
                            ctx.closeAndEvict();
                        }
                    }
                }
                //load a new MergedContext based on the locationStrings
                final TestContextState state = new TestContextState(locationStrings);
                state.registerTest(currentTest);
                curTestContextState = state;
                ReinitializationHookManager.reinitializationStarted();
                MergedContext.logContextsBeingLoaded(newLocations);
                TEST_CONTEXT_STATE_HOLDER.set(state);
                final ConfigurableApplicationContext delegate = PARENT
                        .loadContext(locationStrings.toArray(Strings.EMPTY_ARRAY));
                TEST_CONTEXT_STATE_HOLDER.remove();
                final TestContext ctx = new TestContext(delegate, state);
                currentTest.setContext(ctx);
                if (FIRST_INITIALIZATION.getAndSet(false)) {
                    MergedContext.logBootstrapFinished();
                }
                ReinitializationHookManager.reinitializationFinished();
                return ctx;
            }
        } catch (final Throwable t) {
            final TestContext premergedContext = new TestContext(PreMergedContext.getInstance(true), null);
            final LoggedRuntimeException processed = Err.process(t);
            try {
                //need to clean up the mess, but cannot use currentTest since hooks are not initialized there
                for (final IStub hook : getTestHooks(PreMergedContext.getInstance())) {
                    hook.tearDownOnce(currentTest, premergedContext);
                }
                ReinitializationHookManager.reinitializationFailed();
            } catch (final Throwable tInner) {
                Err.process(tInner);
            }
            throw processed;
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
     * Forces loadContext to be called, by having a unique locations list every time. Essentially forcing
     * org.springframework.test.context.cache.DefaultContextCache to not really be used (since there is no better way to
     * disable the cache?). This will cause the DefaultContextCache to grow unlimited, potentially causing a memory
     * leak; though since we don't expect too many contexts per executed tests (this is a finite number) to be loaded
     * anyway and a closed TestContext does not take too much space, since the delegate is nulled, this should not cause
     * any memory problems during testing.
     */
    @Override
    public String[] processLocations(final Class<?> clazz, final String... locations) {
        final List<String> list = new ArrayList<String>(Arrays.asList(locations));
        list.add(0, CTX_DUMMY + "_" + UUID.randomUUID().toString());
        return list.toArray(Strings.EMPTY_ARRAY);
    }
}
