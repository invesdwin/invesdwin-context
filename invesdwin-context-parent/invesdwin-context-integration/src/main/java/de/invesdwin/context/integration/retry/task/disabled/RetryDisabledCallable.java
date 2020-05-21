package de.invesdwin.context.integration.retry.task.disabled;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.IntegrationProperties;
import de.invesdwin.util.concurrent.priority.IPriorityCallable;
import de.invesdwin.util.concurrent.priority.IPriorityProvider;

@ThreadSafe
public final class RetryDisabledCallable<T> implements IPriorityCallable<T> {

    private final Callable<T> delegate;

    private RetryDisabledCallable(final Callable<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T call() throws Exception {
        final boolean registerThreadRetryDisabled = IntegrationProperties.registerThreadRetryDisabled();
        try {
            return delegate.call();
        } finally {
            IntegrationProperties.unregisterThreadRetryDisabled(registerThreadRetryDisabled);
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

    public static <T> RetryDisabledCallable<T> of(final Callable<T> runnable) {
        return new RetryDisabledCallable<T>(runnable);
    }

    public static <T> List<RetryDisabledCallable<T>> of(final Collection<? extends Callable<T>> tasks) {
        final List<RetryDisabledCallable<T>> wrapped = new ArrayList<>(tasks.size());
        for (final Callable<T> task : tasks) {
            wrapped.add(RetryDisabledCallable.of(task));
        }
        return wrapped;
    }

}
