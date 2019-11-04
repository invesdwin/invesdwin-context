package de.invesdwin.context.integration.retry.internal;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.retry.task.RetryOriginator;

@ThreadSafe
public class WrappedRetryException extends Exception {
    private static final long serialVersionUID = 1L;

    private final RetryOriginator originator;
    private final int retryCount;

    public WrappedRetryException(final RetryOriginator originator, final int retryCount, final Throwable cause) {
        super(cause);
        this.originator = originator;
        this.retryCount = retryCount;
    }

    public RetryOriginator getOriginator() {
        return originator;
    }

    public int getRetryCount() {
        return retryCount;
    }

}
