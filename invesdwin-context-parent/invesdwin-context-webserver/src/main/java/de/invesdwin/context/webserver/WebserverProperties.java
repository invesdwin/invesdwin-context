package de.invesdwin.context.webserver;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.IntegrationProperties;

@Immutable
public final class WebserverProperties {

    public static final boolean SSL_ENABLED;

    static {
        SSL_ENABLED = "https".equals(IntegrationProperties.WEBSERVER_BIND_URI.getScheme());
    }

    private WebserverProperties() {}

}
