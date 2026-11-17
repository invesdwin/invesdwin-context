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
import io.netty.util.concurrent.FastThreadLocal;

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

    /**
     * True on a thread that is itself running a call this tier dispatched to {@link #UPDATE_EXECUTOR}. Such a thread is
     * already dedicated to the caller's work, so a further demand raised from it has to run on it rather than take
     * another slot in the same pool: the caller may hold a store lock that the dispatched worker would wait behind,
     * and a thread parked on a pool future while holding a lock is a cycle that no thread dump reports as one.
     */
    private static final FastThreadLocal<Boolean> THREAD_IN_DISPATCHED_CALL = new FastThreadLocal<>();

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

    protected static boolean isThreadInDispatchedCall() {
        return Boolean.TRUE.equals(THREAD_IN_DISPATCHED_CALL.get());
    }

    /**
     * Runs {@code supplier} the way a dispatched call runs it: blocking allowed, and marked as a dispatched call so
     * that a demand raised inside it does not dispatch either. Both the pool task and the inline branch use this, so
     * the two paths differ only in which thread runs the body.
     */
    protected <V> V callDispatched(final Supplier<V> supplier) {
        final Boolean before = THREAD_IN_DISPATCHED_CALL.get();
        THREAD_IN_DISPATCHED_CALL.set(Boolean.TRUE);
        try {
            return callBlockingAll(supplier);
        } finally {
            if (before == null) {
                THREAD_IN_DISPATCHED_CALL.remove();
            } else {
                THREAD_IN_DISPATCHED_CALL.set(before);
            }
        }
    }

    protected void runDispatched(final Runnable runnable) {
        callDispatched(() -> {
            runnable.run();
            return null;
        });
    }

}
