package de.invesdwin.context.webserver.test.internal;

import java.util.List;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.eclipse.jetty.server.Server;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import de.invesdwin.context.beans.init.MergedContext;
import de.invesdwin.context.beans.init.locations.PositionedResource;
import de.invesdwin.context.integration.IntegrationProperties;
import de.invesdwin.context.test.ATest;
import de.invesdwin.context.test.TestContext;
import de.invesdwin.context.test.stub.StubSupport;
import de.invesdwin.context.webserver.WebserverContextLocation;
import de.invesdwin.context.webserver.test.WebserverTest;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.shutdown.IShutdownHook;
import de.invesdwin.util.shutdown.ShutdownHookManager;
import jakarta.inject.Named;

@Named
@ThreadSafe
public class WebserverTestStub extends StubSupport {

    @GuardedBy("this.class")
    private static Server lastServer;

    static {
        ShutdownHookManager.register(new IShutdownHook() {
            @Override
            public void shutdown() throws Exception {
                maybeStopLastServer();
            }
        });
    }

    @Override
    public void setUpContextLocations(final ATest test, final List<PositionedResource> locations) throws Exception {
        final WebserverTest annotation = Reflections.getAnnotation(test, WebserverTest.class);
        if (annotation != null) {
            if (annotation.value()) {
                locations.add(WebserverContextLocation.getContextLocation());
            } else {
                locations.remove(WebserverContextLocation.getContextLocation());
            }
        }
    }

    @Override
    public void setUpContext(final ATest test, final TestContext ctx) throws Exception {
        if (ctx.isPreMergedContext()) {
            return;
        }
        //if for some reason the tearDownOnce was not executed on the last test (maybe maven killed it?), then try to stop here aswell
        maybeStopLastServer();
    }

    @Override
    public void setUpOnce(final ATest test, final TestContext ctx) throws Exception {
        synchronized (WebserverTestStub.class) {
            if (WebserverTestStub.lastServer == null) {
                try {
                    WebserverTestStub.lastServer = MergedContext.getInstance().getBean(Server.class);
                } catch (final NoSuchBeanDefinitionException e) { //SUPPRESS CHECKSTYLE empty block
                    //ignore
                }
            }
        }
    }

    @Override
    public void tearDownOnce(final ATest test, final TestContext ctx) throws Exception {
        if (!ctx.isFinished()) {
            return;
        }
        maybeStopLastServer();
    }

    private static synchronized void maybeStopLastServer() throws Exception {
        if (lastServer != null) {
            IntegrationProperties.setWebserverTest(false);
            lastServer.stop();
            lastServer = null;
        }
    }

}
