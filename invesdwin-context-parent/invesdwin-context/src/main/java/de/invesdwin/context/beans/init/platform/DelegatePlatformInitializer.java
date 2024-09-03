package de.invesdwin.context.beans.init.platform;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.concurrent.lock.FileChannelLock;
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
    public void initFstDeepCloneProvider() {
        delegate.initFstDeepCloneProvider();
    }

    @Override
    public void initDefaultUncaughtExceptionHandler(final UncaughtExceptionHandler handler) {
        delegate.initDefaultUncaughtExceptionHandler(handler);
    }

    @Override
    public void initDefaultExecutorExceptionHandler() {
        delegate.initDefaultExecutorExceptionHandler();
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
    public void initDeleteTempDirectoryRunner(final File tempDirectory, final FileChannelLock tempDirectoryLock) {
        delegate.initDeleteTempDirectoryRunner(tempDirectory, tempDirectoryLock);
    }

    @Override
    public FileChannelLock initTempDirectoryLock(final File tempDirectory) {
        return delegate.initTempDirectoryLock(tempDirectory);
    }

    @Override
    public File initJavaIoTmpdirRedirect(final File tempDirectory) {
        return delegate.initJavaIoTmpdirRedirect(tempDirectory);
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
    public URI initSystemPropertiesUri() {
        return delegate.initSystemPropertiesUri();
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
    public void initAmazonCorrettoSecurityProvider() {
        delegate.initAmazonCorrettoSecurityProvider();
    }

    @Override
    public void initWildflyOpenSslSecurityProvider() {
        delegate.initWildflyOpenSslSecurityProvider();
    }

    @Override
    public void initBouncyCastleSecurityProvider() {
        delegate.initBouncyCastleSecurityProvider();
    }

    @Override
    public void initCryptoPolicyUnlimited() {
        delegate.initCryptoPolicyUnlimited();
    }

    @Override
    public void initAgronaBoundsChecks() {
        delegate.initAgronaBoundsChecks();
    }

    @Override
    public void initNettyBoundsChecks() {
        delegate.initNettyBoundsChecks();
    }

    @Override
    public void initDisableJavaModuleSystemRestrictions() {
        delegate.initDisableJavaModuleSystemRestrictions();
    }

}
