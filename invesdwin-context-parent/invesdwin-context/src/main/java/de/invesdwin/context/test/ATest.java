package de.invesdwin.context.test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import de.invesdwin.context.beans.init.MergedContext;
import de.invesdwin.context.beans.init.PreMergedContext;
import de.invesdwin.context.beans.init.locations.PositionedResource;
import de.invesdwin.context.log.Log;
import de.invesdwin.context.test.internal.ITestLifecycle;
import de.invesdwin.context.test.internal.LoadTimeWeavingSpringExtension;
import de.invesdwin.context.test.stub.IStub;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.time.Instant;
import io.netty.util.concurrent.FastThreadLocal;
import jakarta.inject.Inject;

/**
 * If a BeanNotFoundException occurs during tests where EntityManagerFactory is not found, then it means that the test
 * should implement APersistenceTest instead of ATest. This is because the persistence module is in classpath.
 * 
 * <p>
 * If you have a test that cannot extend this class, but needs to initialize load time weaving properly, put the
 * following code into the test:
 * </p>
 * 
 * <pre>
 * static {
 *     //initialize load time weaving
 *     Assertions.assertThat(PreMergedContext.getInstance()).isNotNull();
 * }
 * </pre>
 * 
 * WARNING: Aspects will not be woven into this class due to bootstrap limitations. Since the class that initializes the
 * load time weaving is loaded before that, it cannot be woven afterwards. Use inner classes instead for tests of
 * aspects.
 */
@ExtendWith(LoadTimeWeavingSpringExtension.class)
@ContextConfiguration(locations = { TestContextLoader.CTX_DUMMY }, loader = TestContextLoader.class)
@NotThreadSafe
//TransactionalTestExecutionListener collides with CTW transactions
@TestExecutionListeners({ DirtiesContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
public abstract class ATest implements ITestLifecycle, ITestContextState {

    private static final AtomicInteger NEXT_TEST_CLASS_ID = new AtomicInteger();
    @GuardedBy("this.class")
    private static final ALoadingCache<Class<?>, TestClassRun> TEST_CLASS_RUN = new ALoadingCache<Class<?>, TestClassRun>() {
        @Override
        protected TestClassRun loadValue(final Class<?> key) {
            return new TestClassRun();
        }

        @Override
        protected boolean isHighConcurrency() {
            return true;
        }
    };
    private static final FastThreadLocal<ATest> LAST_TEST = new FastThreadLocal<>();

    protected final Log log = new Log(this);

    private TestClassRun run;
    private int testMethodId;

    private Instant testMethodTimeMeasurement;
    private AutoCloseable mocks;

    //CHECKSTYLE:OFF public
    public String testMethodName;
    //CHECKSTYLE:ON

    static {
        Assertions.assertThat(PreMergedContext.class).isNotNull();
    }

    @Inject
    private IStub[] hooks;

    public ATest() {
        if (TestContextLoader.getCurrentTest() == null) {
            TestContextLoader.setCurrentTest(this);
        }
        LAST_TEST.set(this);
    }

    /**
     * Tells how many parallel tests are currently running over all test classes.
     */
    public int getActiveCountGlobal() {
        return TestContextState.ACTIVE_COUNT_GLOBAL.get();
    }

    @Override
    public boolean isFinishedGlobal() {
        return TestContextState.ACTIVE_COUNT_GLOBAL.get() <= 0;
    }

    /**
     * Tells how many parallel tests are currently running on this test class (each thread has its own instance of this
     * class, thus setUpOnce/tearDownOnce should only modify static state, while setUp/tearDown should modify only the
     * current instance). @ParallelSuite wrappers allow to modify if parallelisation should happen per testSuite,
     * testClass, or testMethod to control thread safety further for tests.
     */
    public int getActiveCount() {
        if (run == null) {
            return 0;
        }
        return run.activeCount.get();
    }

    @Override
    public boolean isFinished() {
        if (run == null) {
            return false;
        }
        return run.activeCount.get() <= 0;
    }

    void setContext(final TestContext ctx) {
        if (run == null) {
            run = TEST_CLASS_RUN.get(getClass());
        }
        run.setContext(ctx);
    }

    @Override
    public void setUpContextLocations(final List<PositionedResource> contextLocations) throws Exception {
        //noop
    }

    @Override
    public void setUpContextBeforeLoading() throws Exception {
        //noop
    }

    @Override
    public void setUpContext(final ITestContextSetup ctx) throws Exception {
        //noop
    }

    @BeforeEach
    public final void before(final TestInfo testInfo) throws Exception {
        if (run == null) {
            run = TEST_CLASS_RUN.get(getClass());
        }
        run.setUp(this, testInfo);
    }

    @AfterEach
    public final void after() throws Exception {
        run.tearDown(this);
    }

    @Override
    public void setUpOnce() throws Exception {
        //noop
    }

    @Override
    public void setUp() throws Exception {
        //noop
    }

    @Override
    public void tearDown() throws Exception {
        //noop
    }

    @Override
    public void tearDownOnce() throws Exception {
        //noop
    }

    @AfterAll
    public static void afterAll() throws Exception {
        //clean up refrence also in the thread where the instance got created and registered
        TestContextLoader.setCurrentTest(null);
        final ATest lastTest = LAST_TEST.get();
        if (lastTest != null) {
            final TestClassRun run = lastTest.run;
            if (run != null) {
                run.tearDownOnce(lastTest);
                LAST_TEST.remove();
            }
        }
    }

    private static final class TestClassRun {
        private final Instant testClassTimeMeasurement = new Instant();
        private final int testClassId = NEXT_TEST_CLASS_ID.incrementAndGet();
        private final AtomicInteger nextTestMethodId = new AtomicInteger();
        @GuardedBy("this")
        private boolean setUpOnceCalled = false;
        private final AtomicInteger activeCount = new AtomicInteger();
        @GuardedBy("this")
        private TestContext context;

        private TestClassRun() {}

        public synchronized void setContext(final TestContext context) {
            this.context = context;
        }

        public synchronized TestContext getContext() {
            if (context != null) {
                return context;
            }
            final TestContextState state = TestContextLoader.getCurTestContextState();
            if (state == null) {
                return new TestContext(PreMergedContext.getInstance(), null);
            } else {
                return new TestContext(MergedContext.getInstance(), state);
            }
        }

        public synchronized void maybeSetUpOnce(final ATest test) throws Exception {
            if (setUpOnceCalled) {
                return;
            }
            setUpOnceCalled = true;
            test.log.info("%s) >> [%s] >> running (%s|%s)", testClassId, test.getClass().getName(),
                    TestContextState.ACTIVE_COUNT_GLOBAL.get(), activeCount.get());
            final TestContext ctx = getContext();
            test.setUpOnce();
            for (final IStub hook : test.hooks) {
                hook.setUpOnce(test, ctx);
            }
        }

        public synchronized void setUp(final ATest test, final TestInfo testInfo) throws Exception {
            final int globalActive = TestContextState.ACTIVE_COUNT_GLOBAL.incrementAndGet();
            final int active = activeCount.incrementAndGet();
            test.testMethodName = testInfo.getTestMethod().get().getName();
            test.testMethodId = nextTestMethodId.incrementAndGet();
            test.mocks = MockitoAnnotations.openMocks(test);
            maybeSetUpOnce(test);
            test.log.info("%s.%s) ++ [%s.%s] ++ running (%s|%s)", testClassId, test.testMethodId,
                    test.getClass().getSimpleName(), test.testMethodName, globalActive, active);
            final TestContext ctx = getContext();
            test.setUp();
            for (final IStub hook : test.hooks) {
                hook.setUp(test, ctx);
            }
            test.testMethodTimeMeasurement = new Instant();
        }

        public synchronized void tearDown(final ATest test) throws Exception {
            final int globalActive = TestContextState.ACTIVE_COUNT_GLOBAL.getAndDecrement();
            final int active = activeCount.getAndDecrement();
            test.log.info("%s.%s) -- [%s.%s] -- finished (%s|%s) after %s", testClassId, test.testMethodId,
                    test.getClass().getSimpleName(), test.testMethodName, globalActive, active,
                    test.testMethodTimeMeasurement);
            final TestContext ctx = getContext();
            test.tearDown();
            for (final IStub hook : test.hooks) {
                hook.tearDown(test, ctx);
            }
            if (test.mocks != null) {
                test.mocks.close();
                test.mocks = null;
            }
            test.testMethodName = null;
            test.testMethodId = 0;
            test.testMethodTimeMeasurement = null;
        }

        public synchronized void tearDownOnce(final ATest test) throws Exception {
            final TestContext ctx = getContext();
            ctx.getState().unregisterTest(test);
            test.tearDownOnce();
            for (final IStub hook : test.hooks) {
                hook.tearDownOnce(test, ctx);
            }
            TEST_CLASS_RUN.remove(test.getClass());
            //clean up reference on any thread that might have had created an instance for running a parallel test method
            TestContextLoader.setCurrentTest(null);
            test.log.info("%s) << [%s] << finished (%s|%s) after %s", testClassId, test.getClass().getName(),
                    TestContextState.ACTIVE_COUNT_GLOBAL.get(), activeCount.get(), testClassTimeMeasurement);
        }
    }

}
