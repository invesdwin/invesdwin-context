package de.invesdwin.context.integration;

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
    public static final boolean JNI_COMPRESSION_ALLOWED;
    public static final boolean FAST_COMPRESSION_ALWAYS;
    private static final FastThreadLocal<Boolean> THREAD_RETRY_DISABLED = new FastThreadLocal<>();
    private static volatile boolean webserverTest;

    private static final SystemProperties SYSTEM_PROPERTIES;

    static {
        SYSTEM_PROPERTIES = new SystemProperties(IntegrationProperties.class);
        HOSTNAME = determineHostname();

        INTERNET_CHECK_URIS = readInternetCheckUris();
        WEBSERVER_BIND_URI = readWebserverBindUri();
        JNI_COMPRESSION_ALLOWED = readJniCompressionAllowed();
        FAST_COMPRESSION_ALWAYS = readFastCompressionAlways();
    }

    private IntegrationProperties() {
    }

    private static boolean readJniCompressionAllowed() {
        final String key = "JNI_COMPRESSION_ALLOWED";
        if (SYSTEM_PROPERTIES.containsValue(key)) {
            final boolean jniCompressionAllowed = SYSTEM_PROPERTIES.getBoolean(key);
            return jniCompressionAllowed;
        } else {
            return false;
        }
    }

    private static boolean readFastCompressionAlways() {
        final String key = "FAST_COMPRESSION_ALWAYS";
        if (SYSTEM_PROPERTIES.containsValue(key)) {
            final boolean fastCompressionAlways = SYSTEM_PROPERTIES.getBoolean(key);
            return fastCompressionAlways;
        } else {
            return false;
        }
    }

    private static List<URI> readInternetCheckUris() {
        final String key = "INTERNET_CHECK_URIS";
        if (SYSTEM_PROPERTIES.containsValue(key)) {
            final String[] uris = SYSTEM_PROPERTIES.getStringArray(key);
            final List<URI> uriList = new ArrayList<URI>();
            for (final String uri : uris) {
                uriList.add(URIs.asUri(uri));
            }
            return Collections.unmodifiableList(uriList);
        } else {
            return Collections.emptyList();
        }
    }

    private static URI readWebserverBindUri() {
        final String key = "WEBSERVER_BIND_URI";
        if (SYSTEM_PROPERTIES.containsValue(key)) {
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
        } else {
            return null;
        }
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
