package de.invesdwin.context.integration.retry.task.disabled;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ListeningExecutorService;

import de.invesdwin.util.concurrent.ASimpleDelegateExecutorService;

@ThreadSafe
public class RetryDisabledExecutorService extends ASimpleDelegateExecutorService {

    public RetryDisabledExecutorService(final ListeningExecutorService delegate) {
        super(delegate);
    }

    @Override
    protected Runnable newRunnable(final Runnable runnable) {
        return RetryDisabledRunnable.of(runnable);
    }

    @Override
    protected <T> Callable<T> newCallable(final Callable<T> callable) {
        return RetryDisabledCallable.of(callable);
    }

}
