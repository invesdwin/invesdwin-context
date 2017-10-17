package de.invesdwin.context;

import java.io.File;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.beans.init.platform.IPlatformInitializer;
import de.invesdwin.context.beans.init.platform.util.internal.BasePackagesConfigurer;
import de.invesdwin.context.system.OperatingSystem;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.instrument.DynamicInstrumentationProperties;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.lang.uri.URIsConnect;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@ThreadSafe
public final class ContextProperties {

    public static final String COMMON_CACHE_NAME = "common";

    public static final boolean IS_TEST_ENVIRONMENT;

    /**
     * Process specific temp dir that gets cleaned on exit.
     */
    public static final File TEMP_DIRECTORY;
    public static final File TEMP_CLASSPATH_DIRECTORY;
    public static final File EHCACHE_DISK_STORE_DIRECTORY;
    public static final Duration DEFAULT_NETWORK_TIMEOUT;
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
        File ehcacheDiskStoreDirectory = null;
        try {
            tempDirectory = initializer.initTempDirectory();
            tempClasspathDirectory = initializer.initTempClasspathDirectory(tempDirectory);
            ehcacheDiskStoreDirectory = new File(tempDirectory, "ehcache");
        } catch (final Throwable t) {
            //webstart safety for access control
            tempDirectory = null;
            tempClasspathDirectory = null;
            ehcacheDiskStoreDirectory = null;
            PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
        }
        TEMP_DIRECTORY = tempDirectory;
        TEMP_CLASSPATH_DIRECTORY = tempClasspathDirectory;
        EHCACHE_DISK_STORE_DIRECTORY = ehcacheDiskStoreDirectory;

        if (PlatformInitializerProperties.isAllowed()) {
            try {
                initializer.initXmlTransformerConfigurer();
                initializer.initLogbackConfigurationLoader();
                initializer.initSystemPropertiesLoader();

                initializer.initEhcacheSystemProperties(EHCACHE_DISK_STORE_DIRECTORY);
            } catch (final Throwable t) {
                PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
            }
        }

        DEFAULT_NETWORK_TIMEOUT = readDefaultNetworkTimeout();
        URIsConnect.setDefaultNetworkTimeout(DEFAULT_NETWORK_TIMEOUT);
        CPU_THREAD_POOL_COUNT = readCpuThreadPoolCount();
        Executors.setCpuThreadPoolCount(CPU_THREAD_POOL_COUNT);
    }

    private ContextProperties() {}

    /**
     * Invesdwin specific home dir that gets shared between multiple processes.
     * 
     * this should be $HOME/.invesdwin
     */
    public static synchronized File getHomeDirectory() {
        if (homeDirectory == null) {
            homeDirectory = PlatformInitializerProperties.getInitializer().initHomeDirectory(getSystemHomeDirectory(),
                    IS_TEST_ENVIRONMENT && !PlatformInitializerProperties.isKeepSystemHomeDuringTests());
            if (PlatformInitializerProperties.isKeepSystemHomeDuringTests()) {
                ContextDirectoriesStub.addProtectedDirectory(homeDirectory);
            }
        }
        return homeDirectory;
    }

    /**
     * http://stackoverflow.com/questions/585534/what-is-the-best-way-to-find-the-users-home-directory-in-java
     * 
     * this should $HOME
     */
    public static String getSystemHomeDirectory() {
        final String[] envs;
        if (OperatingSystem.isWindows()) {
            envs = new String[] { "HOMEDRIVE", "HOMEPATH", "USERPROFILE" };
        } else {
            envs = new String[] { "HOME" };
        }
        String home = null;
        for (final String env : envs) {
            final String envHome = System.getenv(env);
            if (Strings.isNotBlank(envHome)) {
                home = envHome;
                break;
            }
        }
        if (Strings.isBlank(home)) {
            home = new SystemProperties().getString("user.home");
        }
        return home;
    }

    public static synchronized File getLogDirectory() {
        if (logDirectory == null) {
            logDirectory = PlatformInitializerProperties.getInitializer().initLogDirectory(IS_TEST_ENVIRONMENT);
        }
        return logDirectory;
    }

    /**
     * Cache dir that holds information over multiple JVM instances.
     */
    public static synchronized File getCacheDirectory() {
        if (cacheDirectory == null) {
            cacheDirectory = PlatformInitializerProperties.getInitializer().initCacheDirectory();
        }
        return cacheDirectory;
    }

    public static synchronized Set<String> getBasePackages() {
        return BasePackagesConfigurer.getBasePackages();
    }

    private static Duration readDefaultNetworkTimeout() {
        final SystemProperties systemProperties = new SystemProperties(ContextProperties.class);
        final String key = "DEFAULT_NETWORK_TIMEOUT";
        final Duration duration;
        if (!PlatformInitializerProperties.isAllowed() || !systemProperties.containsValue(key)) {
            //default to 30 seconds if for some reason the properties were not loaded
            duration = new Duration(30, FTimeUnit.SECONDS);
        } else {
            duration = systemProperties.getDuration(key);
            //So that Spring-WS also respects the timeouts...
            PlatformInitializerProperties.getInitializer().initDefaultTimeoutSystemProperties(duration);
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

    public static String getProcessId() {
        return DynamicInstrumentationProperties.getProcessId();
    }

}
