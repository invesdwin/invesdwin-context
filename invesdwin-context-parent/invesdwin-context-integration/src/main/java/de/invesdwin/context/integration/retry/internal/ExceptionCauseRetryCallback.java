package de.invesdwin.context.integration.retry.internal;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffPolicy;

import de.invesdwin.context.integration.retry.hook.RetryHookManager;
import de.invesdwin.context.integration.retry.task.RetryOriginator;

@NotThreadSafe
public class ExceptionCauseRetryCallback<E> implements RetryCallback<E, Exception> {

    private final Callable<E> callable;
    private final RetryOriginator originator;
    private final BackOffPolicy backOffPolicyOverride;
    private int retryCount;

    public ExceptionCauseRetryCallback(final Callable<E> callable, final RetryOriginator originator,
            final BackOffPolicy backOffPolicyOverride) {
        this.callable = callable;
        this.originator = originator;
        this.backOffPolicyOverride = backOffPolicyOverride;
    }

    public void open(final RetryContext retryContext) {
        retryContext.setAttribute(RetryOriginator.ATTRIBUTE_RETRY_ORIGINATOR, originator);
        if (backOffPolicyOverride != null) {
            retryContext.setAttribute(ExceptionCauseBackOffPolicy.ATTRIBUTE_BACK_OFF_POLICY_OVERRIDE,
                    backOffPolicyOverride);
        }
    }

    @Override
    public E doWithRetry(final RetryContext retryContext) throws Exception {
        retryCount = retryContext.getRetryCount();
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

    public BackOffPolicy getBackOffPolicyOverride() {
        return backOffPolicyOverride;
    }

}