package de.invesdwin.context.webserver.internal;

import javax.annotation.concurrent.ThreadSafe;

import org.eclipse.jetty.util.thread.QueuedThreadPool;

import de.invesdwin.context.integration.IntegrationProperties;
import de.invesdwin.context.webserver.WebserverProperties;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;

@ThreadSafe
public class FastQueuedThreadPool extends QueuedThreadPool {

    public FastQueuedThreadPool() {
        final StringBuilder name = new StringBuilder("Jetty_");
        if (WebserverProperties.SSL_ENABLED) {
            name.append("https");
        } else {
            name.append("http");
        }
        name.append("_");
        name.append(IntegrationProperties.WEBSERVER_BIND_URI.getPort());
        setName(name.toString());
        setMinThreads(1);
        setMaxThreads(WebserverProperties.THREAD_POOL_COUNT);
    }

    @Override
    protected Thread newThread(final Runnable runnable) {
        return new FastThreadLocalThread(runnable);
    }

    @Override
    protected void runJob(final Runnable job) {
        try {
            super.runJob(job);
        } finally {
            FastThreadLocal.removeAll();
        }
    }

}
