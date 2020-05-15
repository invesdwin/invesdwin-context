package de.invesdwin.context.integration.retry.internal;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffPolicy;

import de.invesdwin.context.integration.retry.ExceptionCauseRetryPolicy;
import de.invesdwin.context.integration.retry.hook.IRetryHook;
import de.invesdwin.context.integration.retry.hook.RetryHookManager;
import de.invesdwin.context.integration.retry.task.RetryOriginator;

@NotThreadSafe
public class ExceptionCauseRetryCallback<E> implements RetryCallback<E, Exception> {

    private final Callable<E> callable;
    private final RetryOriginator originator;
    private final BackOffPolicy backOffPolicyOverride;
    private int retryCount;
    private final IRetryHook retryListener;

    public ExceptionCauseRetryCallback(final Callable<E> callable, final RetryOriginator originator,
            final BackOffPolicy backOffPolicyOverride, final IRetryHook retryListener) {
        this.callable = callable;
        this.originator = originator;
        this.backOffPolicyOverride = backOffPolicyOverride;
        this.retryListener = retryListener;
    }

    public void open(final RetryContext retryContext) {
        retryContext.setAttribute(RetryOriginator.ATTRIBUTE_RETRY_ORIGINATOR, originator);
        if (backOffPolicyOverride != null) {
            retryContext.setAttribute(ExceptionCauseBackOffPolicy.ATTRIBUTE_BACK_OFF_POLICY_OVERRIDE,
                    backOffPolicyOverride);
        }
        if (retryListener != null) {
            retryContext.setAttribute(ExceptionCauseRetryPolicy.ATTRIBUTE_RETRY_LISTENER, retryListener);
        }
    }

    @Override
    public E doWithRetry(final RetryContext retryContext) throws Exception {
        retryCount = retryContext.getRetryCount();
        try {
            final E ret = callable.call();
            if (retryCount > 0) {
                RetryHookManager.getEventTrigger().onRetrySucceeded(originator, retryCount);
                if (retryListener != null) {
                    retryListener.onRetrySucceeded(originator, retryCount);
                }
            }
            return ret;
        } catch (final Throwable e) {
            throw new WrappedRetryException(originator, retryCount, e);
        }
    }

    public RetryOriginator getOriginator() {
        return originator;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public BackOffPolicy getBackOffPolicyOverride() {
        return backOffPolicyOverride;
    }

    public IRetryHook getRetryListener() {
        return retryListener;
    }

}