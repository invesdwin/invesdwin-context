package de.invesdwin.context.beans.init.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.springframework.core.io.Resource;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.beans.init.PreMergedContext;
import de.invesdwin.context.log.Log;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.util.lang.Resources;

@Immutable
public final class SystemPropertiesLoader {

    private static final String META_INF = "/META-INF/";
    private static final String META_INF_ENV = META_INF + "env/";
    private static final Log LOG = new Log(SystemPropertiesLoader.class);

    private SystemPropertiesLoader() {}

    /**
     * This should only be used by infrastructure classes.
     */
    public static void loadSystemProperties() {
        try {
            final Resource[] properties = PreMergedContext.getInstance().getResources(
                    "classpath*:" + META_INF + "*.properties");
            logPropertiesBeingLoaded(properties);
            for (final Resource p : properties) {
                SystemProperties.setSystemProperties(p, false);
            }
            final String overridePropertiesName;
            if (ContextProperties.IS_TEST_ENVIRONMENT) {
                overridePropertiesName = new SystemProperties().getString("user.name") + ".properties";
            } else {
                overridePropertiesName = "distribution.properties";
            }
            final Resource[] overrideProperties = PreMergedContext.getInstance().getResources(
                    "classpath*:" + META_INF_ENV + overridePropertiesName);
            logOverridePropertiesBeingLoaded(overridePropertiesName, overrideProperties);
            for (final Resource p : overrideProperties) {
                SystemProperties.setSystemProperties(p, true);
            }
        } catch (final IOException e) {
            throw Err.process(e);
        }
    }

    private static void logPropertiesBeingLoaded(final Resource[] properties) {
        if (LOG.isInfoEnabled() && properties.length > 0) {
            final List<String> propertyFilesForLog = Resources.extractMetaInfResourceLocations(Arrays.asList(properties));
            String filesSingularPlural = "file";
            if (properties.length != 1) {
                filesSingularPlural += "s";
            }
            LOG.info("Loading " + properties.length + " properties " + filesSingularPlural + " "
                    + propertyFilesForLog.toString());
        }
    }

    private static void logOverridePropertiesBeingLoaded(final String overridePropertiesName,
            final Resource[] overrideProperties) {
        if (LOG.isInfoEnabled()) {
            String filesSingularPlural = "file";
            if (overrideProperties.length != 1) {
                filesSingularPlural += "s";
            }
            LOG.info("Loading " + overrideProperties.length + " override properties " + filesSingularPlural + " ["
                    + META_INF_ENV + overridePropertiesName + "]");
        }
    }

}
