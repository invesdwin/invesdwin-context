package de.invesdwin.context.log.log4j2;

import java.io.Serializable;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import de.invesdwin.context.ContextProperties;

@NotThreadSafe
@Plugin(name = "ConfiguredFile", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public final class ConfiguredFileAppender extends AbstractOutputStreamAppender<RollingFileManager> {

    private ConfiguredFileAppender(final RollingFileAppender delegate) {
        super(delegate.getName(), delegate.getLayout(), delegate.getFilter(), delegate.ignoreExceptions(),
                delegate.getImmediateFlush(), delegate.getPropertyArray(), delegate.getManager());
    }

    @PluginFactory
    public static Appender createAppender(@PluginAttribute("name") final String name,
            @PluginAttribute("ignoreExceptions") final String ignore,
            @PluginElement("Layout") final Layout<? extends Serializable> pLayout,
            @PluginElement("Filters") final Filter filter, @PluginConfiguration final Configuration config) {

        if (name == null) {
            return null;
        }

        final Layout<? extends Serializable> layout;
        if (pLayout == null) {
            layout = PatternLayout.newBuilder()
                    .setPattern(Log4j2Properties.LAYOUT_PATTERN)
                    .setConfiguration(config)
                    .build();
        } else {
            layout = pLayout;
        }

        final String logDirectory = ContextProperties.getLogDirectory().getPath();
        final String fileBase = logDirectory + "/" + name;
        final String fileName = fileBase + ".log";
        final String filePattern = fileBase + "_%i.log";

        final boolean ignoreExceptions = ignore == null || Boolean.parseBoolean(ignore);

        // Create default rollover strategy (max 9 files as in logback)
        final DefaultRolloverStrategy rolloverStrategy = DefaultRolloverStrategy.newBuilder()
                .setMax("9")
                .setConfig(config)
                .build();

        return RollingFileAppender.newBuilder()
                .setName(name)
                .setLayout(layout)
                .setFilter(filter)
                .setFileName(fileName)
                .setFilePattern(filePattern)
                .setPolicy(SizeBasedTriggeringPolicy.createPolicy("20MB"))
                .setStrategy(rolloverStrategy)
                .setIgnoreExceptions(ignoreExceptions)
                .setConfiguration(config)
                .build();
    }

}
