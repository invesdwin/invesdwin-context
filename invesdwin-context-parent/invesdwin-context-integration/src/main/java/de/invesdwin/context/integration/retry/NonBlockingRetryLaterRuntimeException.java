package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.log.error.IGuiHiddenException;

@Immutable
public class NonBlockingRetryLaterRuntimeException extends RetryLaterRuntimeException implements IGuiHiddenException {

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
