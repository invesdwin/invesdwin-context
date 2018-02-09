package de.invesdwin.context.integration.retry.fast;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.retry.RetryLaterException;
import de.invesdwin.util.error.Throwables;

@NotThreadSafe
public class FastRetryLaterException extends RetryLaterException {

    private static final long serialVersionUID = 1L;

    public FastRetryLaterException() {
        super();
    }

    public FastRetryLaterException(final String message) {
        super(message);
    }

    public FastRetryLaterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FastRetryLaterException(final Throwable cause) {
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
