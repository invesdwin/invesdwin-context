package de.invesdwin.context.integration.retry.fast;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.retry.RetryLaterRuntimeException;
import de.invesdwin.util.error.Throwables;

@NotThreadSafe
public class FastRetryLaterRuntimeException extends RetryLaterRuntimeException {

    private static final long serialVersionUID = 1L;

    public FastRetryLaterRuntimeException() {
        super();
    }

    public FastRetryLaterRuntimeException(final String message) {
        super(message);
    }

    public FastRetryLaterRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FastRetryLaterRuntimeException(final Throwable cause) {
        super(cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        if (Throwables.isDebugStackTraceEnabled()) {
            return super.fillInStackTrace();
        }
        return this;
    }

}
