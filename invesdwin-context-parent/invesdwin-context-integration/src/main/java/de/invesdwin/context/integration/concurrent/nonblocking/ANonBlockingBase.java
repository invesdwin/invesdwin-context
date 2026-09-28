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

    protected final String parentName;
    protected final String retryMessage;

    public ANonBlockingBase(final Class<?> parentClass, final String parentInfo) {
        this.parentName = getClass().getSimpleName();
        if (Strings.isBlank(parentName)) {
            throw new NullPointerException(
                    "parentClass.getSimpleName() should not be blank or null (no anonymous nested classes): "
                            + parentClass);
        }
        this.retryMessage = newRetryMessage(parentName, parentInfo);
    }

    private String newRetryMessage(final String parentName, final String taskNamePrefix) {
        final String taskName = getClass().getSimpleName();
        if (Strings.isBlank(taskName)) {
            throw new NullPointerException(
                    "getClass().getSimpleName() should not be blank or null (no anonymous nested classes): "
                            + getClass());
        }
        final StringBuilder sb = new StringBuilder(parentName);
        sb.append(": ");
        if (Strings.isNotBlank(taskNamePrefix)) {
            sb.append(taskNamePrefix);
            sb.append(": ");
        }
        sb.append(taskName);
        sb.append(" is running");
        return sb.toString();
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

    protected RuntimeException newRetryException(final Throwable cause) {
        return new FastNonBlockingRetryLaterRuntimeException(retryMessage, cause);
    }

    protected WrappedExecutorService getExecutor() {
        return UPDATE_EXECUTOR.getNestedExecutor(parentName);
    }

}
