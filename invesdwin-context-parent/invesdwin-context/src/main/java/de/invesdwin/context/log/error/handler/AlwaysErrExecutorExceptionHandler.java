package de.invesdwin.context.log.error.handler;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.log.error.Err;
import de.invesdwin.util.concurrent.handler.IExecutorExceptionHandler;

@Immutable
public final class AlwaysErrExecutorExceptionHandler implements IExecutorExceptionHandler {

    public static final AlwaysErrExecutorExceptionHandler INSTANCE = new AlwaysErrExecutorExceptionHandler();

    private AlwaysErrExecutorExceptionHandler() {}

    @SuppressWarnings("deprecation")
    @Override
    public Throwable handleExecutorException(final Throwable t, final boolean executeCalledWithoutFuture,
            final boolean callableRequiresReturnValue) {
        return Err.process(t, false);
    }
}
