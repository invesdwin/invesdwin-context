package de.invesdwin.context.integration;

import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import org.springframework.integration.annotation.ServiceActivator;

import de.invesdwin.context.log.Log;
import de.invesdwin.util.assertions.Assertions;

@ThreadSafe
public class IntegrationTestService implements IIntegrationTestService {

    private volatile boolean requestProcesses;
    private final Log log = new Log(this);

    public void waitForProcessedRequest() throws InterruptedException {
        while (!requestProcesses) {
            TimeUnit.SECONDS.sleep(1);
        }
        requestProcesses = false;
    }

    @ServiceActivator(inputChannel = HELLO_WORLD_REQUEST_CHANNEL)
    @Override
    public void helloWorld(final String request) {
        Assertions.assertThat(request).isEqualTo(IntegrationTest.HELLO_WORLD);
        log.info(request);
        requestProcesses = true;
    }

    @ServiceActivator(inputChannel = HELLO_WORLD_MIT_ANSWER_REQUEST_CHANNEL)
    @Override
    public String helloWorldWithAnswer(final String request) {
        Assertions.assertThat(request).isEqualTo(IntegrationTest.HELLO_WORLD);
        log.info(request);
        requestProcesses = true;
        return request;
    }

}
