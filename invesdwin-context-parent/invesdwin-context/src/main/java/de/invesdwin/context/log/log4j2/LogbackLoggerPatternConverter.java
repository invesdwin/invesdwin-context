package de.invesdwin.context.log.log4j2;

import javax.annotation.concurrent.Immutable;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Immutable
@Plugin(name = "LogbackLoggerPatternConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({ "logbackLogger" })
@PerformanceSensitive("allocation")
public final class LogbackLoggerPatternConverter extends ALogbackNamePatternConverter {

    private LogbackLoggerPatternConverter(final int targetLength) {
        super("LogbackLogger", "lockbackLogger", targetLength);
    }

    public static LogbackLoggerPatternConverter newInstance(final Configuration config, final String[] options) {
        if (options.length != 1) {
            //CHECKSTYLE:OFF
            LOGGER.error("Incorrect number of options on maxLength: expected 1 received {}: {}", options.length,
                    options);
            //CHECKSTYLE:ON
            return null;
        }
        if (options[0] == null) {
            LOGGER.error("No length supplied on targetLength");
            return null;
        }
        final int targetLength = AbstractAppender.parseInt(options[0], 100);
        return new LogbackLoggerPatternConverter(targetLength);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void format(final LogEvent event, final StringBuilder toAppendTo) {
        abbreviate(event.getLoggerName(), toAppendTo);
    }
}
