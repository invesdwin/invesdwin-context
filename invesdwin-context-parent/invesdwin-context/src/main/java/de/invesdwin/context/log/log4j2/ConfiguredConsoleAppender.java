package de.invesdwin.context.log.log4j2;

import java.io.Serializable;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.OutputStreamManager;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

@NotThreadSafe
@Plugin(name = "ConfiguredConsole", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public final class ConfiguredConsoleAppender extends AbstractOutputStreamAppender<OutputStreamManager> {

    private ConfiguredConsoleAppender(final ConsoleAppender delegate) {
        super(delegate.getName(), delegate.getLayout(), delegate.getFilter(), delegate.ignoreExceptions(),
                delegate.getImmediateFlush(), delegate.getPropertyArray(), delegate.getManager());
    }

    @SuppressWarnings("deprecation")
    @PluginFactory
    public static Appender createAppender(@PluginAttribute("name") final String name,
            @PluginAttribute("target") final String target, @PluginAttribute("ignoreExceptions") final String ignore,
            @PluginElement("Layout") final Layout<? extends Serializable> pLayout,
            @PluginElement("Filters") final Filter filter, @PluginConfiguration final Configuration config) {

        if (name == null) {
            return null;
        }

        final Layout<? extends Serializable> layout;
        if (pLayout == null) {
            layout = PatternLayout.newBuilder()
                    .withPattern(Log4j2Properties.LAYOUT_PATTERN)
                    .withConfiguration(config)
                    .build();
        } else {
            layout = pLayout;
        }

        final ConsoleAppender.Target targetEnum = target == null ? ConsoleAppender.Target.SYSTEM_OUT
                : ConsoleAppender.Target.valueOf(target);
        final boolean ignoreExceptions = ignore == null || Boolean.parseBoolean(ignore);

        final ConsoleAppender delegate = ConsoleAppender.newBuilder()
                .setName(name)
                .setLayout(layout)
                .setFilter(filter)
                .setTarget(targetEnum)
                .setIgnoreExceptions(ignoreExceptions)
                .setConfiguration(config)
                .build();
        return new ConfiguredConsoleAppender(delegate);
    }

}
