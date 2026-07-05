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

    private final PatternFormatter[] formatters;
    private final int fixedLength;
    private final boolean handlesThrowable;

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
        // Optimized: Convert to a native array for faster traversal in the hot path
        this.formatters = formatters.toArray(new PatternFormatter[0]);

        // Optimized: Pre-calculate handlesThrowable strictly once at startup
        boolean handles = false;
        for (final PatternFormatter formatter : this.formatters) {
            if (formatter.getConverter() instanceof LogEventPatternConverter) {
                if (formatter.getConverter().handlesThrowable()) {
                    handles = true;
                    break;
                }
            }
        }
        this.handlesThrowable = handles;

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
        // Optimized: Cache the target math arithmetic outside the hot loop
        final int targetLength = initialLength + fixedLength;

        for (int i = 0; i < formatters.length; i++) {
            formatters[i].format(event, toAppendTo);
            if (toAppendTo.length() >= targetLength) { // stop early
                break;
            }
        }

        final int currentLength = toAppendTo.length();
        if (currentLength > targetLength) {
            toAppendTo.setLength(targetLength);
        } else if (currentLength < targetLength) {
            // Optimized: Calculate exact missing characters and append primitive chars
            final int paddingNeeded = targetLength - currentLength;
            for (int i = 0; i < paddingNeeded; i++) {
                toAppendTo.append(' ');
            }
        }
    }

    @Override
    public boolean handlesThrowable() {
        return handlesThrowable;
    }
}