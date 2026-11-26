package de.invesdwin.context.integration.retry;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * A RetryLater whose cause is PROVABLY productive work in flight (an initial download running, a strategy still
 * initializing, an update lock held by a running updater). Retry budgets that should not punish waiting for such
 * work (see the non-pending retry count in the retry hooks) classify causes via
 * RetryLaterPendingRuntimeException#isPending(Throwable) - which also recognises the PENDING_TAG when the
 * exception crossed a wire boundary and lost its type.
 */
@NotThreadSafe
public class RetryLaterPendingException extends RetryLaterException {

    private static final long serialVersionUID = 1L;

    public RetryLaterPendingException(final String message) {
        super(message);
    }

    public RetryLaterPendingException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
