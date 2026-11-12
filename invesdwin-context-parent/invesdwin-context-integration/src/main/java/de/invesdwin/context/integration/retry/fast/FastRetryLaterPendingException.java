package de.invesdwin.context.integration.retry.fast;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.retry.RetryLaterPendingException;
import de.invesdwin.context.log.error.IHiddenException;
import de.invesdwin.util.error.Throwables;

/**
 * Stack-trace-free variant of RetryLaterPendingException, so a pending wait can be reported without capturing a
 * stack: RetryLaterPendingRuntimeException.isPending(Throwable) still recognises it by type, and
 * fillInStackTrace() captures nothing unless Throwables.DEBUG_STACK_TRACE_ENABLED is on. IHiddenException marks it as a skip verdict: on the Err/ThrowableConverter lane that
 * marker suppresses the whole chain's frames, so a Fast form must not wrap a genuine fault there;
 * logback's own rendering prints every cause's stack regardless of the wrapper's own frames.
 */
@NotThreadSafe
public class FastRetryLaterPendingException extends RetryLaterPendingException implements IHiddenException {

    private static final long serialVersionUID = 1L;

    public FastRetryLaterPendingException(final String message) {
        super(message);
    }

    public FastRetryLaterPendingException(final String message, final Throwable cause) {
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
