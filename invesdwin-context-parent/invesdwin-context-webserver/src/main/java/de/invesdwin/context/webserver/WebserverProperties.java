package de.invesdwin.context.webserver;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.IntegrationProperties;
import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.context.system.properties.SystemProperties;

@Immutable
public final class WebserverProperties {

    public static final boolean SSL_ENABLED;

    static {
        SSL_ENABLED = "https".equals(IntegrationProperties.WEBSERVER_BIND_URI.getScheme());

        if (SSL_ENABLED) {
            final SystemProperties systemProperties = new SystemProperties(WebserverProperties.class);
            //create default password warnings
            systemProperties.getStringWithSecurityWarning("KEYSTORE_KEYPASS", IProperties.INVESDWIN_DEFAULT_PASSWORD);
            systemProperties.getStringWithSecurityWarning("KEYSTORE_STOREPASS", IProperties.INVESDWIN_DEFAULT_PASSWORD);
        }
    }

    private WebserverProperties() {}

}
