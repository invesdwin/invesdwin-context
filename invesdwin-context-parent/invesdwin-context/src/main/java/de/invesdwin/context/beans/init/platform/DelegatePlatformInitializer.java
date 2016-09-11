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
    public void initSystemPropertiesLoader() {
        delegate.initSystemPropertiesLoader();
    }

    @Override
    public void initEhcacheSystemProperties(final File ehcacheDiskStoreDirectory) {
        delegate.initEhcacheSystemProperties(ehcacheDiskStoreDirectory);
    }

    @Override
    public void initDefaultTimeoutSystemProperties(final Duration duration) {
        delegate.initDefaultTimeoutSystemProperties(duration);
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
    public File initLogDirectory(final boolean isTestEnvironment) {
        return delegate.initLogDirectory(isTestEnvironment);
    }

    @Override
    public File initCacheDirectory() {
        return delegate.initCacheDirectory();
    }

    @Override
    public Resource initSystemPropertiesResource() {
        return delegate.initSystemPropertiesResource();
    }

}
