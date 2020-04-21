package de.invesdwin.context.beans.init.platform;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.UIManager;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.context.beans.init.platform.util.AspectJWeaverIncludesConfigurer;
import de.invesdwin.context.beans.init.platform.util.DefaultTimeZoneConfigurer;
import de.invesdwin.context.beans.init.platform.util.RegisterTypesForSerializationConfigurer;
import de.invesdwin.context.beans.init.platform.util.internal.FileEncodingChecker;
import de.invesdwin.context.beans.init.platform.util.internal.InstrumentationHookLoader;
import de.invesdwin.context.beans.init.platform.util.internal.LogbackConfigurationLoader;
import de.invesdwin.context.beans.init.platform.util.internal.SystemPropertiesLoader;
import de.invesdwin.context.beans.init.platform.util.internal.XmlTransformerConfigurer;
import de.invesdwin.context.beans.init.platform.util.internal.protocols.ProtocolRegistration;
import de.invesdwin.context.jcache.CacheBuilder;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.instrument.DynamicInstrumentationLoader;
import de.invesdwin.instrument.DynamicInstrumentationProperties;
import de.invesdwin.instrument.DynamicInstrumentationReflections;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.classpath.ClassPathScanner;
import de.invesdwin.util.classpath.FastClassPathScanner;
import de.invesdwin.util.lang.Files;
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
        new CacheBuilder<Object, Object>().withMaximumSize(1000000)
                .withName(defaultCacheName)
                .withExpireAfterAccess(new Duration(2, FTimeUnit.MINUTES))
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
        String home = systemHomeDirectory;
        if (isTestEnvironment) {
            home = ".";
        }
        final File homeDir = new File(home, ".invesdwin");
        createDirectoryIfAllowed(homeDir);
        return homeDir;
    }

    @Override
    public File initLogDirectory(final boolean isTestEnvironment, final File fallbackWorkDirectory) {
        String logDirSr = "log";
        if (!isTestEnvironment) {
            //Productive logs should not mix each other between processes
            logDirSr += "/";
            logDirSr += PlatformInitializerProperties.START_OF_APPLICATION_CLOCK_TIME.toString("yyyyMMddHHmmss");
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
    public Resource initSystemPropertiesResource() {
        return new FileSystemResource(new File(ContextProperties.getHomeDirectory(), "system.properties"));
    }

    @Override
    public void initUiManager() {
        //prevent race condition in JFreeChart when UIManager initialized by multiple threads at the same time
        UIManager.getColor("Panel.background");
    }

    @Override
    public void registerTypesForSerialization() {
        new RegisterTypesForSerializationConfigurer().registerTypesForSerialization();
    }

    @Override
    public void initClassPathScanner() {
        //filter out test classes to prevent issues with class not found or resource not found in production
        FastClassPathScanner.addBlacklistPath("de/invesdwin/*Test");
        FastClassPathScanner.addBlacklistPath("de/invesdwin/*Stub");
        FastClassPathScanner.addBlacklistPath("de/invesdwin/*/test/*");
        for (final String basePackage : ContextProperties.getBasePackages()) {
            FastClassPathScanner.addWhitelistPath(basePackage.replace(".", "/"));
        }

        final List<TypeFilter> defaultExcludeFilters = new ArrayList<TypeFilter>();
        //filter out test classes to prevent issues with class not found or resource not found in production
        defaultExcludeFilters.add(new RegexPatternTypeFilter(Pattern.compile("de\\.invesdwin\\..*(Test|Stub)")));
        defaultExcludeFilters.add(new RegexPatternTypeFilter(Pattern.compile("de\\.invesdwin\\..*\\.test\\..*")));
        ClassPathScanner.setDefaultExcludeFilters(defaultExcludeFilters);
    }

}
