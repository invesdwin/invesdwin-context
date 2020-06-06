package de.invesdwin.context.webserver.internal;

import javax.annotation.concurrent.NotThreadSafe;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import de.invesdwin.context.integration.IntegrationProperties;

@NotThreadSafe
public class ConfiguredHttpServer extends Server {

    public ConfiguredHttpServer() {
        super(new FastQueuedThreadPool());
        final HttpConfiguration config = createHttpConfiguration();
        final HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(config);
        //        final HTTP2CServerConnectionFactory http2cServerConnectionFactory = new HTTP2CServerConnectionFactory(config);
        final ServerConnector connector = new ServerConnector(this, httpConnectionFactory);
        connector.setPort(IntegrationProperties.WEBSERVER_BIND_URI.getPort());
        addConnector(connector);
    }

    private static HttpConfiguration createHttpConfiguration() {
        final HttpConfiguration config = new HttpConfiguration();
        config.setSecureScheme("http");
        config.setSecurePort(IntegrationProperties.WEBSERVER_BIND_URI.getPort());
        config.setSendXPoweredBy(false);
        config.setSendServerVersion(false);
        return config;
    }

}
