package de.invesdwin.context;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import de.invesdwin.context.beans.init.InvesdwinInitializationProperties;
import de.invesdwin.context.beans.init.internal.LogbackConfigurationLoader;
import de.invesdwin.context.beans.init.internal.SystemPropertiesLoader;
import de.invesdwin.context.beans.init.internal.XmlTransformerConfigurer;
import de.invesdwin.context.beans.init.locations.IBasePackageDefinition;
import de.invesdwin.context.log.Log;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.system.OperatingSystem;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.instrument.DynamicInstrumentationProperties;
import de.invesdwin.instrument.DynamicInstrumentationReflections;
import de.invesdwin.norva.marker.ISerializableValueObject;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.classpath.ClassPathScanner;
import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.lang.Reflections;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.lang.uri.URIsConnect;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@ThreadSafe
public final class ContextProperties {

    public static final String COMMON_CACHE_NAME = "common";

    public static final boolean IS_TEST_ENVIRONMENT;

    /**
     * Process specific temp dir that gets cleaned on exit.
     */
    public static final File TEMP_DIRECTORY;
    public static final File TEMP_CLASSPATH_DIRECTORY;
    public static final File EHCACHE_DISK_STORE_DIRECTORY;
    public static final Duration DEFAULT_NETWORK_TIMEOUT;
    @GuardedBy("this.class")
    private static File cacheDirectory;
    @GuardedBy("this.class")
    private static File homeDirectory;
    @GuardedBy("this.class")
    private static File logDirectory;
    @GuardedBy("this.class")
    private static Set<String> basePackages;

    static {
        IS_TEST_ENVIRONMENT = determineTestEnvironment();

        TEMP_DIRECTORY = DynamicInstrumentationProperties.TEMP_DIRECTORY;
        TEMP_CLASSPATH_DIRECTORY = getTempClasspathDirectory();
        EHCACHE_DISK_STORE_DIRECTORY = new File(TEMP_DIRECTORY, "ehcache");

        if (InvesdwinInitializationProperties.isInvesdwinInitializationAllowed()) {
            try {
                DynamicInstrumentationReflections.addPathToSystemClassLoader(TEMP_CLASSPATH_DIRECTORY);
                Assertions.assertThat(XmlTransformerConfigurer.INITIALIZED).isTrue();
                LogbackConfigurationLoader.loadLogbackConfiguration();
                SystemPropertiesLoader.loadSystemProperties();

                final SystemProperties systemProperties = new SystemProperties();
                systemProperties.setString("ehcache.disk.store.dir", EHCACHE_DISK_STORE_DIRECTORY.getAbsolutePath());
                systemProperties.setString("net.sf.ehcache.enableShutdownHook", "true");
            } catch (final Throwable t) {
                InvesdwinInitializationProperties.logInitializationFailedIsIgnored(t);
            }
        }

        DEFAULT_NETWORK_TIMEOUT = readDefaultNetworkTimeout();
        URIsConnect.setDefaultNetworkTimeout(DEFAULT_NETWORK_TIMEOUT);

        //needs to happen after properties have been loaded
        initClassPathScanner();
        registerTypesForSerialization();
    }

    private ContextProperties() {}

    private static boolean determineTestEnvironment() {
        //since java classes are packaged in src/main/java, we check if the test directory exists and if it actually contains any tests
        final File srcTestJavaDir = new File("src/test/java");
        final boolean testClassesExist = srcTestJavaDir.exists() && srcTestJavaDir.isDirectory()
                && srcTestJavaDir.list().length > 0;
        return testClassesExist;
    }

    /**
     * Invesdwin specific home dir that gets shared between multiple processes.
     * 
     * this should be $HOME/.invesdwin
     */
    public static synchronized File getHomeDirectory() {
        if (homeDirectory == null) {
            String home = getSystemHomeDirectory();
            if (IS_TEST_ENVIRONMENT) {
                home = ".";
            }
            final File homeDir = new File(home, ".invesdwin");
            createDirectory(homeDir);
            homeDirectory = homeDir;
        }
        return homeDirectory;
    }

    /**
     * http://stackoverflow.com/questions/585534/what-is-the-best-way-to-find-the-users-home-directory-in-java
     * 
     * this should $HOME
     */
    public static String getSystemHomeDirectory() {
        final String[] envs;
        if (OperatingSystem.isWindows()) {
            envs = new String[] { "HOMEDRIVE", "HOMEPATH", "USERPROFILE" };
        } else {
            envs = new String[] { "HOME" };
        }
        String home = null;
        for (final String env : envs) {
            final String envHome = System.getenv(env);
            if (Strings.isNotBlank(envHome)) {
                home = envHome;
                break;
            }
        }
        if (Strings.isBlank(home)) {
            home = new SystemProperties().getString("user.home");
        }
        return home;
    }

    public static synchronized File getLogDirectory() {
        if (logDirectory == null) {
            String logDirSr = "log";
            if (!ContextProperties.IS_TEST_ENVIRONMENT) {
                //Productive logs should not mix each other between processes
                logDirSr += "/";
                logDirSr += InvesdwinInitializationProperties.START_OF_APPLICATION_CLOCK_TIME
                        .toString("yyyyMMddHHmmss");
                logDirSr += "_";
                logDirSr += ManagementFactory.getRuntimeMXBean().getName();
            }
            final File logDir = new File(logDirSr);
            createDirectory(logDir);
            logDirectory = logDir;
        }
        return logDirectory;
    }

    private static File getTempClasspathDirectory() {
        final File tempClasspathDir = new File(TEMP_DIRECTORY, "cp");
        createDirectory(tempClasspathDir);
        return tempClasspathDir;
    }

    private static void createDirectory(final File dir) {
        if (InvesdwinInitializationProperties.isInvesdwinInitializationAllowed()) {
            try {
                FileUtils.forceMkdir(dir);
            } catch (final IOException e) {
                throw Err.process(e);
            }
        }
    }

    /**
     * Cache dir that holds information over multiple JVM instances.
     */
    public static synchronized File getCacheDirectory() {
        if (cacheDirectory == null) {
            final File cacheDir = new File("cache");
            createDirectory(cacheDir);
            cacheDirectory = cacheDir;
        }
        return cacheDirectory;
    }

    public static synchronized Set<String> getBasePackages() {
        if (basePackages == null) {
            final ClassPathScanner scanner = new ClassPathScanner();
            scanner.addIncludeFilter(new AssignableTypeFilter(IBasePackageDefinition.class));

            basePackages = new HashSet<String>();
            try {
                for (final BeanDefinition bd : scanner.findCandidateComponents("de.invesdwin")) {
                    final Class<IBasePackageDefinition> clazz = Reflections.classForName(bd.getBeanClassName());
                    final IBasePackageDefinition basePackage = clazz.newInstance();
                    basePackages.add(basePackage.getBasePackage());
                }
            } catch (final Exception e) {
                throw Err.process(e);
            }

            final Log log = new Log(ContextProperties.class);
            if (log.isInfoEnabled() && basePackages.size() > 0) {
                String basePackageSingularPlural = "base package";
                if (basePackages.size() != 1) {
                    basePackageSingularPlural += "s";
                }

                log.info("Loading %s %s %s", basePackages.size(), basePackageSingularPlural, basePackages);
            }
        }
        return basePackages;
    }

    private static Duration readDefaultNetworkTimeout() {
        final SystemProperties systemProperties = new SystemProperties(ContextProperties.class);
        final String key = "DEFAULT_NETWORK_TIMEOUT";
        final Duration duration;
        if (!systemProperties.containsKey(key)
                && !InvesdwinInitializationProperties.isInvesdwinInitializationAllowed()) {
            //default to 30 seconds if for some reason the properties were not loaded
            duration = new Duration(30, FTimeUnit.SECONDS);
        } else {
            duration = systemProperties.getDuration(key);
            //So that Spring-WS also respects the timeouts...
            final SystemProperties sysProps = new SystemProperties();
            sysProps.setInteger("sun.net.client.defaultConnectTimeout", duration.intValue(FTimeUnit.MILLISECONDS));
            sysProps.setInteger("sun.net.client.defaultReadTimeout", duration.intValue(FTimeUnit.MILLISECONDS));
        }
        return duration;
    }

    private static void registerTypesForSerialization() {
        /*
         * performance optimization see: https://github.com/RuedigerMoeller/fast-serialization/wiki/Serialization
         */
        final ClassPathScanner scanner = new ClassPathScanner();
        scanner.addIncludeFilter(new AssignableTypeFilter(ISerializableValueObject.class));
        final List<Class<?>> classesToRegister = new ArrayList<Class<?>>();
        for (final String basePackage : ContextProperties.getBasePackages()) {
            for (final BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
                final Class<?> clazz = Reflections.classForName(bd.getBeanClassName());
                classesToRegister.add(clazz);
            }
        }
        //sort them so they always get the same index in registration
        classesToRegister.sort(new Comparator<Class<?>>() {
            @Override
            public int compare(final Class<?> o1, final Class<?> o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (final Class<?> clazz : classesToRegister) {
            Objects.SERIALIZATION_CONFIG.registerClass(clazz);
        }
    }

    public static void initClassPathScanner() {
        final List<TypeFilter> defaultExcludeFilters = new ArrayList<TypeFilter>();
        //filter out test classes to prevent issues with class not found or resource not found in production
        defaultExcludeFilters.add(new RegexPatternTypeFilter(Pattern.compile("de\\.invesdwin\\..*(Test|Stub)")));
        defaultExcludeFilters.add(new RegexPatternTypeFilter(Pattern.compile("de\\.invesdwin\\..*\\.test\\..*")));
        ClassPathScanner.setDefaultExcludeFilters(defaultExcludeFilters);
    }

}
