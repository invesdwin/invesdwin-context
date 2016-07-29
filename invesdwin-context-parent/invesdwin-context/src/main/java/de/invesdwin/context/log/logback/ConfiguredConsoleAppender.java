package de.invesdwin.context.log.logback;

import javax.annotation.concurrent.NotThreadSafe;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import de.invesdwin.context.log.logback.internal.LogbackProperties;

@NotThreadSafe
public class ConfiguredConsoleAppender extends ch.qos.logback.core.ConsoleAppender<ILoggingEvent> {

    @Override
    public void start() {
        final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern(LogbackProperties.ENCODER_PATTERN);
        encoder.setContext(getContext());
        encoder.start();
        setEncoder(encoder);
        super.start();
    }

}
