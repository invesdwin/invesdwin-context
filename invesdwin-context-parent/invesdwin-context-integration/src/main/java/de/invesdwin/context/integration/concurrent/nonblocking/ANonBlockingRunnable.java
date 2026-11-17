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

    public ANonBlockingRunnable(final Class<?> parentClass, final String parentInfo) {
        super(parentClass, parentInfo);
    }

    @Override
    public Future<?> maybeRun() {
        Future<?> runFutureCopy = runFuture;
        if (runFutureCopy != null && !shouldRunAgain(runFutureCopy)) {
            return runFutureCopy;
        }
        synchronized (this) {
            runFutureCopy = runFuture;
            if (runFutureCopy != null && !shouldRunAgain(runFutureCopy)) {
                return runFutureCopy;
            }
            final ListenableFuture<?> future = getExecutor().submit(() -> {
                runDispatched(this::run);
            });
            runFuture = future;
            return future;
        }
    }

    protected boolean shouldRunAgain(final Future<?> future) {
        //run again if already done, otherwise wait for this future to finish
        return future.isDone();
    }

    @Override
    public void maybeRunAndWait() {
        if (isThreadNonBlocking()) {
            maybeRunAndRetry();
        } else if (isThreadInDispatchedCall()) {
            //this thread is itself running a call this tier dispatched: it is already dedicated to the work and may
            //hold the very store lock that a further dispatch would wait behind, so the run happens here. The other
            //branch has no timeout, so a dispatch that lands behind such a lock waits forever.
            runDispatched(this::run);
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
