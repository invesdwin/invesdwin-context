package de.invesdwin.context.integration.concurrent.nonblocking;

import java.util.concurrent.Future;

public interface INonBlockingRunnableForce extends INonBlockingRunnable {

    Future<?> maybeRun(boolean force);

    void maybeRunAndWait(boolean force);

    void maybeRunAndRetry(boolean force);

}
