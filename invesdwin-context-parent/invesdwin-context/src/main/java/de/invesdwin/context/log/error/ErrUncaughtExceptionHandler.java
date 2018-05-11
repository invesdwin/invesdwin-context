package de.invesdwin.context.log.error;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ErrUncaughtExceptionHandler implements UncaughtExceptionHandler {
    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        Err.process(e, true);
    }
}