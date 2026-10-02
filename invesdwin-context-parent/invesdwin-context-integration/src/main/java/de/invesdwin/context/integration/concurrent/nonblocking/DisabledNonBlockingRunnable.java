package de.invesdwin.context.integration.concurrent.nonblocking;

import java.util.concurrent.Future;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.future.NullFuture;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public class DisabledNonBlockingRunnable implements INonBlockingRunnable {

    public static final DisabledNonBlockingRunnable INSTANCE = new DisabledNonBlockingRunnable();

    protected DisabledNonBlockingRunnable() {}

    @Override
    public void run() {}

    @Override
    public Future<?> maybeRun() {
        return NullFuture.getInstance();
    }

    @Override
    public void maybeRunAndWait() {}

    @Override
    public void maybeRunAndRetry() {}

    @Override
    public void getRunFuture(final Future<?> future) {}

    @Override
    public void getRunFutureOrRetry(final Future<?> future, final Duration timeout) {}

    @Override
    public void reset() {}

    @Override
    public void resetIfDone() {}

}
