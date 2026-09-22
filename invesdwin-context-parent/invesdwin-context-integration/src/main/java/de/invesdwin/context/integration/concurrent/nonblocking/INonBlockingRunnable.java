package de.invesdwin.context.integration.concurrent.nonblocking;

import java.util.concurrent.Future;

import de.invesdwin.util.time.duration.Duration;

public interface INonBlockingRunnable extends Runnable, INonBlockingBase {

    Future<?> maybeRun();

    void maybeRunAndWait();

    void maybeRunAndRetry();

    void getRunFuture(Future<?> future);

    void getRunFutureOrRetry(Future<?> future, Duration timeout);

}
