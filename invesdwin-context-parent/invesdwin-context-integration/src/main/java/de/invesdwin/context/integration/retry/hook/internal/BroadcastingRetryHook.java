package de.invesdwin.context.integration.retry.hook.internal;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.retry.hook.IRetryHook;
import de.invesdwin.context.integration.retry.task.RetryOriginator;
import de.invesdwin.util.collections.fast.concurrent.ASynchronizedFastIterableDelegateSet;

@ThreadSafe
public class BroadcastingRetryHook implements IRetryHook {

    private final ASynchronizedFastIterableDelegateSet<IRetryHook> hooks = new ASynchronizedFastIterableDelegateSet<IRetryHook>() {
        @Override
        protected Set<IRetryHook> newDelegate() {
            return new LinkedHashSet<IRetryHook>();
        }
    };

    @Override
    public void onBeforeRetry(final RetryOriginator originator, final int retryCount, final Throwable cause) {
        if (hooks.isEmpty()) {
            return;
        }
        final IRetryHook[] array = hooks.asArray(IRetryHook.EMPTY_ARRAY);
        for (int i = 0; i < array.length; i++) {
            final IRetryHook hook = array[i];
            hook.onBeforeRetry(originator, retryCount, cause);
        }
    }

    @Override
    public void onRetryAborted(final RetryOriginator originator, final int retryCount, final Throwable cause) {
        if (hooks.isEmpty()) {
            return;
        }
        final IRetryHook[] array = hooks.asArray(IRetryHook.EMPTY_ARRAY);
        for (int i = 0; i < array.length; i++) {
            final IRetryHook hook = array[i];
            hook.onRetryAborted(originator, retryCount, cause);
        }
    }

    @Override
    public void onRetrySucceeded(final RetryOriginator originator, final int retryCount) {
        if (hooks.isEmpty()) {
            return;
        }
        final IRetryHook[] array = hooks.asArray(IRetryHook.EMPTY_ARRAY);
        for (int i = 0; i < array.length; i++) {
            final IRetryHook hook = array[i];
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
