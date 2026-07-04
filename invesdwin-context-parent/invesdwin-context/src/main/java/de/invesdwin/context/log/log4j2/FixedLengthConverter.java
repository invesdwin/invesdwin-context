package de.invesdwin.context.log.log4j2;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Immutable
@Plugin(name = "FixedLength", category = PatternConverter.CATEGORY)
@ConverterKeys({ "fixedLength", "fixedLen" })
@PerformanceSensitive("allocation")
public final class FixedLengthConverter extends LogEventPatternConverter {

    private final List<PatternFormatter> formatters;
    private final int fixedLength;

    /**
     * Construct the converter.
     *
     * @param formatters
     *            The PatternFormatters to generate the text to manipulate.
     * @param fixedLength
     *            The max. length of the resulting string. Ellipsis ("...") is appended on shorted string, if greater
     *            than 20.
     */
    private FixedLengthConverter(final List<PatternFormatter> formatters, final int fixedLength) {
        super("FixedLength", "fixedLength");
        this.fixedLength = fixedLength;
        this.formatters = formatters;
        //CHECKSTYLE:OFF
        LOGGER.trace("new MaxLengthConverter with {}", fixedLength);
        //CHECKSTYLE:ON
    }

    public static FixedLengthConverter newInstance(final Configuration config, final String[] options) {
        if (options.length != 2) {
            //CHECKSTYLE:OFF
            LOGGER.error("Incorrect number of options on maxLength: expected 2 received {}: {}", options.length,
                    options);
            //CHECKSTYLE:ON
            return null;
        }
        if (options[0] == null) {
            LOGGER.error("No pattern supplied on maxLength");
            return null;
        }
        if (options[1] == null) {
            LOGGER.error("No length supplied on maxLength");
            return null;
        }
        final PatternParser parser = PatternLayout.createPatternParser(config);
        final List<PatternFormatter> formatters = parser.parse(options[0]);
        return new FixedLengthConverter(formatters, AbstractAppender.parseInt(options[1], 100));
    }

    @Override
    public void format(final LogEvent event, final StringBuilder toAppendTo) {
        final int initialLength = toAppendTo.length();
        for (int i = 0; i < formatters.size(); i++) {
            final PatternFormatter formatter = formatters.get(i);
            formatter.format(event, toAppendTo);
            if (toAppendTo.length() > initialLength + fixedLength) { // stop early
                break;
            }
        }
        if (toAppendTo.length() > initialLength + fixedLength) {
            toAppendTo.setLength(initialLength + fixedLength);
        } else {
            while (toAppendTo.length() < initialLength + fixedLength) {
                toAppendTo.append(" ");
            }
        }
    }

    @Override
    public boolean handlesThrowable() {
        return formatters.stream()
                .map(PatternFormatter::getConverter)
                .anyMatch(LogEventPatternConverter::handlesThrowable);
    }
}
