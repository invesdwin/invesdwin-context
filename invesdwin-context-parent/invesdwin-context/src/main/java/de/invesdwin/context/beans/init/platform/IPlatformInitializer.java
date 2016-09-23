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

    void initSystemPropertiesLoader();

    void initEhcacheSystemProperties(File ehcacheDiskStoreDirectory);

    void initDefaultTimeoutSystemProperties(Duration duration);

    boolean initIsTestEnvironment();

    File initTempDirectory();

    File initTempClasspathDirectory(File tempDirectory);

    void createDirectoryIfAllowed(File dir);

    void addDirectoryToSystemClassLoaderIfAllowed(File dir);

    File initHomeDirectory(String systemHomeDirectory, boolean isTestEnvironment);

    File initLogDirectory(boolean isTestEnvironment);

    File initCacheDirectory();

    Resource initSystemPropertiesResource();

}