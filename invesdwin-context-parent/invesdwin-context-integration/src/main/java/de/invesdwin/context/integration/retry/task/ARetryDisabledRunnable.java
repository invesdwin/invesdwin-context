package de.invesdwin.context.integration.retry.task;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.Threads;

@ThreadSafe
public abstract class ARetryDisabledRunnable implements Runnable {

    @Override
    public final void run() {
        final Boolean registerThreadRetryDisabled = Threads.registerThreadRetryDisabled();
        try {
            runRetryDisabled();
        } finally {
            Threads.unregisterThreadRetryDisabled(registerThreadRetryDisabled);
        }
    }

    protected abstract void runRetryDisabled();

}
