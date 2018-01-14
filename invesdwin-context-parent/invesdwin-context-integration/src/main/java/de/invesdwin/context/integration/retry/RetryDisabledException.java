package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class RetryDisabledException extends Exception {

    private static final long serialVersionUID = 1L;

    public RetryDisabledException() {
        super();
    }

    public RetryDisabledException(final String message) {
        super(message);
    }

    public RetryDisabledException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RetryDisabledException(final Throwable cause) {
        super(cause);
    }

}
