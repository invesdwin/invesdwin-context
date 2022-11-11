package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class RetryLaterRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RetryLaterRuntimeException() {
        super();
    }

    public RetryLaterRuntimeException(final String message) {
        super(message);
    }

    public RetryLaterRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RetryLaterRuntimeException(final Throwable cause) {
        super(cause);
    }

}
