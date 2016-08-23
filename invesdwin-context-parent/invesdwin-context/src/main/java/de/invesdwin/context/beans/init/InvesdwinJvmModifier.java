package de.invesdwin.context.beans.init;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.beans.init.internal.FileEncodingChecker;
import de.invesdwin.context.beans.init.internal.InstrumentationHookLoader;
import de.invesdwin.context.beans.init.internal.LogbackConfigurationLoader;
import de.invesdwin.context.beans.init.internal.SystemPropertiesLoader;
import de.invesdwin.context.beans.init.internal.XmlTransformerConfigurer;
import de.invesdwin.context.beans.init.internal.protocols.ProtocolRegistration;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.instrument.DynamicInstrumentationLoader;
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
public class InvesdwinJvmModifier {

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

    public void initTempClasspathDirectoryInSystemClassLoader(final File tempClasspathDirectory) {
        DynamicInstrumentationReflections.addPathToSystemClassLoader(tempClasspathDirectory);
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

}
