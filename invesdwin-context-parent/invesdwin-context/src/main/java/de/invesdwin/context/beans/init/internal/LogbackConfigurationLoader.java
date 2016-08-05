package de.invesdwin.context.beans.init.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import de.invesdwin.context.log.Log;
import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.lang.Resources;

@Immutable
public final class LogbackConfigurationLoader {

    private static final String META_INF_LOGBACK = "/META-INF/logback/";
    private static final Log LOG = new Log(LogbackConfigurationLoader.class);

    private LogbackConfigurationLoader() {}

    public static void loadLogbackConfiguration() {
        final org.slf4j.ILoggerFactory lf = org.slf4j.LoggerFactory.getILoggerFactory();
        if (lf instanceof LoggerContext) {
            try {
                final List<Resource> orderedConfigs = new ArrayList<Resource>();
                final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                orderedConfigs.addAll(
                        Arrays.asList(resolver.getResources("classpath*:" + META_INF_LOGBACK + "*logback.xml")));
                orderedConfigs.addAll(
                        Arrays.asList(resolver.getResources("classpath*:" + META_INF_LOGBACK + "*logback-test.xml")));

                final LoggerContext lc = (LoggerContext) lf;
                final JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(lc);
                lc.reset();

                configurator.doConfigure(new LogbackConfigurationMerger(orderedConfigs).getInputStream());
                //http://stackoverflow.com/questions/2533227/how-can-i-disable-the-default-console-handler-while-using-the-java-logging-api
                java.util.logging.LogManager.getLogManager().reset();
                SLF4JBridgeHandler.install();
                logLogbackBeingConfigured(orderedConfigs);
                StatusPrinter.printIfErrorsOccured(lc);
            } catch (final JoranException e) {
                throw Err.process(e);
            } catch (final IOException e) {
                throw Err.process(e);
            }
        }
    }

    private static void logLogbackBeingConfigured(final List<Resource> configs) {
        if (LOG.isInfoEnabled() && configs.size() > 0) {
            String configSingularPlural = "config";
            if (configs.size() != 1) {
                configSingularPlural += "s";
            }

            final List<String> configStrings = Resources.extractMetaInfResourceLocations(configs);
            LOG.info("Loading " + configs.size() + " logback " + configSingularPlural + " " + configStrings);
        }
    }

}
