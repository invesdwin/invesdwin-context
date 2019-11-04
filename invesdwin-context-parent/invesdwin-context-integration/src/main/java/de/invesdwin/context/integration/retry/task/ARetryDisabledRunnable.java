package de.invesdwin.context.integration.retry.task;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.IntegrationProperties;

@ThreadSafe
public abstract class ARetryDisabledRunnable implements Runnable {

    @Override
    public final void run() {
        final boolean registerThreadRetryDisabled = IntegrationProperties.registerThreadRetryDisabled();
        try {
            runRetryDisabled();
        } finally {
            IntegrationProperties.unregisterThreadRetryDisabled(registerThreadRetryDisabled);
        }
    }

    protected abstract void runRetryDisabled();

}
