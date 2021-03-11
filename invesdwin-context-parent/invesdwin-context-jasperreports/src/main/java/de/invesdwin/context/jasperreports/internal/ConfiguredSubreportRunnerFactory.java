package de.invesdwin.context.jasperreports.internal;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.annotation.concurrent.Immutable;

import com.google.common.util.concurrent.ListeningExecutorService;

import de.invesdwin.util.concurrent.ASimpleDelegateExecutorService;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import de.invesdwin.util.concurrent.nested.ANestedExecutor;
import net.sf.jasperreports.engine.fill.JRFillContext;
import net.sf.jasperreports.engine.fill.ThreadPoolSubreportRunnerFactory;

@Immutable
public class ConfiguredSubreportRunnerFactory extends ThreadPoolSubreportRunnerFactory {

    //prevent deadlocks by using caller runs with nested reports
    private static final ANestedExecutor EXECUTOR = new ANestedExecutor(
            ConfiguredSubreportRunnerFactory.class.getSimpleName()) {
        @Override
        protected WrappedExecutorService newNestedExecutor(final String nestedName) {
            return Executors.newFixedThreadPool(nestedName, Executors.getCpuThreadPoolCount());
        }
    };

    @Override
    protected ExecutorService createThreadExecutor(final JRFillContext fillContext) {
        return new ASimpleDelegateExecutorService(null) {

            @Override
            public ListeningExecutorService getDelegate() {
                return EXECUTOR.getNestedExecutor();
            }

            @Override
            protected Runnable newRunnable(final Runnable runnable) {
                return runnable;
            }

            @Override
            protected <T> Callable<T> newCallable(final Callable<T> callable) {
                return callable;
            }

            @Override
            public void shutdown() {
                try {
                    final WrappedExecutorService delegate = (WrappedExecutorService) getDelegate();
                    delegate.awaitPendingCountZero();
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public List<Runnable> shutdownNow() {
                //noop
                return Collections.emptyList();
            }

        };
    }

}
