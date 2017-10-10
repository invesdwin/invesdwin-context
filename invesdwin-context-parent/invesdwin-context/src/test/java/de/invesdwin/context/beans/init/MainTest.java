package de.invesdwin.context.beans.init;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;

import org.junit.Test;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.shutdown.IShutdownHook;
import de.invesdwin.util.shutdown.ShutdownHookManager;

@NotThreadSafe
public class MainTest extends AMain {

    private static final String TEST_SYSPROP_KEY = "someSysProp";
    private static final String TEST_SYSPROP_VALUE = "someSysPropValue";
    private static final String TEST_SYSPROP_TOO_KEY = "someSysPropToo";
    private static final String TEST_SYSPROP_TOO_VALUE = "someSysPropTooValue";
    private static final String TEST_ARGUMENT_SHORT = "-t";
    private static final String TEST_ARGUMENT_LONG = "--test";
    private static final String[] ARGS = new String[] { "-D" + TEST_SYSPROP_KEY + "=" + TEST_SYSPROP_VALUE,
            TEST_ARGUMENT_LONG, "-D" + TEST_SYSPROP_TOO_KEY + "=" + TEST_SYSPROP_TOO_VALUE };

    @Option(name = TEST_ARGUMENT_SHORT, aliases = TEST_ARGUMENT_LONG, required = true, usage = "testUsage")
    private boolean test;

    @Inject
    private TestBean bean;

    @Inject
    private TestBeanToo too;

    public MainTest() {
        super(ARGS);
        Assertions.assertThat(new SystemProperties().getString(TEST_SYSPROP_KEY)).isEqualTo(TEST_SYSPROP_VALUE);
        Assertions.assertThat(new SystemProperties().getString(TEST_SYSPROP_TOO_KEY)).isEqualTo(TEST_SYSPROP_TOO_VALUE);
        Assertions.assertThat(test).isTrue();
        Assertions.assertThat(ShutdownHookManager.isShuttingDown()).isFalse();
        ShutdownHookManager.register(new IShutdownHook() {
            @Override
            public void shutdown() throws Exception {
                Assertions.assertThat(ShutdownHookManager.isShuttingDown()).isTrue();
            }
        });
    }

    @Test
    public void test() throws InterruptedException {
        Assertions.assertThat(bean).isNotNull();
        bean.test();
        Assertions.assertThat(too).isNotSameAs(bean.getToo());
    }

    @Test
    public void testEnvironmentCorrectlyDetected() {
        Assertions.assertThat(ContextProperties.IS_TEST_ENVIRONMENT).isTrue();
    }

    @Test
    public void testPrintHelp() {
        final CmdLineParser parser = new CmdLineParser(this);
        printHelp(parser);
    }

    @Override
    protected void startApplication(final CmdLineParser parser) {
        Assertions.assertThat(test).isTrue();
    }

}
