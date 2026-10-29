package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Unchecked counterpart of RetryLaterPendingException: a RetryLater that proves work is in flight. Producers
 * whose exception may travel over a wire boundary (where the subtype is lost) should also embed PENDING_TAG in
 * the message; isPending(Throwable) recognises both.
 */
@NotThreadSafe
public class RetryLaterPendingRuntimeException extends RetryLaterRuntimeException {

    /**
     * Message sentinel for pending RetryLaters that crossed a serialization boundary.
     */
    public static final String PENDING_TAG = "[PENDING]";

    private static final long serialVersionUID = 1L;

    public RetryLaterPendingRuntimeException(final String message) {
        super(message);
    }

    public RetryLaterPendingRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * True when the throwable chain carries a pending marker: either one of the RetryLaterPending subtypes or a
     * message containing PENDING_TAG (wire-restored exceptions keep the message text).
     */
    public static boolean isPending(final Throwable t) {
        Throwable cause = t;
        while (cause != null) {
            if (cause instanceof RetryLaterPendingException
                    || cause instanceof RetryLaterPendingRuntimeException) {
                return true;
            }
            final String msg = cause.getMessage();
            if (msg != null && msg.contains(PENDING_TAG)) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

}
