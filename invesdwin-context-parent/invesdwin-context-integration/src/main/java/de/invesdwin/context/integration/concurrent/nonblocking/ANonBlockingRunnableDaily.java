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

    public ANonBlockingRunnableDaily(final Class<?> parentClass, final String taskName) {
        super(parentClass, taskName);
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
        if (runFutureCopy != null && !runFutureCopy.isDone()) {
            return runFutureCopy;
        }
        synchronized (this) {
            runFutureCopy = runFuture;
            if (runFutureCopy != null && !runFutureCopy.isDone()) {
                return runFutureCopy;
            }
            if (!force && FDates.isSameJulianDay(runFutureFinished, now)) {
                if (runFutureCopy == null) {
                    return NullFuture.getInstance();
                } else {
                    return runFutureCopy;
                }
            }
            final ListenableFuture<?> future = getExecutor().submit(() -> callBlockingAll(() -> {
                run();
                runFutureFinished = now;
            }));
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
