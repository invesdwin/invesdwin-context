package de.invesdwin.context.integration.concurrent.nonblocking;

import java.util.concurrent.Future;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ListenableFuture;

import de.invesdwin.util.concurrent.future.NullFuture;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

@ThreadSafe
public abstract class ANonBlockingRunnableDaily extends ANonBlockingRunnable implements INonBlockingRunnableForce {

    private volatile FDate runFutureFinished = FDates.MIN_DATE;

    public ANonBlockingRunnableDaily(final Class<?> parentClass, final String parentInfo) {
        super(parentClass, parentInfo);
    }

    @Override
    public Future<?> maybeRun() {
        return maybeRun(false);
    }

    @Override
    public Future<?> maybeRun(final boolean force) {
        final FDate now = FDate.now();
        if (!force && FDates.isSameJulianDay(runFutureFinished, now)) {
            final Future<?> runFutureCopy = runFuture;
            if (runFutureCopy == null) {
                return NullFuture.getInstance();
            } else {
                return runFutureCopy;
            }
        }
        Future<?> runFutureCopy = runFuture;
        if (runFutureCopy != null && !shouldRunAgain(runFutureCopy)) {
            return runFutureCopy;
        }
        synchronized (this) {
            runFutureCopy = runFuture;
            if (runFutureCopy != null && !shouldRunAgain(runFutureCopy)) {
                return runFutureCopy;
            }
            if (!force && FDates.isSameJulianDay(runFutureFinished, now)) {
                if (runFutureCopy == null) {
                    return NullFuture.getInstance();
                } else {
                    return runFutureCopy;
                }
            }
            final ListenableFuture<?> future = getExecutor().submit(() -> {
                runDispatched(() -> {
                    run();
                    runFutureFinished = now;
                });
            });
            runFuture = future;
            return future;
        }
    }

    @Override
    public void maybeRunAndWait() {
        maybeRunAndWait(false);
    }

    @Override
    public void maybeRunAndWait(final boolean force) {
        if (isThreadNonBlocking()) {
            maybeRunAndRetry(force);
        } else if (isThreadInDispatchedCall()) {
            //this thread is itself running a call this tier dispatched: it is already dedicated to the work and may
            //hold the very store lock that a further dispatch would wait behind, so the run happens here. The other
            //branch has no timeout, so a dispatch that lands behind such a lock waits forever.
            final Future<?> finished = runFuture;
            if (finished != null && finished.isDone()) {
                getRunFuture(finished);
                return;
            }
            //an in-flight run on another thread is deliberately not joined: that wait is the cycle this branch exists
            //to remove, and the store's per-key claim turns the second run into a refusal rather than a double write
            final FDate now = FDate.now();
            if (!force && FDates.isSameJulianDay(runFutureFinished, now)) {
                //already done today - the dispatch path answers this by handing back a finished (or null) future
                return;
            }
            runDispatched(() -> {
                run();
                runFutureFinished = now;
            });
        } else {
            final Future<?> future = maybeRun(force);
            getRunFuture(future);
        }
    }

    @Override
    public void maybeRunAndRetry() {
        maybeRunAndRetry(false);
    }

    @Override
    public void maybeRunAndRetry(final boolean force) {
        final Future<?> future = maybeRun(force);
        getRunFutureOrRetry(future, getNonBlockingAsyncWaitTimeout());
    }

    @Override
    public void reset() {
        super.reset();
        runFutureFinished = FDates.MIN_DATE;
    }

}
