package de.invesdwin.context.log.logback;

import javax.annotation.concurrent.Immutable;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

@Immutable
public class StdoutExpressionEvaluator extends EventEvaluatorBase<ILoggingEvent> {

    private static final int INFO = ch.qos.logback.classic.Level.INFO.toInt();

    @Override
    public boolean evaluate(final ILoggingEvent event) throws NullPointerException, EvaluationException {
        final int level = event.getLevel().toInt();
        return level <= INFO;
    }
}