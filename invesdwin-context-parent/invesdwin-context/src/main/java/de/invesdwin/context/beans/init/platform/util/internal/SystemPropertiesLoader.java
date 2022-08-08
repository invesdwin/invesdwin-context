package de.invesdwin.context.beans.init.platform.util.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.context.log.Log;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.streams.resource.Resources;

@Immutable
public final class SystemPropertiesLoader {

    private static final String META_INF = "/META-INF/";
    private static final String META_INF_ENV = META_INF + "env/";
    private static final Log LOG = new Log(SystemPropertiesLoader.class);

    private SystemPropertiesLoader() {
    }

    /**
     * This should only be used by infrastructure classes.
     */
    public static void loadSystemProperties() {
        try {
            final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            final Resource[] properties = resolver.getResources("classpath*:" + META_INF + "*.properties");
            logPropertiesBeingLoaded(properties);
            for (final Resource p : properties) {
                SystemProperties.setSystemProperties(p, false);
            }
            final List<Resource> overrideProperties = new ArrayList<Resource>();
            final List<String> overridePropertiesNames = new ArrayList<String>();
            final Resource systemPropertiesResource = PlatformInitializerProperties.getInitializer()
                    .initSystemPropertiesResource();
            if (systemPropertiesResource != null && systemPropertiesResource.exists()) {
                overrideProperties.add(systemPropertiesResource);
            }
            overridePropertiesNames.add(Resources.resourceToPatternString(systemPropertiesResource) + "("
                    + overrideProperties.size() + ")");
            final String distributionPropertiesName;
            if (ContextProperties.IS_TEST_ENVIRONMENT || ContextProperties.isIgnoreDistributionProperties()) {
                distributionPropertiesName = new SystemProperties().getString("user.name") + ".properties";
            } else {
                distributionPropertiesName = "distribution.properties";
            }
            final String distributionPropertiesPattern = "classpath*:" + META_INF_ENV + distributionPropertiesName;
            final List<Resource> distributionProperties = Arrays
                    .asList(resolver.getResources(distributionPropertiesPattern));
            overridePropertiesNames.add(distributionPropertiesPattern + "(" + distributionProperties.size() + ")");
            overrideProperties.addAll(distributionProperties);
            logOverridePropertiesBeingLoaded(overrideProperties, overridePropertiesNames);
            for (final Resource p : overrideProperties) {
                SystemProperties.setSystemProperties(p, true);
            }
        } catch (final IOException e) {
            throw Err.process(e);
        }
    }

    private static void logPropertiesBeingLoaded(final Resource[] properties) {
        if (LOG.isInfoEnabled() && properties.length > 0) {
            final List<String> propertyFilesForLog = Resources
                    .extractMetaInfResourceLocations(Arrays.asList(properties));
            String filesSingularPlural = "file";
            if (properties.length != 1) {
                filesSingularPlural += "s";
            }
            LOG.info("Loading " + properties.length + " properties " + filesSingularPlural + " from classpath "
                    + propertyFilesForLog.toString());
        }
    }

    private static void logOverridePropertiesBeingLoaded(final List<Resource> overrideProperties,
            final List<String> overridePropertiesNames) {
        if (LOG.isInfoEnabled()) {
            String filesSingularPlural = "file";
            if (overrideProperties.size() != 1) {
                filesSingularPlural += "s";
            }
            LOG.info("Loading " + overrideProperties.size() + " override properties " + filesSingularPlural + " from "
                    + overridePropertiesNames);
        }
    }

}
