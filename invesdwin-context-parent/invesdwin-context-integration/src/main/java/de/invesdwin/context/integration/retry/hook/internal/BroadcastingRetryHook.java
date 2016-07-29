package de.invesdwin.context.integration.retry.hook.internal;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.retry.RetryOriginator;
import de.invesdwin.context.integration.retry.hook.IRetryHook;

@ThreadSafe
public class BroadcastingRetryHook implements IRetryHook {

    private final Set<IRetryHook> hooks = new CopyOnWriteArraySet<IRetryHook>();

    @Override
    public void onBeforeRetry(final RetryOriginator originator, final int retryCount, final Throwable cause) {
        for (final IRetryHook hook : hooks) {
            hook.onBeforeRetry(originator, retryCount, cause);
        }
    }

    @Override
    public void onRetryAborted(final RetryOriginator originator, final int retryCount, final Throwable cause) {
        for (final IRetryHook hook : hooks) {
            hook.onRetryAborted(originator, retryCount, cause);
        }
    }

    @Override
    public void onRetrySucceeded(final RetryOriginator originator, final int retryCount) {
        for (final IRetryHook hook : hooks) {
            hook.onRetrySucceeded(originator, retryCount);
        }
    }

    public boolean add(final IRetryHook hook) {
        return hooks.add(hook);
    }

    public boolean remove(final IRetryHook hook) {
        return hooks.remove(hook);
    }

}
