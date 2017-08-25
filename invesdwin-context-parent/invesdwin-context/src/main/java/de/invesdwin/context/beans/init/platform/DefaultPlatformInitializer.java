package de.invesdwin.context.beans.init.platform;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.management.ManagementFactory;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.context.beans.init.platform.util.DefaultTimeZoneConfigurer;
import de.invesdwin.context.beans.init.platform.util.internal.FileEncodingChecker;
import de.invesdwin.context.beans.init.platform.util.internal.InstrumentationHookLoader;
import de.invesdwin.context.beans.init.platform.util.internal.LogbackConfigurationLoader;
import de.invesdwin.context.beans.init.platform.util.internal.SystemPropertiesLoader;
import de.invesdwin.context.beans.init.platform.util.internal.XmlTransformerConfigurer;
import de.invesdwin.context.beans.init.platform.util.internal.protocols.ProtocolRegistration;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.instrument.DynamicInstrumentationLoader;
import de.invesdwin.instrument.DynamicInstrumentationProperties;
import de.invesdwin.instrument.DynamicInstrumentationReflections;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

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
    public void initSystemPropertiesLoader() {
        SystemPropertiesLoader.loadSystemProperties();
    }

    @Override
    public void initEhcacheSystemProperties(final File ehcacheDiskStoreDirectory) {
        final SystemProperties systemProperties = new SystemProperties();
        systemProperties.setString("ehcache.disk.store.dir", ehcacheDiskStoreDirectory.getAbsolutePath());
        systemProperties.setString("net.sf.ehcache.enableShutdownHook", "true");
    }

    @Override
    public void initDefaultTimeoutSystemProperties(final Duration duration) {
        final SystemProperties sysProps = new SystemProperties();
        sysProps.setInteger("sun.net.client.defaultConnectTimeout", duration.intValue(FTimeUnit.MILLISECONDS));
        sysProps.setInteger("sun.net.client.defaultReadTimeout", duration.intValue(FTimeUnit.MILLISECONDS));
    }

    @Override
    public boolean initIsTestEnvironment() {
        try {
            //since java classes are packaged in src/main/java, we check if the test directory exists and if it actually contains any tests
            final File srcTestJavaDir = new File("src/test/java");
            final boolean testClassesExist = srcTestJavaDir.exists() && srcTestJavaDir.isDirectory()
                    && srcTestJavaDir.list().length > 0;
            return testClassesExist;
        } catch (final Throwable t) {
            //access control might prevent access to files/folders in a webstart environment
            return false;
        }
    }

    @Override
    public File initTempDirectory() {
        return DynamicInstrumentationProperties.TEMP_DIRECTORY;
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
                FileUtils.forceMkdir(dir);
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
        String home = systemHomeDirectory;
        if (isTestEnvironment) {
            home = ".";
        }
        final File homeDir = new File(home, ".invesdwin");
        createDirectoryIfAllowed(homeDir);
        return homeDir;
    }

    @Override
    public File initLogDirectory(final boolean isTestEnvironment) {
        String logDirSr = "log";
        if (!isTestEnvironment) {
            //Productive logs should not mix each other between processes
            logDirSr += "/";
            logDirSr += PlatformInitializerProperties.START_OF_APPLICATION_CLOCK_TIME.toString("yyyyMMddHHmmss");
            logDirSr += "_";
            logDirSr += ManagementFactory.getRuntimeMXBean().getName();
        }
        final File logDir = new File(logDirSr);
        createDirectoryIfAllowed(logDir);
        return logDir;
    }

    @Override
    public File initCacheDirectory() {
        final File cacheDir = new File("cache");
        createDirectoryIfAllowed(cacheDir);
        return cacheDir;
    }

    @Override
    public Resource initSystemPropertiesResource() {
        return new FileSystemResource(new File(ContextProperties.getHomeDirectory(), "system.properties"));
    }

    @Override
    public void initUiManager() {
        //prevent race condition in JFreeChart when UIManager initialized by multiple threads at the same time
        UIManager.getColor("Panel.background");
    }

}
