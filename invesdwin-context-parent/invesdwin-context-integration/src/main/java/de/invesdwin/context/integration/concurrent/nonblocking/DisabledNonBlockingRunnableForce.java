package de.invesdwin.context.integration.concurrent.nonblocking;

import java.util.concurrent.Future;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.future.NullFuture;

@Immutable
public class DisabledNonBlockingRunnableForce extends DisabledNonBlockingRunnable implements INonBlockingRunnableForce {

    public static final DisabledNonBlockingRunnableForce INSTANCE = new DisabledNonBlockingRunnableForce();

    protected DisabledNonBlockingRunnableForce() {}

    @Override
    public Future<?> maybeRun(final boolean force) {
        return NullFuture.getInstance();
    }

    @Override
    public void maybeRunAndWait(final boolean force) {}

    @Override
    public void maybeRunAndRetry(final boolean force) {}
}
