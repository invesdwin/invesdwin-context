package de.invesdwin.context;

import java.io.File;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.BooleanUtils;

import de.invesdwin.context.beans.init.platform.IPlatformInitializer;
import de.invesdwin.context.beans.init.platform.util.internal.BasePackagesConfigurer;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.lang.uri.Addresses;
import de.invesdwin.util.lang.uri.connect.InputStreamHttpResponseConsumer;
import de.invesdwin.util.lang.uri.connect.apache.URIsConnectApache;
import de.invesdwin.util.lang.uri.connect.okhttp.URIsConnectOkHttp;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@ThreadSafe
public final class ContextProperties {

    public static final String DEFAULT_CACHE_NAME = "default";

    public static final boolean IS_TEST_ENVIRONMENT;

    /**
     * Process specific temp dir that gets cleaned on exit.
     */
    public static final File TEMP_DIRECTORY;
    public static final File TEMP_CLASSPATH_DIRECTORY;
    public static final Duration DEFAULT_NETWORK_TIMEOUT;
    public static final int DEFAULT_NETWORK_TIMEOUT_MILLIS;
    public static final int CPU_THREAD_POOL_COUNT;
    @GuardedBy("ContextProperties.class")
    private static File cacheDirectory;
    @GuardedBy("ContextProperties.class")
    private static File homeDirectory;
    @GuardedBy("ContextProperties.class")
    private static File logDirectory;

    static {
        final IPlatformInitializer initializer = PlatformInitializerProperties.getInitializer();
        IS_TEST_ENVIRONMENT = initializer.initIsTestEnvironment();

        File tempDirectory = null;
        File tempClasspathDirectory = null;
        try {
            tempDirectory = initializer.initTempDirectory();
            tempClasspathDirectory = initializer.initTempClasspathDirectory(tempDirectory);
        } catch (final Throwable t) {
            //webstart safety for access control
            tempDirectory = null;
            tempClasspathDirectory = null;
            PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
        }
        TEMP_DIRECTORY = tempDirectory;
        TEMP_CLASSPATH_DIRECTORY = tempClasspathDirectory;

        if (PlatformInitializerProperties.isAllowed()) {
            try {
                initializer.initXmlTransformerConfigurer();
                initializer.initLogbackConfigurationLoader();
                initializer.initSystemPropertiesLoader();
                initializer.initJavaUtilPrefsBackingStoreDirectory();

                initializer.initDefaultCache(DEFAULT_CACHE_NAME);
            } catch (final Throwable t) {
                PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
            }
        }

        DEFAULT_NETWORK_TIMEOUT = readDefaultNetworkTimeout();
        initializer.initConscryptSecurityProvider();
        URIsConnectOkHttp.setDefaultNetworkTimeout(DEFAULT_NETWORK_TIMEOUT);
        URIsConnectOkHttp.setDefaultProxy(getSystemProxy());
        URIsConnectApache.setDefaultNetworkTimeout(DEFAULT_NETWORK_TIMEOUT);
        InputStreamHttpResponseConsumer
                .setDefaultTempDir(new File(TEMP_DIRECTORY, InputStreamHttpResponseConsumer.class.getSimpleName()));
        DEFAULT_NETWORK_TIMEOUT_MILLIS = ContextProperties.DEFAULT_NETWORK_TIMEOUT.intValue(FTimeUnit.MILLISECONDS);
        CPU_THREAD_POOL_COUNT = readCpuThreadPoolCount();
        Executors.setCpuThreadPoolCount(CPU_THREAD_POOL_COUNT);
    }

    private ContextProperties() {
    }

    /**
     * Invesdwin specific home dir that gets shared between multiple processes.
     * 
     * this should be $HOME/.invesdwin
     */
    public static synchronized File getHomeDirectory() {
        if (homeDirectory == null) {
            homeDirectory = PlatformInitializerProperties.getInitializer()
                    .initHomeDirectory(getSystemHomeDirectory(),
                            IS_TEST_ENVIRONMENT && !PlatformInitializerProperties.isKeepSystemHomeDuringTests());
        }
        return homeDirectory;
    }

    /**
     * http://stackoverflow.com/questions/585534/what-is-the-best-way-to-find-the-users-home-directory-in-java
     * 
     * this should $HOME
     */
    public static String getSystemHomeDirectory() {
        return new SystemProperties().getString("user.home");
    }

    public static synchronized File getLogDirectory() {
        if (logDirectory == null) {
            logDirectory = PlatformInitializerProperties.getInitializer()
                    .initLogDirectory(IS_TEST_ENVIRONMENT, getFallbackWorkDirectory());
        }
        return logDirectory;
    }

    public static File getFallbackWorkDirectory() {
        return new File(getHomeDirectory(), "work");
    }

    /**
     * Cache dir that holds information over multiple JVM instances.
     */
    public static synchronized File getCacheDirectory() {
        if (cacheDirectory == null) {
            cacheDirectory = PlatformInitializerProperties.getInitializer()
                    .initCacheDirectory(getFallbackWorkDirectory());
        }
        return cacheDirectory;
    }

    public static synchronized Set<String> getBasePackages() {
        return BasePackagesConfigurer.getBasePackages();
    }

    public static String[] getBasePackagesArray() {
        return BasePackagesConfigurer.getBasePackagesArray();
    }

    private static Duration readDefaultNetworkTimeout() {
        final SystemProperties systemProperties = new SystemProperties(ContextProperties.class);
        final String durationKey = "DEFAULT_NETWORK_TIMEOUT";
        final Duration duration;
        if (!PlatformInitializerProperties.isAllowed() || !systemProperties.containsValue(durationKey)) {
            //default to 30 seconds if for some reason the properties were not loaded
            duration = new Duration(30, FTimeUnit.SECONDS);
        } else {
            duration = systemProperties.getDuration(durationKey);
            //So that Spring-WS also respects the timeouts...
            final String skipSystemPropertiesKey = "DEFAULT_NETWORK_TIMEOUT_SKIP_SYSTEM_PROPERTIES";
            final boolean skipSystemProperties = systemProperties.containsValue(skipSystemPropertiesKey)
                    && BooleanUtils.isTrue(systemProperties.getBoolean(skipSystemPropertiesKey));
            if (!skipSystemProperties) {
                PlatformInitializerProperties.getInitializer().initDefaultNetworkTimeoutSystemProperties(duration);
            }
        }
        return duration;
    }

    private static int readCpuThreadPoolCount() {
        try {
            final SystemProperties systemProperties = new SystemProperties(ContextProperties.class);
            final String key = "CPU_THREAD_POOL_COUNT";
            Integer cpuThreadPoolCount;
            if (!PlatformInitializerProperties.isAllowed() || !systemProperties.containsValue(key)) {
                cpuThreadPoolCount = null;
            } else {
                cpuThreadPoolCount = systemProperties.getInteger(key);
            }
            if (cpuThreadPoolCount == null || cpuThreadPoolCount <= 0) {
                cpuThreadPoolCount = Runtime.getRuntime().availableProcessors();
                if (PlatformInitializerProperties.isAllowed()) {
                    systemProperties.setInteger(key, cpuThreadPoolCount);
                }
            }
            return cpuThreadPoolCount;
        } catch (final Throwable t) {
            //webstart safety for access control
            PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
            return Runtime.getRuntime().availableProcessors();
        }
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
