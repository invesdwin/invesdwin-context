package de.invesdwin.context;

import java.io.File;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.BooleanUtils;

import de.invesdwin.context.beans.init.platform.IPlatformInitializer;
import de.invesdwin.context.beans.init.platform.util.internal.BasePackagesConfigurer;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.lock.FileChannelLock;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.lang.uri.connect.InputStreamHttpResponseConsumer;
import de.invesdwin.util.math.Booleans;
import de.invesdwin.util.swing.HiDPI;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public final class ContextProperties {

    public static final String DEFAULT_CACHE_NAME = "default";

    public static final boolean IS_TEST_ENVIRONMENT;

    /**
     * Process specific temp dir that gets cleaned on exit.
     */
    public static final File TEMP_DIRECTORY;
    /**
     * Where java.io.tmpdir is located.
     */
    public static final File TEMP_DIRECTORY_JAVA;
    public static final FileChannelLock TEMP_DIRECTORY_LOCK;
    public static final File TEMP_CLASSPATH_DIRECTORY;
    public static final Duration DEFAULT_NETWORK_TIMEOUT;
    public static final int DEFAULT_NETWORK_TIMEOUT_MILLIS;
    public static final int CPU_THREAD_POOL_COUNT;
    public static final String USER_NAME;
    public static final boolean KEEP_JDK_DEEP_CLONE_PROVIDER;

    @GuardedBy("ContextProperties.class")
    private static File cacheDirectory;
    @GuardedBy("ContextProperties.class")
    private static File homeDirectory;
    @GuardedBy("ContextProperties.class")
    private static File homeDataDirectory;
    @GuardedBy("ContextProperties.class")
    private static File logDirectory;

    static {
        final IPlatformInitializer initializer = PlatformInitializerProperties.getInitializer();
        IS_TEST_ENVIRONMENT = initializer.initIsTestEnvironment();
        if (PlatformInitializerProperties.isAllowed()) {
            try {
                Assertions.checkNotNull(Err.class);
                initializer.initXmlTransformerConfigurer();
                initializer.initLogbackConfigurationLoader();
                initializer.initSystemPropertiesLoader();
            } catch (final Throwable t) {
                PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
            }
        }

        File tempDirectory = null;
        FileChannelLock tempDirectoryLock = null;
        File tempDirectoryJava = null;
        File tempClasspathDirectory = null;
        if (PlatformInitializerProperties.isAllowed()) {
            try {
                tempDirectory = initializer.initTempDirectory();
                tempDirectoryLock = initializer.initTempDirectoryLock(tempDirectory);
                tempDirectoryJava = initializer.initJavaIoTmpdirRedirect(tempDirectory);
                tempClasspathDirectory = initializer.initTempClasspathDirectory(tempDirectory);
                initializer.initDeleteTempDirectoryRunner(tempDirectory, tempDirectoryLock);
            } catch (final Throwable t) {
                //webstart safety for access control
                tempDirectory = null;
                tempDirectoryLock = null;
                tempClasspathDirectory = null;
                PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
            }
        }
        TEMP_DIRECTORY = tempDirectory;
        TEMP_DIRECTORY_LOCK = tempDirectoryLock;
        TEMP_DIRECTORY_JAVA = tempDirectoryJava;
        TEMP_CLASSPATH_DIRECTORY = tempClasspathDirectory;

        if (PlatformInitializerProperties.isAllowed()) {
            try {
                initializer.initJavaUtilPrefsBackingStoreDirectory();
                initializer.initDefaultCache(DEFAULT_CACHE_NAME);
            } catch (final Throwable t) {
                PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
            }
        }

        DEFAULT_NETWORK_TIMEOUT = readDefaultNetworkTimeout();
        initSecurityProviders(initializer);
        URIs.setDefaultNetworkTimeout(DEFAULT_NETWORK_TIMEOUT);
        InputStreamHttpResponseConsumer
                .setDefaultTempDir(new File(TEMP_DIRECTORY, InputStreamHttpResponseConsumer.class.getSimpleName()));
        DEFAULT_NETWORK_TIMEOUT_MILLIS = ContextProperties.DEFAULT_NETWORK_TIMEOUT.intValue(FTimeUnit.MILLISECONDS);
        CPU_THREAD_POOL_COUNT = readCpuThreadPoolCount();
        Executors.setCpuThreadPoolCount(CPU_THREAD_POOL_COUNT);

        final Double hiDpiScaleFactor = readHiDpiScaleFactor();
        if (hiDpiScaleFactor != null) {
            HiDPI.setScaleFactor(hiDpiScaleFactor);
        }

        initDebugStacktraces();

        USER_NAME = new SystemProperties().getString("user.name");
        KEEP_JDK_DEEP_CLONE_PROVIDER = readKeepJdkDeepCloneProvider();
    }

    private ContextProperties() {}

    private static void initSecurityProviders(final IPlatformInitializer initializer) {
        if (readWildflyOpenSslSecurityProviderEnabled()) {
            initializer.initWildflyOpenSslSecurityProvider();
        }
        if (readConscryptSecurityProviderEnabled()) {
            initializer.initConscryptSecurityProvider();
        }
        if (readAmazonCorrettoSecurityProviderEnabled()) {
            initializer.initAmazonCorrettoSecurityProvider();
        }
        if (readBouncyCastleSecurityProviderEnabled()) {
            initializer.initBouncyCastleSecurityProvider();
        }
        initializer.initCryptoPolicyUnlimited();
    }

    private static void initDebugStacktraces() {
        final SystemProperties systemProperties = new SystemProperties(Throwables.class);
        if (Booleans.isTrue(systemProperties.getBooleanOptional("DEBUG_STACK_TRACE_ENABLED"))) {
            Throwables.setDebugStackTraceEnabled(true);
        }
    }

    /**
     * Invesdwin specific home dir that gets shared between multiple processes.
     * 
     * this should be $HOME/.invesdwin
     */
    public static synchronized File getHomeDirectory() {
        if (homeDirectory == null) {
            homeDirectory = PlatformInitializerProperties.getInitializer()
                    .initHomeDirectory(getSystemHomeDirectory(), isTestEnvironmentForHomeDirectory());
        }
        return homeDirectory;
    }

    private static boolean isTestEnvironmentForHomeDirectory() {
        return IS_TEST_ENVIRONMENT && !PlatformInitializerProperties.isKeepSystemHomeDuringTests();
    }

    /**
     * Invesdwin specific home data dir that gets shared between multiple processes.
     * 
     * this should be $HOME/.invesdwin
     * 
     * Though this can be redirected/overridden to a different location.
     */
    public static synchronized File getHomeDataDirectory() {
        if (homeDataDirectory == null) {
            homeDataDirectory = PlatformInitializerProperties.getInitializer()
                    .initHomeDataDirectory(getHomeDirectory(), isTestEnvironmentForHomeDirectory());
        }
        return homeDataDirectory;
    }

    /**
     * http://stackoverflow.com/questions/585534/what-is-the-best-way-to-find-the-users-home-directory-in-java
     * 
     * this should be $HOME
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

    private static boolean readKeepJdkDeepCloneProvider() {
        final SystemProperties systemProperties = new SystemProperties(ContextProperties.class);
        final String key = "KEEP_JDK_DEEP_CLONE_PROVIDER";
        return systemProperties.getBooleanOptional(key, false);
    }

    private static boolean readConscryptSecurityProviderEnabled() {
        if (!PlatformInitializerProperties.isAllowed()) {
            return false;
        }
        final SystemProperties systemProperties = new SystemProperties(ContextProperties.class);
        final String key = "CONSCRYPT_SECURITY_PROVIDER_ENABLED";
        if (!systemProperties.containsValue(key)) {
            return true;
        } else {
            return systemProperties.getBoolean(key);
        }
    }

    private static boolean readAmazonCorrettoSecurityProviderEnabled() {
        if (!PlatformInitializerProperties.isAllowed()) {
            return false;
        }
        final SystemProperties systemProperties = new SystemProperties(ContextProperties.class);
        final String key = "AMAZON_CORRETTO_SECURITY_PROVIDER_ENABLED";
        if (!systemProperties.containsValue(key)) {
            return true;
        } else {
            return systemProperties.getBoolean(key);
        }
    }

    private static boolean readWildflyOpenSslSecurityProviderEnabled() {
        if (!PlatformInitializerProperties.isAllowed()) {
            return false;
        }
        final SystemProperties systemProperties = new SystemProperties(ContextProperties.class);
        final String key = "WILDFLY_OPENSSL_SECURITY_PROVIDER_ENABLED";
        if (!systemProperties.containsValue(key)) {
            return true;
        } else {
            return systemProperties.getBoolean(key);
        }
    }

    private static boolean readBouncyCastleSecurityProviderEnabled() {
        if (!PlatformInitializerProperties.isAllowed()) {
            return false;
        }
        final SystemProperties systemProperties = new SystemProperties(ContextProperties.class);
        final String key = "BOUNCY_CASTLE_SECURITY_PROVIDER_ENABLED";
        if (!systemProperties.containsValue(key)) {
            return true;
        } else {
            return systemProperties.getBoolean(key);
        }
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

    private static Double readHiDpiScaleFactor() {
        final SystemProperties systemProperties = new SystemProperties(ContextProperties.class);
        final String key = "HIDPI_SCALE_FACTOR";
        if (!PlatformInitializerProperties.isAllowed() || !systemProperties.containsValue(key)) {
            return null;
        } else {
            return systemProperties.getDouble(key);
        }
    }

    public static boolean isIgnoreDistributionProperties() {
        final SystemProperties systemProperties = new SystemProperties(ContextProperties.class);
        final String keyIgnoreDistributionProperties = "IGNORE_DISTRIBUTION_PROPERTIES";
        if (systemProperties.containsValue(keyIgnoreDistributionProperties)) {
            return systemProperties.getBoolean(keyIgnoreDistributionProperties);
        } else {
            return false;
        }
    }

}
