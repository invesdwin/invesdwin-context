package de.invesdwin.context.integration.retry.fast;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.retry.RetryDisabledException;
import de.invesdwin.util.error.Throwables;

@NotThreadSafe
public class FastRetryDisabledException extends RetryDisabledException {

    private static final long serialVersionUID = 1L;

    public FastRetryDisabledException() {
        super();
    }

    public FastRetryDisabledException(final String message) {
        super(message);
    }

    public FastRetryDisabledException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FastRetryDisabledException(final Throwable cause) {
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
