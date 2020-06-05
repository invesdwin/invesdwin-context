package de.invesdwin.context.webserver;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.integration.IntegrationProperties;
import de.invesdwin.context.system.properties.IProperties;
import de.invesdwin.context.system.properties.SystemProperties;

@Immutable
public final class WebserverProperties {

    public static final boolean SSL_ENABLED;
    public static final int THREAD_POOL_COUNT;
    private static final SystemProperties SYSTEM_PROPERTIES;

    static {
        SSL_ENABLED = "https".equals(IntegrationProperties.WEBSERVER_BIND_URI.getScheme());

        SYSTEM_PROPERTIES = new SystemProperties(WebserverProperties.class);
        THREAD_POOL_COUNT = SYSTEM_PROPERTIES.getInteger("THREAD_POOL_COUNT");
        if (SSL_ENABLED) {
            //create default password warnings
            SYSTEM_PROPERTIES.getStringWithSecurityWarning("KEYSTORE_KEYPASS", IProperties.INVESDWIN_DEFAULT_PASSWORD);
            SYSTEM_PROPERTIES.getStringWithSecurityWarning("KEYSTORE_STOREPASS",
                    IProperties.INVESDWIN_DEFAULT_PASSWORD);
        }
    }

    private WebserverProperties() {
    }

    public static String getKeystoreResource() {
        return SYSTEM_PROPERTIES.getString("KEYSTORE_RESOURCE");
    }

    public static String getKeystoreKeypass() {
        return SYSTEM_PROPERTIES.getString("KEYSTORE_KEYPASS");
    }

    public static String getKeystoreAlias() {
        return SYSTEM_PROPERTIES.getString("KEYSTORE_ALIAS");
    }

    public static String getKeystoreStorepass() {
        return SYSTEM_PROPERTIES.getString("KEYSTORE_STOREPASS");
    }

}
