package de.invesdwin.context.beans.init;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.management.ManagementFactory;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.FileUtils;

import de.invesdwin.context.beans.init.internal.FileEncodingChecker;
import de.invesdwin.context.beans.init.internal.InstrumentationHookLoader;
import de.invesdwin.context.beans.init.internal.LogbackConfigurationLoader;
import de.invesdwin.context.beans.init.internal.SystemPropertiesLoader;
import de.invesdwin.context.beans.init.internal.XmlTransformerConfigurer;
import de.invesdwin.context.beans.init.internal.protocols.ProtocolRegistration;
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
public class InvesdwinInitializer {

    public void initInstrumentation() {
        DynamicInstrumentationLoader.waitForInitialized();
        Assertions.assertThat(DynamicInstrumentationLoader.initLoadTimeWeavingContext()).isNotNull();
        InstrumentationHookLoader.runInstrumentationHooks();
    }

    public void initDefaultUncaughtExceptionHandler(final UncaughtExceptionHandler handler) {
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    public void initFileEncodingChecker() {
        FileEncodingChecker.check();
    }

    public void initDefaultTimezoneConfigurer() {
        DefaultTimeZoneConfigurer.configure();
    }

    public void initProtocolRegistration() {
        ProtocolRegistration.register();
    }

    public void initXmlTransformerConfigurer() {
        XmlTransformerConfigurer.configure();
    }

    public void initLogbackConfigurationLoader() {
        LogbackConfigurationLoader.loadLogbackConfiguration();
    }

    public void initSystemPropertiesLoader() {
        SystemPropertiesLoader.loadSystemProperties();
    }

    public void initEhcacheSystemProperties(final File ehcacheDiskStoreDirectory) {
        final SystemProperties systemProperties = new SystemProperties();
        systemProperties.setString("ehcache.disk.store.dir", ehcacheDiskStoreDirectory.getAbsolutePath());
        systemProperties.setString("net.sf.ehcache.enableShutdownHook", "true");
    }

    public void initDefaultTimeoutSystemProperties(final Duration duration) {
        final SystemProperties sysProps = new SystemProperties();
        sysProps.setInteger("sun.net.client.defaultConnectTimeout", duration.intValue(FTimeUnit.MILLISECONDS));
        sysProps.setInteger("sun.net.client.defaultReadTimeout", duration.intValue(FTimeUnit.MILLISECONDS));
    }

    public boolean initIsTestEnvironment() {
        //since java classes are packaged in src/main/java, we check if the test directory exists and if it actually contains any tests
        final File srcTestJavaDir = new File("src/test/java");
        final boolean testClassesExist = srcTestJavaDir.exists() && srcTestJavaDir.isDirectory()
                && srcTestJavaDir.list().length > 0;
        return testClassesExist;
    }

    public File initTempDirectory() {
        return DynamicInstrumentationProperties.TEMP_DIRECTORY;
    }

    public File initTempClasspathDirectory(final File tempDirectory) {
        final File tempClasspathDir = new File(tempDirectory, "cp");
        createDirectoryIfAllowed(tempClasspathDir);
        addDirectoryToSystemClassLoaderIfAllowed(tempClasspathDir);
        return tempClasspathDir;
    }

    public void createDirectoryIfAllowed(final File dir) {
        if (InvesdwinInitializationProperties.isInvesdwinInitializationAllowed()) {
            try {
                FileUtils.forceMkdir(dir);
            } catch (final IOException e) {
                throw Err.process(e);
            }
        }
    }

    public void addDirectoryToSystemClassLoaderIfAllowed(final File dir) {
        if (InvesdwinInitializationProperties.isInvesdwinInitializationAllowed()) {
            DynamicInstrumentationReflections.addPathToSystemClassLoader(dir);
        }
    }

    public File initHomeDirectory(final String systemHomeDirectory, final boolean isTestEnvironment) {
        String home = systemHomeDirectory;
        if (isTestEnvironment) {
            home = ".";
        }
        final File homeDir = new File(home, ".invesdwin");
        createDirectoryIfAllowed(homeDir);
        return homeDir;
    }

    public File initLogDirectory(final boolean isTestEnvironment) {
        String logDirSr = "log";
        if (!isTestEnvironment) {
            //Productive logs should not mix each other between processes
            logDirSr += "/";
            logDirSr += InvesdwinInitializationProperties.START_OF_APPLICATION_CLOCK_TIME.toString("yyyyMMddHHmmss");
            logDirSr += "_";
            logDirSr += ManagementFactory.getRuntimeMXBean().getName();
        }
        final File logDir = new File(logDirSr);
        createDirectoryIfAllowed(logDir);
        return logDir;
    }

    public File initCacheDirectory() {
        final File cacheDir = new File("cache");
        createDirectoryIfAllowed(cacheDir);
        return cacheDir;
    }

}
