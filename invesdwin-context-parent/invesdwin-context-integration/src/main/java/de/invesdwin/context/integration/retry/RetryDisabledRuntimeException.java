package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class RetryDisabledRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RetryDisabledRuntimeException() {
        super();
    }

    public RetryDisabledRuntimeException(final String message) {
        super(message);
    }

    public RetryDisabledRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RetryDisabledRuntimeException(final Throwable cause) {
        super(cause);
    }

}
