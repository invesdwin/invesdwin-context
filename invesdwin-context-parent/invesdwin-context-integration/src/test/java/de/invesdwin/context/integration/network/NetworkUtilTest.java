package de.invesdwin.context.integration.network;

import java.net.InetAddress;

import javax.annotation.concurrent.Immutable;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.test.ATest;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;

@Immutable
public class NetworkUtilTest extends ATest {

    @Test
    public void testWaitIfInternetNotAvailable() throws InterruptedException {
        Assertions.assertThat(NetworkUtil.waitIfInternetNotAvailable()).isFalse();
    }

    @Test
    public void testWaitIfInternetNotAvailableParallel() throws InterruptedException {
        final WrappedExecutorService executor = Executors.newFixedThreadPool("testWaitIfInternetNotAvailableParallel",
                100);
        for (int i = 0; i < 10000; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        NetworkUtil.waitIfInternetNotAvailable();
                    } catch (final InterruptedException e) {
                        throw Err.process(e);
                    }
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination();
    }

    @Test
    public void testGetNetworkAddress() {
        final InetAddress local = NetworkUtil.getLocalAddress();
        Assertions.assertThat(local).isNotNull();
        log.debug(local.getHostAddress() + " | " + local.getHostName());
        Assertions.assertThat(local.getHostAddress()).isNotEqualTo("127.0.0.1");
    }

    @Test
    public void testGetInternetAddress() {
        final InetAddress external = NetworkUtil.getExternalAddress();
        Assertions.assertThat(external).isNotNull();
        log.debug(external.getHostAddress() + " | " + external.getHostName());
    }

}
