package de.invesdwin.context.integration.retry.hook;

import javax.annotation.concurrent.Immutable;
import jakarta.inject.Named;

import de.invesdwin.context.integration.retry.task.RetryOriginator;

@Immutable
@Named
public class RetryHookSupport implements IRetryHook {

    @Override
    public void onBeforeRetry(final RetryOriginator originator, final int retryCount, final Throwable cause) {

    }

    @Override
    public void onRetrySucceeded(final RetryOriginator originator, final int retryCount) {}

    @Override
    public void onRetryAborted(final RetryOriginator originator, final int retryCount, final Throwable cause) {}

}
