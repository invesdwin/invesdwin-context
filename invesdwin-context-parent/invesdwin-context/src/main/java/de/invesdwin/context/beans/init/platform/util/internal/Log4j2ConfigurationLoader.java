package de.invesdwin.context.beans.init.platform.util.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;
import org.apache.logging.log4j.jul.Log4jBridgeHandler;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.log.Log;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.streams.resource.Resources;

@Immutable
public final class Log4j2ConfigurationLoader {

    private static final String META_INF_LOG4J = "/META-INF/log4j2/";
    private static final Log LOG = new Log(Log4j2ConfigurationLoader.class);

    private Log4j2ConfigurationLoader() {}

    public static void loadLog4jConfiguration() {
        final org.apache.logging.log4j.spi.LoggerContext loggerContext = LogManager.getContext(false);
        if (loggerContext instanceof LoggerContext) {
            try {
                final LoggerContext cLoggerContext = (LoggerContext) LogManager.getContext(false);
                final List<Resource> orderedConfigs = new ArrayList<Resource>();
                final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                orderedConfigs
                        .addAll(Arrays.asList(resolver.getResources("classpath*:" + META_INF_LOG4J + "*log4j2.xml")));
                if (ContextProperties.IS_TEST_ENVIRONMENT) {
                    orderedConfigs.addAll(
                            Arrays.asList(resolver.getResources("classpath*:" + META_INF_LOG4J + "*log4j2-test.xml")));
                } else {
                    orderedConfigs.addAll(
                            Arrays.asList(resolver.getResources("classpath*:" + META_INF_LOG4J + "*log4j2-dist.xml")));
                }

                final ConfigurationSource configSource = new ConfigurationSource(
                        new Log4j2ConfigurationMerger(orderedConfigs).getInputStream());
                final Configuration configuration = new XmlConfiguration(cLoggerContext, configSource);
                cLoggerContext.setConfiguration(configuration);

                //http://stackoverflow.com/questions/2533227/how-can-i-disable-the-default-console-handler-while-using-the-java-logging-api
                java.util.logging.LogManager.getLogManager().reset();
                Log4jBridgeHandler.install(true, "", true);
                logLog4jBeingConfigured(orderedConfigs);
            } catch (final IOException e) {
                throw Err.process(e);
            }
        }
    }

    private static void logLog4jBeingConfigured(final List<Resource> configs) {
        if (LOG.isInfoEnabled() && configs.size() > 0) {
            String configSingularPlural = "config";
            if (configs.size() != 1) {
                configSingularPlural += "s";
            }

            final List<String> configStrings = Resources.extractMetaInfResourceLocations(configs);
            LOG.info("Loading " + configs.size() + " log4j2 " + configSingularPlural + " from classpath "
                    + configStrings);
        }
    }

}
