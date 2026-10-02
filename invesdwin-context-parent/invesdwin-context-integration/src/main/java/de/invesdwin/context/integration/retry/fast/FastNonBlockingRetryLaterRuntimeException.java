package de.invesdwin.context.integration.retry.fast;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.retry.NonBlockingRetryLaterRuntimeException;
import de.invesdwin.util.error.Throwables;

@NotThreadSafe
public class FastNonBlockingRetryLaterRuntimeException extends NonBlockingRetryLaterRuntimeException {

    private static final long serialVersionUID = 1L;

    public FastNonBlockingRetryLaterRuntimeException() {
        super();
    }

    public FastNonBlockingRetryLaterRuntimeException(final String message) {
        super(message);
    }

    public FastNonBlockingRetryLaterRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FastNonBlockingRetryLaterRuntimeException(final Throwable cause) {
        super(cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        if (isDebugStackTraceEnabled()) {
            return super.fillInStackTrace();
        }
        return this;
    }

    private static boolean isDebugStackTraceEnabled() {
        return Throwables.isDebugStackTraceEnabled();
    }

}
