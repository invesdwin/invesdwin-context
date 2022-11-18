package de.invesdwin.context.webserver.internal;

import javax.annotation.concurrent.NotThreadSafe;

import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import de.invesdwin.context.integration.IntegrationProperties;
import de.invesdwin.context.webserver.WebserverProperties;

/**
 * https://github.com/AndreasKl/embedded-jetty-http2/blob/master/src/main/java/net/andreaskluth/Application.java#L129
 * 
 * @author subes
 *
 */
@NotThreadSafe
public class ConfiguredHttpsServer extends Server {

    public ConfiguredHttpsServer() {
        super(new FastQueuedThreadPool());
        final HttpConfiguration config = createHttpConfiguration();
        final HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(config);
        final HTTP2ServerConnectionFactory http2ConnectionFactory = new HTTP2ServerConnectionFactory(config);
        final ALPNServerConnectionFactory alpn = createAlpnProtocolFactory(httpConnectionFactory);
        final ServerConnector connector = new ServerConnector(this, prepareSsl(alpn), alpn, http2ConnectionFactory,
                httpConnectionFactory);
        connector.setPort(IntegrationProperties.WEBSERVER_BIND_URI.getPort());
        addConnector(connector);
    }

    private ALPNServerConnectionFactory createAlpnProtocolFactory(final HttpConnectionFactory httpConnectionFactory) {
        final ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
        alpn.setDefaultProtocol(httpConnectionFactory.getProtocol());
        return alpn;
    }

    private SslConnectionFactory prepareSsl(final ALPNServerConnectionFactory alpn) {
        final SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStoreResource(new JettySpringResource(WebserverProperties.getKeystoreResource()));
        sslContextFactory.setKeyStorePassword(WebserverProperties.getKeystoreKeypass());
        sslContextFactory.setCertAlias(WebserverProperties.getKeystoreAlias());
        sslContextFactory.setKeyManagerPassword(WebserverProperties.getKeystoreStorepass());
        sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
        sslContextFactory.setUseCipherSuitesOrder(true);
        final SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());
        return ssl;
    }

    private static HttpConfiguration createHttpConfiguration() {
        final HttpConfiguration config = new HttpConfiguration();
        config.setSecureScheme("https");
        config.setSecurePort(IntegrationProperties.WEBSERVER_BIND_URI.getPort());
        config.setSendXPoweredBy(false);
        config.setSendServerVersion(false);
        config.addCustomizer(new SecureRequestCustomizer());
        return config;
    }

}
