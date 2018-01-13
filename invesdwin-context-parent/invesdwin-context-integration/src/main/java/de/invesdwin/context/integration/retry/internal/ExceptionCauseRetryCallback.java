package de.invesdwin.context.integration.retry.internal;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;

import de.invesdwin.context.integration.retry.RetryOriginator;
import de.invesdwin.context.integration.retry.hook.RetryHookManager;

@NotThreadSafe
public class ExceptionCauseRetryCallback<E> implements RetryCallback<E, Exception> {

    private final Callable<E> callable;
    private final RetryOriginator originator;
    private int retryCount;

    public ExceptionCauseRetryCallback(final Callable<E> callable, final RetryOriginator originator) {
        this.callable = callable;
        this.originator = originator;
    }

    @Override
    public E doWithRetry(final RetryContext retryContext) throws Exception {
        retryContext.setAttribute(RetryOriginator.ATTRIBUTE_RETRY_ORIGINATOR, originator);
        retryCount = retryContext.getRetryCount() + 1;
        try {
            final E ret = callable.call();
            if (retryContext.getRetryCount() > 0) {
                RetryHookManager.getEventTrigger().onRetrySucceeded(originator, retryContext.getRetryCount());
            }
            return ret;
        } catch (final Throwable e) {
            throw new WrappedRetryException(originator, retryContext.getRetryCount(), e);
        }
    }

    public RetryOriginator getOriginator() {
        return originator;
    }

    public int getRetryCount() {
        return retryCount;
    }

}