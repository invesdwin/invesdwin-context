package de.invesdwin.context.integration.retry.fast;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.retry.RetryLaterPendingRuntimeException;
import de.invesdwin.context.log.error.IHiddenException;
import de.invesdwin.util.error.Throwables;

/**
 * Stack-trace-free variant of RetryLaterPendingRuntimeException: it keeps the pending subtype, so
 * RetryLaterPendingRuntimeException.isPending(Throwable) still recognises the wait by type and not only by the
 * PENDING_TAG in the message, while fillInStackTrace() captures nothing unless
 * Throwables.DEBUG_STACK_TRACE_ENABLED is on. IHiddenException marks it as a skip verdict: on the Err/ThrowableConverter lane that
 * marker suppresses the whole chain's frames, so a Fast form must not wrap a genuine fault there;
 * logback's own rendering prints every cause's stack regardless of the wrapper's own frames.
 */
@NotThreadSafe
public class FastRetryLaterPendingRuntimeException extends RetryLaterPendingRuntimeException implements IHiddenException {

    private static final long serialVersionUID = 1L;

    public FastRetryLaterPendingRuntimeException(final String message) {
        super(message);
    }

    public FastRetryLaterPendingRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        if (isDebugStackTraceEnabled()) {
            return super.fillInStackTrace();
        }
        return this;
    }

    private static boolean isDebugStackTraceEnabled() {
        return Throwables.isDebugStackTraceEnabled();
    }

}
