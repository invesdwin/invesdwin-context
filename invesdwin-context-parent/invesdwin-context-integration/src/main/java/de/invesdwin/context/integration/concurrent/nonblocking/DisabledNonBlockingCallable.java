package de.invesdwin.context.integration.concurrent.nonblocking;

import java.util.concurrent.Future;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.future.NullFuture;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public class DisabledNonBlockingCallable<V> implements INonBlockingCallable<V> {

    @SuppressWarnings("rawtypes")
    private static final DisabledNonBlockingCallable INSTANCE = new DisabledNonBlockingCallable();

    protected DisabledNonBlockingCallable() {}

    @Override
    public V call() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> DisabledNonBlockingCallable<T> getInstance() {
        return INSTANCE;
    }

    @Override
    public Future<V> maybeCall() {
        return NullFuture.getInstance();
    }

    @Override
    public V maybeCallAndGet() {
        return null;
    }

    @Override
    public V maybeCallAndRetry() {
        return null;
    }

    @Override
    public V getCallFuture(final Future<V> future) {
        return null;
    }

    @Override
    public V getCallFutureOrRetry(final Future<V> future, final Duration timeout) {
        return null;
    }

    @Override
    public void reset() {}

    @Override
    public void resetIfDone() {}

}
