package de.invesdwin.context.integration.retry.fast;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.retry.RetryLaterPendingRuntimeException;
import de.invesdwin.context.log.error.IHiddenException;
import de.invesdwin.util.error.Throwables;

/**
 * Stack-trace-free variant of RetryLaterPendingRuntimeException: it keeps the pending subtype, so
 * RetryLaterPendingRuntimeException.isPending(Throwable) still recognises the wait by type and not only by the
 * PENDING_TAG in the message, while fillInStackTrace() captures nothing unless
 * Throwables.DEBUG_STACK_TRACE_ENABLED is on. IHiddenException says the producer considers this a skip verdict and
 * not a fault, which is what LoggingRetryHook, ThrowableConverter and GuiExceptionHandler honour - so a Fast* form
 * must never be used to wrap a genuine fault.
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
