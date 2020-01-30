package de.invesdwin.context.jasperreports.internal;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.concurrent.ADelegateExecutorService;
import de.invesdwin.util.concurrent.Executors;
import de.invesdwin.util.concurrent.WrappedExecutorService;
import net.sf.jasperreports.engine.fill.JRFillContext;
import net.sf.jasperreports.engine.fill.ThreadPoolSubreportRunnerFactory;

@Immutable
public class ConfiguredSubreportRunnerFactory extends ThreadPoolSubreportRunnerFactory {

    private static final WrappedExecutorService EXECUTOR = Executors
            .newFixedThreadPool(ConfiguredSubreportRunnerFactory.class.getSimpleName(),
                    Executors.getCpuThreadPoolCount())
            .withDynamicThreadName(false);

    @Override
    protected ExecutorService createThreadExecutor(final JRFillContext fillContext) {
        return new ADelegateExecutorService(EXECUTOR) {

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
                    EXECUTOR.awaitPendingCount(0);
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
