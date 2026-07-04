package de.invesdwin.context.log.log4j2;

import javax.annotation.concurrent.Immutable;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

import de.invesdwin.util.time.date.millis.FDatePicos;

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
        final int nanos = event.getInstant().getNanoOfSecond();
        final int nanosecond = FDatePicos.getPicosecond(nanos);
        final int microsecond = FDatePicos.getNanosecond(nanos);
        final int millisecond = FDatePicos.getMicrosecond(nanos);

        appendFraction(toAppendTo, nanosecond);
        toAppendTo.append(".");
        appendFraction(toAppendTo, microsecond);
        toAppendTo.append(".");
        appendFraction(toAppendTo, millisecond);
    }

    private void appendFraction(final StringBuilder toAppendTo, final int fraction) {
        if (fraction < 10) {
            toAppendTo.append("00");
        } else if (fraction < 100) {
            toAppendTo.append("0");
        }
        toAppendTo.append(fraction);
    }
}