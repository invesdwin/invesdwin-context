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
     * Message sentinel for pending RetryLaters that crossed a serialization boundary. Includes the separating space so
     * that the marker never glues to the text following it in a rendered message ('[PENDING]Strategy is ...' was the
     * staging log shape this fixes); every producer concatenates PENDING_TAG with its message, so the space is part of
     * the sentinel and not of the message.
     */
    public static final String PENDING_TAG = "[PENDING] ";

    /**
     * PENDING_TAG without the separating space. Recognition only: a public constant is inlined at compile time, so
     * producers built before the space was added keep emitting this shape over the wire and stay pending-detected.
     */
    private static final String PENDING_TAG_WITHOUT_SPACE = "[PENDING]";

    private static final long serialVersionUID = 1L;

    public RetryLaterPendingRuntimeException(final String message) {
        super(message);
    }

    public RetryLaterPendingRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * True when the throwable chain carries a pending marker: either one of the RetryLaterPending subtypes or a
     * message containing PENDING_TAG (wire-restored exceptions keep the message text). Matching ignores the
     * separating space, so producers compiled before it was added are still recognised.
     */
    public static boolean isPending(final Throwable t) {
        Throwable cause = t;
        while (cause != null) {
            if (cause instanceof RetryLaterPendingException
                    || cause instanceof RetryLaterPendingRuntimeException) {
                return true;
            }
            final String msg = cause.getMessage();
            if (msg != null && msg.contains(PENDING_TAG_WITHOUT_SPACE)) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

}
