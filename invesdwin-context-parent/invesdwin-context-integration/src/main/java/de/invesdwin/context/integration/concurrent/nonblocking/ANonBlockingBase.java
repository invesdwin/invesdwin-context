package de.invesdwin.context.integration.concurrent.nonblocking;

import java.util.function.Supplier;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.integration.IntegrationProperties;
import de.invesdwin.context.integration.concurrent.FinancialdataThreads;
import de.invesdwin.context.integration.retry.fast.FastNonBlockingRetryLaterRuntimeException;
import de.invesdwin.context.log.error.handler.AlwaysErrExecutorExceptionHandler;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.nested.ANestedExecutor;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public abstract class ANonBlockingBase {

    public static final ANestedExecutor UPDATE_EXECUTOR = new ANestedExecutor(
            ANonBlockingBase.class.getSimpleName() + "_UPDATE") {
        @Override
        protected WrappedExecutorService newNestedExecutor(final String nestedName) {
            return Executors.newFixedThreadPool(nestedName, Executors.getCpuThreadPoolCount())
                    .setExecutorExceptionHandler(AlwaysErrExecutorExceptionHandler.INSTANCE);
        }
    };

    protected String executorName;
    protected final String retryMessage;

    public ANonBlockingBase(final Class<?> parentClass, final String taskName) {
        this.executorName = getClass().getSimpleName();
        if (Strings.isBlank(executorName)) {
            throw new NullPointerException(
                    "this.getClass().getSimpleName() should not be blank or null (no anonymous nested classes): "
                            + executorName);
        }
        if (Strings.isBlank(taskName)) {
            throw new NullPointerException("taskName should not be blank or null: " + taskName);
        }
        this.retryMessage = parentClass.getSimpleName() + ": " + taskName + " is running";
    }

    protected <T> T callBlockingAll(final Supplier<T> supplier) {
        return FinancialdataThreads.callBlockingAll(supplier);
    }

    protected void callBlockingAll(final Runnable runnable) {
        FinancialdataThreads.callBlockingAll(runnable);
    }

    protected boolean isThreadNonBlocking() {
        return FinancialdataThreads.isThreadBlockingUpdateDatabaseDisabled();
    }

    protected Duration getNonBlockingAsyncWaitTimeout() {
        return IntegrationProperties.NON_BLOCKING_ASYNC_WAIT_TIMEOUT;
    }

    protected RuntimeException newRetryException() {
        return new FastNonBlockingRetryLaterRuntimeException(retryMessage);
    }

    protected WrappedExecutorService getExecutor() {
        return UPDATE_EXECUTOR.getNestedExecutor(executorName);
    }

}
