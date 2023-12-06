package de.invesdwin.context.log.error.handler;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.concurrent.handler.IExecutorExceptionHandler;

@Immutable
public final class ErrUncaughtExecutorExceptionHandler implements IExecutorExceptionHandler {

    public static final ErrUncaughtExecutorExceptionHandler INSTANCE = new ErrUncaughtExecutorExceptionHandler();

    private ErrUncaughtExecutorExceptionHandler() {}

    @SuppressWarnings("deprecation")
    @Override
    public Throwable handleExecutorException(final Throwable t, final boolean executeCalledWithoutFuture,
            final boolean callableRequiresReturnValue) {
        if (executeCalledWithoutFuture) {
            return Err.process(t, true);
        } else {
            return t;
        }
    }
}
