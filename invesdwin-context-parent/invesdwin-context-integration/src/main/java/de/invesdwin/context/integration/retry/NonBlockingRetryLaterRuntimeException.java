package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.Immutable;


/**
 * A RetryLater that must not queue request threads behind the work that holds the resource. The hidden marker is not
 * on this type: quietness is declared by the stack-trace-free variant fast.FastNonBlockingRetryLaterRuntimeException,
 * which producers on a hot path throw.
 */
@Immutable
public class NonBlockingRetryLaterRuntimeException extends RetryLaterRuntimeException {

    public NonBlockingRetryLaterRuntimeException() {
        super();
    }

    public NonBlockingRetryLaterRuntimeException(final String message) {
        super(message);
    }

    public NonBlockingRetryLaterRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NonBlockingRetryLaterRuntimeException(final Throwable cause) {
        super(cause);
    }

}
