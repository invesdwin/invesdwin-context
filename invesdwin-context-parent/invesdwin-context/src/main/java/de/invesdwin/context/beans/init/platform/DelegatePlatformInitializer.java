package de.invesdwin.context.beans.init.platform;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.Resource;

import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class DelegatePlatformInitializer implements IPlatformInitializer {

    protected IPlatformInitializer delegate;

    public DelegatePlatformInitializer(final IPlatformInitializer delegate) {
        this.delegate = delegate;
    }

    @Override
    public void initInstrumentation() {
        delegate.initInstrumentation();
    }

    @Override
    public void initDefaultUncaughtExceptionHandler(final UncaughtExceptionHandler handler) {
        delegate.initDefaultUncaughtExceptionHandler(handler);
    }

    @Override
    public void initFileEncodingChecker() {
        delegate.initFileEncodingChecker();
    }

    @Override
    public void initDefaultTimezoneConfigurer() {
        delegate.initDefaultTimezoneConfigurer();
    }

    @Override
    public void initProtocolRegistration() {
        delegate.initProtocolRegistration();
    }

    @Override
    public void initXmlTransformerConfigurer() {
        delegate.initXmlTransformerConfigurer();
    }

    @Override
    public void initLogbackConfigurationLoader() {
        delegate.initLogbackConfigurationLoader();
    }

    @Override
    public void initJavaUtilPrefsBackingStoreDirectory() {
        delegate.initJavaUtilPrefsBackingStoreDirectory();
    }

    @Override
    public void initSystemPropertiesLoader() {
        delegate.initSystemPropertiesLoader();
    }

    @Override
    public void initDefaultCache(final String defaultCacheName) {
        delegate.initDefaultCache(defaultCacheName);
    }

    @Override
    public void initDefaultNetworkTimeoutSystemProperties(final Duration duration) {
        delegate.initDefaultNetworkTimeoutSystemProperties(duration);
    }

    @Override
    public boolean initIsTestEnvironment() {
        return delegate.initIsTestEnvironment();
    }

    @Override
    public File initTempDirectory() {
        return delegate.initTempDirectory();
    }

    @Override
    public File initTempClasspathDirectory(final File tempDirectory) {
        return delegate.initTempClasspathDirectory(tempDirectory);
    }

    @Override
    public void createDirectoryIfAllowed(final File dir) {
        delegate.createDirectoryIfAllowed(dir);
    }

    @Override
    public void addDirectoryToSystemClassLoaderIfAllowed(final File dir) {
        delegate.addDirectoryToSystemClassLoaderIfAllowed(dir);
    }

    @Override
    public File initHomeDirectory(final String systemHomeDirectory, final boolean isTestEnvironment) {
        return delegate.initHomeDirectory(systemHomeDirectory, isTestEnvironment);
    }

    @Override
    public File initHomeDataDirectory(final File homeDirectory, final boolean isTestEnvironment) {
        return delegate.initHomeDataDirectory(homeDirectory, isTestEnvironment);
    }

    @Override
    public File initLogDirectory(final boolean isTestEnvironment, final File fallbackWorkDirectory) {
        return delegate.initLogDirectory(isTestEnvironment, fallbackWorkDirectory);
    }

    @Override
    public File initCacheDirectory(final File fallbackWorkDirectory) {
        return delegate.initCacheDirectory(fallbackWorkDirectory);
    }

    @Override
    public Resource initSystemPropertiesResource() {
        return delegate.initSystemPropertiesResource();
    }

    @Override
    public void initUiManager() {
        delegate.initUiManager();
    }

    @Override
    public void registerTypesForSerialization() {
        delegate.registerTypesForSerialization();
    }

    @Override
    public void initClassPathScanner() {
        delegate.initClassPathScanner();
    }

    @Override
    public void initConscryptSecurityProvider() {
        delegate.initConscryptSecurityProvider();
    }

    @Override
    public void initCryptoPolicyUnlimited() {
        delegate.initCryptoPolicyUnlimited();
    }

}
