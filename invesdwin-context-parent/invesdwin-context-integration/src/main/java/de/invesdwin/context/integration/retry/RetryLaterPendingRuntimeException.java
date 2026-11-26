package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Unchecked counterpart of RetryLaterPendingException: a RetryLater that proves work is in flight. Producers
 * whose exception may travel over a wire boundary (where the subtype is lost) should also prefix the message with
 * PENDING_PREFIX; isPending(Throwable) recognises the PENDING_TAG in either shape.
 */
@NotThreadSafe
public class RetryLaterPendingRuntimeException extends RetryLaterRuntimeException {

    /**
     * Message sentinel for pending RetryLaters that crossed a serialization boundary - identity only, no separating
     * space, so it also matches messages produced before the space was introduced. Recognition goes through
     * isPending(Throwable); producers never concatenate this constant themselves.
     */
    public static final String PENDING_TAG = "[PENDING]";

    /**
     * PENDING_TAG with the separating space - rendering only. Producers concatenate PENDING_PREFIX with their
     * message so that the marker never glues to the text following it ('[PENDING]Strategy is ...' was the staging log
     * shape this fixes), and the space stays part of the marker rather than of the message.
     */
    public static final String PENDING_PREFIX = PENDING_TAG + " ";

    private static final long serialVersionUID = 1L;

    public RetryLaterPendingRuntimeException(final String message) {
        super(message);
    }

    public RetryLaterPendingRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * True when the throwable chain carries a pending marker: either one of the RetryLaterPending subtypes or a
     * message containing PENDING_TAG (wire-restored exceptions keep the message text). Matching is on the space-free
     * tag, so messages rendered with PENDING_PREFIX and older ones without the separating space both count.
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
