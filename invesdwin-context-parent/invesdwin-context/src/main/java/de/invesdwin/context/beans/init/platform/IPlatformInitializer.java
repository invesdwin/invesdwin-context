package de.invesdwin.context.beans.init.platform;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import org.springframework.core.io.Resource;

import de.invesdwin.util.time.duration.Duration;

public interface IPlatformInitializer {

    void initInstrumentation();

    void initDefaultUncaughtExceptionHandler(UncaughtExceptionHandler handler);

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

    File initTempClasspathDirectory(File tempDirectory);

    void createDirectoryIfAllowed(File dir);

    void addDirectoryToSystemClassLoaderIfAllowed(File dir);

    File initHomeDirectory(String systemHomeDirectory, boolean isTestEnvironment);

    File initHomeDataDirectory(File homeDirectory, boolean isTestEnvironment);

    File initLogDirectory(boolean isTestEnvironment, File fallbackWorkDirectory);

    File initCacheDirectory(File fallbackWorkDirectory);

    Resource initSystemPropertiesResource();

    void initUiManager();

    void registerTypesForSerialization();

    void initClassPathScanner();

    void initConscryptSecurityProvider();

    void initCryptoPolicyUnlimited();

    void initAgronaBoundsChecks();

}
