package de.invesdwin.context.integration.retry.task;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.RetryThreads;

@ThreadSafe
public abstract class ARetryDisabledRunnable implements Runnable {

    @Override
    public final void run() {
        final Boolean registerThreadRetryDisabled = RetryThreads.registerThreadRetryDisabled();
        try {
            runRetryDisabled();
        } finally {
            RetryThreads.unregisterThreadRetryDisabled(registerThreadRetryDisabled);
        }
    }

    protected abstract void runRetryDisabled();

}
