package de.invesdwin.context.integration;

import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.BooleanUtils;

import de.invesdwin.aspects.EventDispatchThreadUtil;
import de.invesdwin.context.integration.network.NetworkUtil;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.lang.uri.Addresses;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.math.Booleans;
import io.netty.util.concurrent.FastThreadLocal;

@ThreadSafe
public final class IntegrationProperties {

    public static final List<URI> INTERNET_CHECK_URIS;
    public static final URI WEBSERVER_BIND_URI;
    public static final String HOSTNAME;
    private static final FastThreadLocal<Boolean> THREAD_RETRY_DISABLED = new FastThreadLocal<>();
    private static volatile boolean webserverTest;

    private static final SystemProperties SYSTEM_PROPERTIES;

    static {
        SYSTEM_PROPERTIES = new SystemProperties(IntegrationProperties.class);
        HOSTNAME = determineHostname();

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

    private static String determineHostname() {
        final String key = "HOSTNAME";
        if (SYSTEM_PROPERTIES.containsValue(key)) {
            return SYSTEM_PROPERTIES.getString(key);
        } else {
            final String hostname = NetworkUtil.getHostname();
            SYSTEM_PROPERTIES.setString(key, hostname);
            return hostname;
        }
    }

    public static boolean isThreadRetryDisabled() {
        return Booleans.isTrue(THREAD_RETRY_DISABLED.get()) || EventDispatchThreadUtil.isEventDispatchThread()
                || Threads.isInterrupted();
    }

    public static boolean registerThreadRetryDisabled() {
        final boolean retryDisabledBefore = BooleanUtils.isTrue(THREAD_RETRY_DISABLED.get());
        if (!retryDisabledBefore) {
            THREAD_RETRY_DISABLED.set(true);
            return true;
        } else {
            return false;
        }
    }

    public static void unregisterThreadRetryDisabled(final boolean registerThreadRetryDisabled) {
        if (registerThreadRetryDisabled) {
            THREAD_RETRY_DISABLED.remove();
        }
    }

}
