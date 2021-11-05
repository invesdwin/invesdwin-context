package de.invesdwin.context.integration.retry.hook.log;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum LoggingReason {
    INITIAL,
    NEW_CAUSE,
    TIME;
}