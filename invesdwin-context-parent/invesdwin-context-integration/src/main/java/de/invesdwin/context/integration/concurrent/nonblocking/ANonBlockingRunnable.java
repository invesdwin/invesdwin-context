package de.invesdwin.context.integration.concurrent.nonblocking;

import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ListenableFuture;

import de.invesdwin.util.concurrent.future.Futures;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public abstract class ANonBlockingRunnable extends ANonBlockingBase implements INonBlockingRunnable {

    protected volatile Future<?> runFuture;

    public ANonBlockingRunnable(final Class<?> parentClass, final String taskName) {
        super(parentClass, taskName);
    }

    @Override
    public Future<?> maybeRun() {
        Future<?> runFutureCopy = runFuture;
        if (runFutureCopy != null && !runFutureCopy.isDone()) {
            return runFutureCopy;
        }
        synchronized (this) {
            runFutureCopy = runFuture;
            if (runFutureCopy != null && !runFutureCopy.isDone()) {
                return runFutureCopy;
            }
            final ListenableFuture<?> future = getExecutor().submit(() -> callBlockingAll(() -> {
                run();
            }));
            runFuture = future;
            return future;
        }
    }

    @Override
    public void maybeRunAndWait() {
        if (isThreadNonBlocking()) {
            maybeRunAndRetry();
        } else {
            final Future<?> future = maybeRun();
            getRunFuture(future);
        }
    }

    @Override
    public void maybeRunAndRetry() {
        final Future<?> future = maybeRun();
        getRunFutureOrRetry(future, getNonBlockingAsyncWaitTimeout());
    }

    @Override
    public void getRunFuture(final Future<?> future) {
        try {
            Futures.getNoInterrupt(future);
        } catch (final Throwable t) {
            synchronized (this) {
                if (runFuture == future) {
                    runFuture = null;
                }
            }
            throw Throwables.propagate(t);
        }
    }

    @Override
    public void getRunFutureOrRetry(final Future<?> future, final Duration timeout) {
        try {
            Futures.getNoInterrupt(future, timeout);
        } catch (final TimeoutException e) {
            throw newRetryException(e);
        } catch (final Throwable t) {
            synchronized (this) {
                if (runFuture == future) {
                    runFuture = null;
                }
            }
            throw Throwables.propagate(t);
        }
    }

    @Override
    public void reset() {
        runFuture = null;
    }

    @Override
    public void resetIfDone() {
        Future<?> runFutureCopy = runFuture;
        if (runFutureCopy != null && runFutureCopy.isDone()) {
            synchronized (this) {
                runFutureCopy = runFuture;
                if (runFutureCopy != null && runFutureCopy.isDone()) {
                    reset();
                }
            }
        }
    }

}
