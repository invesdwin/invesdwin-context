package de.invesdwin.context.integration.concurrent.nonblocking;

import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ListenableFuture;

import de.invesdwin.util.concurrent.future.Futures;
import de.invesdwin.util.concurrent.lambda.IBooleanFunction;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public abstract class ANonBlockingCallable<V> extends ANonBlockingBase implements INonBlockingCallable<V> {

    protected volatile Future<V> callFuture;

    public ANonBlockingCallable(final Class<?> parentClass, final String parentInfo) {
        super(parentClass, parentInfo);
    }

    @Override
    public Future<V> maybeCall() {
        Future<V> callFutureCopy = callFuture;
        if (callFutureCopy != null && !shouldCallAgain(callFutureCopy)) {
            return callFutureCopy;
        }
        synchronized (this) {
            callFutureCopy = callFuture;
            if (callFutureCopy != null && !shouldCallAgain(callFutureCopy)) {
                return callFutureCopy;
            }
            final ListenableFuture<V> future = getExecutor().submit(() -> callBlockingAll(() -> {
                return call();
            }));
            callFuture = future;
            return future;
        }
    }

    protected boolean shouldCallAgain(final Future<V> future) {
        //        return future.isDone();
        //keep value until reset
        return false;
    }

    @Override
    public V maybeCallAndGet() {
        if (isThreadNonBlocking()) {
            return maybeCallAndRetry();
        } else {
            final Future<V> future = maybeCall();
            return getCallFuture(future);
        }
    }

    @Override
    public V maybeCallAndRetry() {
        final Future<V> future = maybeCall();
        return getCallFutureOrRetry(future, getNonBlockingAsyncWaitTimeout());
    }

    @Override
    public V getCallFuture(final Future<V> future) {
        try {
            return Futures.getNoInterrupt(future);
        } catch (final Throwable t) {
            synchronized (this) {
                if (callFuture == future) {
                    callFuture = null;
                }
            }
            throw Throwables.propagate(t);
        }
    }

    @Override
    public V getCallFutureOrRetry(final Future<V> future, final Duration timeout) {
        try {
            return Futures.getNoInterrupt(future, timeout);
        } catch (final TimeoutException e) {
            throw newRetryException(e);
        } catch (final Throwable t) {
            synchronized (this) {
                if (callFuture == future) {
                    callFuture = null;
                }
            }
            throw Throwables.propagate(t);
        }
    }

    @Override
    public void reset() {
        callFuture = null;
    }

    @Override
    public void resetIfDone(final IBooleanFunction<V> shouldReset) {
        Future<V> callFutureCopy = callFuture;
        if (callFutureCopy != null && callFutureCopy.isDone() && applyShouldReset(shouldReset, callFutureCopy)) {
            synchronized (this) {
                callFutureCopy = callFuture;
                if (callFutureCopy != null && callFutureCopy.isDone()
                        && applyShouldReset(shouldReset, callFutureCopy)) {
                    reset();
                }
            }
        }
    }

    private boolean applyShouldReset(final IBooleanFunction<V> shouldReset, final Future<V> callFutureCopy) {
        try {
            return shouldReset.apply(Futures.getNoInterrupt(callFutureCopy));
        } catch (final Throwable t) {
            return true;
        }
    }

}
