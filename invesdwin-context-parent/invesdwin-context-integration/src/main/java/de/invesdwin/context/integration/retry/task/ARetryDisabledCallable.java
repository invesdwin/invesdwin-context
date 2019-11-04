package de.invesdwin.context.integration.retry.task;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.IntegrationProperties;

@ThreadSafe
public abstract class ARetryDisabledCallable<E> implements Callable<E> {

    @Override
    public final E call() {
        final boolean registerRetryDisabled = IntegrationProperties.registerThreadRetryDisabled();
        try {
            return callRetryDisabled();
        } finally {
            IntegrationProperties.unregisterThreadRetryDisabled(registerRetryDisabled);
        }
    }

    protected abstract E callRetryDisabled();

}
