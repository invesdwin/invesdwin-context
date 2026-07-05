package de.invesdwin.context.log.log4j2;

import javax.annotation.concurrent.Immutable;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

import de.invesdwin.util.time.date.FTimeUnit;

@Immutable
@Plugin(name = "NanoTimestampConverter", category = "Converter")
@ConverterKeys({ "nanoTime" })
public class NanoTimestampConverter extends LogEventPatternConverter {

    protected NanoTimestampConverter(final String[] options) {
        super("NanoTime", "nanoTime");
    }

    public static NanoTimestampConverter newInstance(final String[] options) {
        return new NanoTimestampConverter(options);
    }

    @Override
    public void format(final LogEvent event, final StringBuilder toAppendTo) {
        // Instant.getNanoOfSecond() always returns a 9-digit value (0 to 999,999,999)
        final int nanos = event.getInstant().getNanoOfSecond();

        // Inlining the math avoids the method invocation overhead of FDatePicos
        final int millisecond = nanos / FTimeUnit.NANOSECONDS_IN_MILLISECOND;
        final int microsecond = (nanos / FTimeUnit.NANOSECONDS_IN_MICROSECOND) % FTimeUnit.NANOSECONDS_IN_MICROSECOND;
        final int nanosecond = nanos % FTimeUnit.NANOSECONDS_IN_MICROSECOND;

        // Maintains the same formatting order as the original code
        append3Digits(toAppendTo, nanosecond);
        toAppendTo.append('.');
        append3Digits(toAppendTo, microsecond);
        toAppendTo.append('.');
        append3Digits(toAppendTo, millisecond);
    }

    /**
     * Extracts digits directly using character arithmetic. This avoids CPU branching, Integer.toString(), and heavy
     * StringBuilder.append(int) methods.
     */
    private void append3Digits(final StringBuilder sb, final int val) {
        sb.append((char) ('0' + (val / 100)));
        sb.append((char) ('0' + ((val / 10) % 10)));
        sb.append((char) ('0' + (val % 10)));
    }
}