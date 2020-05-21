package de.invesdwin.context.integration.retry.task.disabled;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.concurrent.ADelegateExecutorService;

@ThreadSafe
public class RetryDisabledExecutorService extends ADelegateExecutorService {

    public RetryDisabledExecutorService(final ExecutorService delegate) {
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
