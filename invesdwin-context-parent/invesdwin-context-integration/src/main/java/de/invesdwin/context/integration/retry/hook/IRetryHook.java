package de.invesdwin.context.integration.retry.hook;

import de.invesdwin.context.integration.retry.task.RetryOriginator;

public interface IRetryHook {

    /**
     * Just throw another exception to abort the retry.
     */
    void onBeforeRetry(RetryOriginator originator, int retryCount, Throwable cause);

    /**
     * The cause is the one responsible for cancelling the retry.
     */
    void onRetryAborted(RetryOriginator originator, int retryCount, Throwable cause);

    /**
     * This method gets only called when a at least one retry happened.
     */
    void onRetrySucceeded(RetryOriginator originator, int retryCount);

}
