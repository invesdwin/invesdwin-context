package de.invesdwin.context.integration;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;

import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.TestContext;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.assertions.Executable;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@ThreadSafe
public class IntegrationTest extends ATest {

    public static final String HELLO_WORLD = "Hello World";

    @Inject
    @Named("testServiceGateway")
    private IIntegrationTestService gateway;
    @Inject
    private IntegrationTestService service;

    @Override
    public void setUpContext(final TestContext ctx) throws Exception {
        super.setUpContext(ctx);
        ctx.activateBean(IntegrationTestContextLocation.class);
    }

    @Test
    public void testHelloWorld() throws InterruptedException {
        Assertions.assertTimeout(new Duration(3, FTimeUnit.SECONDS), new Executable() {
            @Override
            public void execute() throws Throwable {
                gateway.helloWorld(HELLO_WORLD);
                service.waitForProcessedRequest();
            }
        });
    }

    @Test
    public void testHelloWorldWithAnswer() throws InterruptedException {
        Assertions.assertTimeout(new Duration(3, FTimeUnit.SECONDS), new Executable() {
            @Override
            public void execute() throws Throwable {
                final String response = gateway.helloWorldWithAnswer(HELLO_WORLD);
                Assertions.assertThat(response).isEqualTo(HELLO_WORLD);
                log.info(response);
                service.waitForProcessedRequest(); //in fact useless here, but still checking
            }
        });
    }
}
