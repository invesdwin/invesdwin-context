package de.invesdwin.context.beans.init.platform;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;

import de.invesdwin.util.concurrent.lock.FileChannelLock;
import de.invesdwin.util.time.duration.Duration;

public interface IPlatformInitializer {

    void initInstrumentation();

    void initBeanPathCollectionsProvider();

    void initFstDeepCloneProvider();

    void initDefaultUncaughtExceptionHandler(UncaughtExceptionHandler handler);

    void initDefaultExecutorExceptionHandler();

    void initFileEncodingChecker();

    void initDefaultTimezoneConfigurer();

    void initProtocolRegistration();

    void initXmlTransformerConfigurer();

    void initLogbackConfigurationLoader();

    void initJavaUtilPrefsBackingStoreDirectory();

    void initSystemPropertiesLoader();

    void initDefaultCache(String defaultCacheName);

    void initDefaultNetworkTimeoutSystemProperties(Duration duration);

    boolean initIsTestEnvironment();

    File initTempDirectory();

    FileChannelLock initTempDirectoryLock(File tempDirectory);

    File initJavaIoTmpdirRedirect(File tempDirectory);

    void initDeleteTempDirectoryRunner(File tempDirectory, FileChannelLock tempDirectoryLock);

    File initTempClasspathDirectory(File tempDirectory);

    void createDirectoryIfAllowed(File dir);

    void addDirectoryToSystemClassLoaderIfAllowed(File dir);

    File initHomeDirectory(String systemHomeDirectory, boolean isTestEnvironment);

    File initHomeDataDirectory(File homeDirectory, boolean isTestEnvironment);

    File initLogDirectory(boolean isTestEnvironment, File fallbackWorkDirectory);

    File initCacheDirectory(File fallbackWorkDirectory);

    URI initSystemPropertiesUri();

    void registerTypesForSerialization();

    void initClassPathScanner();

    void initConscryptSecurityProvider();

    void initAmazonCorrettoSecurityProvider();

    void initWildflyOpenSslSecurityProvider();

    void initBouncyCastleSecurityProvider();

    void initCryptoPolicyUnlimited();

    void initAgronaBoundsChecks();

    void initNettyBoundsChecks();

    void initDisableJavaModuleSystemRestrictions();

}
