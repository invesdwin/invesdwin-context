package de.invesdwin.context.integration.retry.fast;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.retry.RetryDisabledRuntimeException;
import de.invesdwin.util.error.Throwables;

@NotThreadSafe
public class FastRetryDisabledRuntimeException extends RetryDisabledRuntimeException {

    private static final long serialVersionUID = 1L;

    public FastRetryDisabledRuntimeException() {
        super();
    }

    public FastRetryDisabledRuntimeException(final String message) {
        super(message);
    }

    public FastRetryDisabledRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FastRetryDisabledRuntimeException(final Throwable cause) {
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
