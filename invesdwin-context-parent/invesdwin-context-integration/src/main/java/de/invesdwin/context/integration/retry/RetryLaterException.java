package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class RetryLaterException extends Exception implements IRetryLaterException {

    private static final long serialVersionUID = 1L;

    public RetryLaterException() {
        super();
    }

    public RetryLaterException(final String message) {
        super(message);
    }

    public RetryLaterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RetryLaterException(final Throwable cause) {
        super(cause);
    }

}
