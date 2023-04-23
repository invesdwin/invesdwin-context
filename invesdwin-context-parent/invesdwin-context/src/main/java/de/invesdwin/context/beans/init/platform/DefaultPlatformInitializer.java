package de.invesdwin.context.beans.init.platform;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.management.ManagementFactory;
import java.net.URI;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.concurrent.UnsafeBuffer;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.context.beans.init.platform.util.AmazonCorrettoSecurityProviderConfigurer;
import de.invesdwin.context.beans.init.platform.util.AspectJWeaverIncludesConfigurer;
import de.invesdwin.context.beans.init.platform.util.BouncyCastleSecurityProviderConfigurer;
import de.invesdwin.context.beans.init.platform.util.ClassPathScannerConfigurer;
import de.invesdwin.context.beans.init.platform.util.ConscryptSecurityProviderConfigurer;
import de.invesdwin.context.beans.init.platform.util.CryptoPolicyConfigurer;
import de.invesdwin.context.beans.init.platform.util.DefaultTimeZoneConfigurer;
import de.invesdwin.context.beans.init.platform.util.RegisterTypesForSerializationConfigurer;
import de.invesdwin.context.beans.init.platform.util.TempDirectoryLockConfigurerer;
import de.invesdwin.context.beans.init.platform.util.WildflyOpenSslSecurityProviderConfigurer;
import de.invesdwin.context.beans.init.platform.util.internal.FileEncodingChecker;
import de.invesdwin.context.beans.init.platform.util.internal.InstrumentationHookLoader;
import de.invesdwin.context.beans.init.platform.util.internal.LogbackConfigurationLoader;
import de.invesdwin.context.beans.init.platform.util.internal.SystemPropertiesLoader;
import de.invesdwin.context.beans.init.platform.util.internal.XmlTransformerConfigurer;
import de.invesdwin.context.beans.init.platform.util.internal.protocols.ProtocolRegistration;
import de.invesdwin.context.jcache.CacheBuilder;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.instrument.DynamicInstrumentationLoader;
import de.invesdwin.instrument.DynamicInstrumentationProperties;
import de.invesdwin.instrument.DynamicInstrumentationReflections;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.lock.FileChannelLock;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

/**
 * You can override this class and disable individual methods to skip specific invesdwin initialization features.
 * 
 * @author subes
 *
 */
@NotThreadSafe
public class DefaultPlatformInitializer implements IPlatformInitializer {

    @Override
    public void initInstrumentation() {
        AspectJWeaverIncludesConfigurer.configure();
        DynamicInstrumentationLoader.waitForInitialized();
        Assertions.assertThat(DynamicInstrumentationLoader.initLoadTimeWeavingContext()).isNotNull();
        InstrumentationHookLoader.runInstrumentationHooks();
    }

    @Override
    public void initDefaultUncaughtExceptionHandler(final UncaughtExceptionHandler handler) {
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    @Override
    public void initFileEncodingChecker() {
        FileEncodingChecker.check();
    }

    @Override
    public void initDefaultTimezoneConfigurer() {
        DefaultTimeZoneConfigurer.configure();
    }

    @Override
    public void initProtocolRegistration() {
        ProtocolRegistration.register();
    }

    @Override
    public void initXmlTransformerConfigurer() {
        XmlTransformerConfigurer.configure();
    }

    @Override
    public void initLogbackConfigurationLoader() {
        LogbackConfigurationLoader.loadLogbackConfiguration();
    }

    @Override
    public void initJavaUtilPrefsBackingStoreDirectory() {
        //https://stackoverflow.com/questions/2027566/java-util-prefs-throwing-backingstoreexception-why
        //CHECKSTYLE:OFF
        System.setProperty("java.util.prefs.userRoot", ContextProperties.getHomeDirectory().getAbsolutePath());
        System.setProperty("java.util.prefs.systemRoot", ContextProperties.getHomeDirectory().getAbsolutePath());
        try {
            Files.forceMkdir(new File(ContextProperties.getHomeDirectory(), ".java/.userPrefs"));
            Files.forceMkdir(new File(ContextProperties.getHomeDirectory(), ".systemPrefs"));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        //CHECKSTYLE:ON
    }

    @Override
    public void initSystemPropertiesLoader() {
        SystemPropertiesLoader.loadSystemProperties();
    }

    @Override
    public void initDefaultCache(final String defaultCacheName) {
        //yo dawg, JCache caches "caches" internally
        new CacheBuilder<Object, Object>().setMaximumSize(1000000)
                .setName(defaultCacheName)
                .setExpireAfterAccess(new Duration(2, FTimeUnit.MINUTES))
                .getOrCreate();
    }

    @Override
    public void initDefaultNetworkTimeoutSystemProperties(final Duration duration) {
        //CHECKSTYLE:OFF
        System.setProperty("sun.net.client.defaultConnectTimeout",
                String.valueOf(duration.intValue(FTimeUnit.MILLISECONDS)));
        System.setProperty("sun.net.client.defaultReadTimeout",
                String.valueOf(duration.intValue(FTimeUnit.MILLISECONDS)));
        //CHECKSTYLE:ON
    }

    @Override
    public boolean initIsTestEnvironment() {
        try {
            //since java classes are packaged in src/main/java, we check if the test directory exists
            final File srcTestJavaDir = new File("src/test/java");
            return srcTestJavaDir.exists() && srcTestJavaDir.isDirectory();
        } catch (final Throwable t) {
            //access control might prevent access to files/folders in a webstart environment
            return false;
        }
    }

    @Override
    public File initTempDirectory() {
        return DynamicInstrumentationProperties.USER_TEMP_DIRECTORY;
    }

    @Override
    public void initDeleteTempDirectoryRunner(final File tempDirectory, final FileChannelLock tempDirectoryLock) {
        DynamicInstrumentationProperties.setDeleteTempDirectoryRunner(() -> {
            tempDirectoryLock.unlock();
            Files.deleteNative(tempDirectory);
        });
        TempDirectoryLockConfigurerer.deleteObsoleteTempDirectories(tempDirectory);
    }

    @Override
    public FileChannelLock initTempDirectoryLock(final File tempDirectory) {
        final FileChannelLock lock = new FileChannelLock(
                new File(tempDirectory, TempDirectoryLockConfigurerer.TEMP_DIRECTORY_LOCK_FILE_NAME));
        lock.tryLockThrowing();
        return lock;
    }

    @Override
    public File initTempClasspathDirectory(final File tempDirectory) {
        final File tempClasspathDir = new File(tempDirectory, "cp");
        createDirectoryIfAllowed(tempClasspathDir);
        addDirectoryToSystemClassLoaderIfAllowed(tempClasspathDir);
        return tempClasspathDir;
    }

    @Override
    public void createDirectoryIfAllowed(final File dir) {
        if (PlatformInitializerProperties.isAllowed()) {
            try {
                Files.forceMkdir(dir);
            } catch (final IOException e) {
                throw Err.process(e);
            }
        }
    }

    @Override
    public void addDirectoryToSystemClassLoaderIfAllowed(final File dir) {
        if (PlatformInitializerProperties.isAllowed()) {
            DynamicInstrumentationReflections.addPathToSystemClassLoader(dir);
        }
    }

    @Override
    public File initHomeDirectory(final String systemHomeDirectory, final boolean isTestEnvironment) {
        final String home;
        if (isTestEnvironment) {
            //use project root
            home = ".";
        } else {
            home = systemHomeDirectory;
        }
        final File homeDir = new File(home, ".invesdwin");
        createDirectoryIfAllowed(homeDir);
        return homeDir;
    }

    @Override
    public File initHomeDataDirectory(final File homeDirectory, final boolean isTestEnvironment) {
        final File homeDataDir;
        if (isTestEnvironment) {
            //stick to project root
            homeDataDir = homeDirectory;
        } else {
            final SystemProperties systemProperties = new SystemProperties(ContextProperties.class);
            final String key = "HOME_DATA_DIR_OVERRIDE";
            if (systemProperties.containsValue(key)) {
                homeDataDir = systemProperties.getFile(key);
            } else {
                homeDataDir = homeDirectory;
            }
        }
        createDirectoryIfAllowed(homeDataDir);
        return homeDataDir;
    }

    @Override
    public File initLogDirectory(final boolean isTestEnvironment, final File fallbackWorkDirectory) {
        String logDirSr = "log";
        if (!isTestEnvironment) {
            //Productive logs should not mix each other between processes
            logDirSr += "/";
            logDirSr += new FDate(PlatformInitializerProperties.START_OF_APPLICATION_CLOCK_TIME_MILLIS)
                    .toString("yyyyMMddHHmmss");
            logDirSr += "_";
            logDirSr += ManagementFactory.getRuntimeMXBean().getName();
        }
        return createDirectoryWithFallback(logDirSr, fallbackWorkDirectory);
    }

    private File createDirectoryWithFallback(final String dirStr, final File fallbackWorkDirectory) {
        try {
            final File logDir = new File(dirStr);
            createDirectoryIfAllowed(logDir);
            return logDir;
        } catch (final Throwable t) {
            final File fallbackLogDir = new File(fallbackWorkDirectory, dirStr);
            createDirectoryIfAllowed(fallbackLogDir);
            return fallbackLogDir;
        }
    }

    @Override
    public File initCacheDirectory(final File fallbackWorkDirectory) {
        final String cacheDirStr = "cache";
        return createDirectoryWithFallback(cacheDirStr, fallbackWorkDirectory);
    }

    @Override
    public URI initSystemPropertiesUri() {
        return new File(ContextProperties.getHomeDirectory(), "system.properties").toURI();
    }

    @Override
    public void registerTypesForSerialization() {
        new RegisterTypesForSerializationConfigurer().registerTypesForSerialization();
    }

    @Override
    public void initClassPathScanner() {
        ClassPathScannerConfigurer.configure();
    }

    /**
     * make okhttp and jetty use the faster SSL provider
     * 
     * https://github.com/square/okhttp
     */
    @Override
    public void initConscryptSecurityProvider() {
        //conscrypt dependency can be added/removed if desired
        if (Reflections.classExists(ConscryptSecurityProviderConfigurer.CONSCRYPT_CLASS)) {
            ConscryptSecurityProviderConfigurer.configure();
        }
    }

    @Override
    public void initAmazonCorrettoSecurityProvider() {
        //corretto dependency can be added/removed if desired
        if (Reflections.classExists(AmazonCorrettoSecurityProviderConfigurer.AMAZON_CORRETTO_CRYPTO_PROVIDER_CLASS)) {
            AmazonCorrettoSecurityProviderConfigurer.configure();
        }
    }

    @Override
    public void initWildflyOpenSslSecurityProvider() {
        //wildfly dependency can be added/removed if desired
        if (Reflections.classExists(WildflyOpenSslSecurityProviderConfigurer.WILDFLY_OPENSSL_SECURITY_PROVIDER_CLASS)) {
            WildflyOpenSslSecurityProviderConfigurer.configure();
        }
    }

    @Override
    public void initBouncyCastleSecurityProvider() {
        //bouncycastle dependency can be added/removed if desired
        if (Reflections.classExists(BouncyCastleSecurityProviderConfigurer.BOUNCY_CASTLE_PROVIDER_CLASS)) {
            BouncyCastleSecurityProviderConfigurer.configure();
        }
    }

    /**
     * Might be needed in some circumstances: https://www.baeldung.com/java-bouncy-castle
     */
    @Override
    public void initCryptoPolicyUnlimited() {
        CryptoPolicyConfigurer.configure();
    }

    @Override
    public void initAgronaBoundsChecks() {
        if (Throwables.isDebugStackTraceEnabled()) {
            //CHECKSTYLE:OFF
            final String key = "agrona.disable.bounds.checks";
            System.setProperty(key, "false");
            //CHECKSTYLE:ON
            Assertions.checkEquals(UnsafeBuffer.DISABLE_BOUNDS_CHECKS_PROP_NAME, key);
            Assertions.checkTrue(UnsafeBuffer.SHOULD_BOUNDS_CHECK);
        }
    }

    @Override
    public void initNettyBoundsChecks() {
        if (Throwables.isDebugStackTraceEnabled()) {
            //CHECKSTYLE:OFF
            final String key = "io.netty.buffer.checkBounds";
            System.setProperty(key, "true");
            //CHECKSTYLE:ON
        }
    }

    @Override
    public void initDisableJavaModuleSystemRestrictions() {
        Reflections.disableJavaModuleSystemRestrictions();
    }

}
