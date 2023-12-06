package de.invesdwin.context.log.error.handler;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.log.error.Err;

@Immutable
public final class ErrUncaughtExceptionHandler implements UncaughtExceptionHandler {
    @SuppressWarnings("deprecation")
    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        Err.process(e, true);
    }
}