package de.invesdwin.context.webserver;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.IntegrationProperties;
import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.context.system.properties.SystemProperties;

@Immutable
public final class WebserverProperties {

    public static final boolean SSL_ENABLED;
    public static final int THREAD_POOL_COUNT;

    static {
        SSL_ENABLED = "https".equals(IntegrationProperties.WEBSERVER_BIND_URI.getScheme());

        final SystemProperties systemProperties = new SystemProperties(WebserverProperties.class);
        THREAD_POOL_COUNT = systemProperties.getInteger("THREAD_POOL_COUNT");
        if (SSL_ENABLED) {
            //create default password warnings
            systemProperties.getStringWithSecurityWarning("KEYSTORE_KEYPASS", IProperties.INVESDWIN_DEFAULT_PASSWORD);
            systemProperties.getStringWithSecurityWarning("KEYSTORE_STOREPASS", IProperties.INVESDWIN_DEFAULT_PASSWORD);
        }
    }

    private WebserverProperties() {}

}
