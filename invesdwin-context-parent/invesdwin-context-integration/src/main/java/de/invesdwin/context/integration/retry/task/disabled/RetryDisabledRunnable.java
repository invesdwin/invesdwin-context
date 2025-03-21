package de.invesdwin.context.integration.retry.task.disabled;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.RetryThreads;
import de.invesdwin.util.concurrent.priority.IPriorityProvider;
import de.invesdwin.util.concurrent.priority.IPriorityRunnable;

@ThreadSafe
public final class RetryDisabledRunnable implements IPriorityRunnable {

    private final Runnable delegate;

    private RetryDisabledRunnable(final Runnable delegate) {
        this.delegate = delegate;
    }

    @Override
    public void run() {
        final Boolean registerThreadRetryDisabled = RetryThreads.registerThreadRetryDisabled();
        try {
            delegate.run();
        } finally {
            RetryThreads.unregisterThreadRetryDisabled(registerThreadRetryDisabled);
        }
    }

    @Override
    public double getPriority() {
        if (delegate instanceof IPriorityProvider) {
            final IPriorityProvider cDelegate = (IPriorityProvider) delegate;
            return cDelegate.getPriority();
        }
        return MISSING_PRIORITY;
    }

    public static RetryDisabledRunnable of(final Runnable runnable) {
        return new RetryDisabledRunnable(runnable);
    }

    public static List<RetryDisabledRunnable> of(final Collection<? extends Runnable> tasks) {
        final List<RetryDisabledRunnable> wrapped = new ArrayList<>(tasks.size());
        for (final Runnable task : tasks) {
            wrapped.add(RetryDisabledRunnable.of(task));
        }
        return wrapped;
    }

}
