package de.invesdwin.context.integration;

import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.util.lang.uri.Addresses;
import de.invesdwin.util.lang.uri.URIs;

@ThreadSafe
public final class IntegrationProperties {

    public static final List<URI> INTERNET_CHECK_URIS;
    public static final URI WEBSERVER_BIND_URI;
    private static volatile boolean webserverTest;

    private static final SystemProperties SYSTEM_PROPERTIES;

    static {
        SYSTEM_PROPERTIES = new SystemProperties(IntegrationProperties.class);

        INTERNET_CHECK_URIS = readInternetCheckUris();
        WEBSERVER_BIND_URI = readWebserverBindUri();
    }

    private IntegrationProperties() {}

    private static List<URI> readInternetCheckUris() {
        final String[] uris = SYSTEM_PROPERTIES.getStringArray("INTERNET_CHECK_URIS");
        final List<URI> uriList = new ArrayList<URI>();
        for (final String uri : uris) {
            uriList.add(URIs.asUri(uri));
        }
        return Collections.unmodifiableList(uriList);
    }

    private static URI readWebserverBindUri() {
        final String key = "WEBSERVER_BIND_URI";
        final String expectedFormat = "Expected Format: (http|https)://<host>:<port>";
        final URL url = SYSTEM_PROPERTIES.getURL(key, true);
        final int port = url.getPort();
        if (!Addresses.isPort(port)) {
            throw new IllegalArgumentException(SYSTEM_PROPERTIES.getErrorMessage(key, url, null,
                    "Port [" + port + "] is incorrect. " + expectedFormat));
        }
        final String protocol = url.getProtocol();
        if (!("http".equals(protocol) || "https".equals(protocol))) {
            throw new IllegalArgumentException(SYSTEM_PROPERTIES.getErrorMessage(key, url, null,
                    "Protocol [" + protocol + "] is incorrect. " + expectedFormat));
        }
        return URIs.asUri(url);
    }

    /**
     * Determines if a test with a webserver enabled is currently running.
     */
    public static boolean isWebserverTest() {
        return webserverTest;
    }

    /**
     * A test webserver should set this property on startup to true.
     */
    public static void setWebserverTest(final boolean webserverTest) {
        IntegrationProperties.webserverTest = webserverTest;
    }

    public static Proxy getSystemProxy() {
        final SystemProperties properties = new SystemProperties();
        final String httpProxyHostKey = "http.proxyHost";
        final String httpProxyPortKey = "http.proxyPort";
        if (properties.containsKey(httpProxyHostKey) && properties.containsKey(httpProxyPortKey)) {
            final String httpProxyHost = properties.getString(httpProxyHostKey);
            final Integer httpProxyPort = properties.getInteger(httpProxyPortKey);
            return new Proxy(Type.HTTP, Addresses.asAddress(httpProxyHost, httpProxyPort));
        } else {
            return null;
        }
    }

}
