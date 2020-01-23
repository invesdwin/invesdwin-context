package de.invesdwin.context.jasperreports.internal;

import java.util.concurrent.ExecutorService;

import javax.annotation.concurrent.Immutable;

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
        return EXECUTOR;
    }

}
