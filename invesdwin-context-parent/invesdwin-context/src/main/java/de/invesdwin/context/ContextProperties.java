package de.invesdwin.context;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import de.invesdwin.context.beans.init.locations.IBasePackageDefinition;
import de.invesdwin.context.beans.init.platform.DefaultPlatformInitializer;
import de.invesdwin.context.log.Log;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.system.OperatingSystem;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.instrument.DynamicInstrumentationProperties;
import de.invesdwin.norva.marker.ISerializableValueObject;
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
        final DefaultPlatformInitializer initializer = PlatformInitializerProperties.getInitializer();
        IS_TEST_ENVIRONMENT = initializer.initIsTestEnvironment();

        TEMP_DIRECTORY = initializer.initTempDirectory();
        TEMP_CLASSPATH_DIRECTORY = initializer.initTempClasspathDirectory(TEMP_DIRECTORY);
        EHCACHE_DISK_STORE_DIRECTORY = new File(TEMP_DIRECTORY, "ehcache");

        if (PlatformInitializerProperties.isAllowed()) {
            try {
                initializer.initXmlTransformerConfigurer();
                initializer.initLogbackConfigurationLoader();
                initializer.initSystemPropertiesLoader();

                initializer.initEhcacheSystemProperties(EHCACHE_DISK_STORE_DIRECTORY);
            } catch (final Throwable t) {
                PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
            }
        }

        DEFAULT_NETWORK_TIMEOUT = readDefaultNetworkTimeout();
        URIsConnect.setDefaultNetworkTimeout(DEFAULT_NETWORK_TIMEOUT);

        //needs to happen after properties have been loaded
        initClassPathScanner();
        registerTypesForSerialization();
    }

    private ContextProperties() {}

    /**
     * Invesdwin specific home dir that gets shared between multiple processes.
     * 
     * this should be $HOME/.invesdwin
     */
    public static synchronized File getHomeDirectory() {
        if (homeDirectory == null) {
            homeDirectory = PlatformInitializerProperties.getInitializer().initHomeDirectory(getSystemHomeDirectory(),
                    IS_TEST_ENVIRONMENT && !PlatformInitializerProperties.isKeepSystemHomeDuringTests());
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
            logDirectory = PlatformInitializerProperties.getInitializer().initLogDirectory(IS_TEST_ENVIRONMENT);
        }
        return logDirectory;
    }

    /**
     * Cache dir that holds information over multiple JVM instances.
     */
    public static synchronized File getCacheDirectory() {
        if (cacheDirectory == null) {
            cacheDirectory = PlatformInitializerProperties.getInitializer().initCacheDirectory();
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
        if (!systemProperties.containsKey(key) && !PlatformInitializerProperties.isAllowed()) {
            //default to 30 seconds if for some reason the properties were not loaded
            duration = new Duration(30, FTimeUnit.SECONDS);
        } else {
            duration = systemProperties.getDuration(key);
            //So that Spring-WS also respects the timeouts...
            PlatformInitializerProperties.getInitializer().initDefaultTimeoutSystemProperties(duration);
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

    public static String getProcessId() {
        return DynamicInstrumentationProperties.getProcessId();
    }

}
