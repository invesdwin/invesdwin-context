package de.invesdwin.context.integration.script.callback;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.log.Log;

@Immutable
public class LoggingDelegateScriptTaskCallback implements IScriptTaskCallback {

    private static final AtomicLong INSTANCES = new AtomicLong();
    private final Log log;
    private final IScriptTaskCallback delegate;
    private final long instance;
    private final AtomicLong requests = new AtomicLong();

    public LoggingDelegateScriptTaskCallback(final Log log, final IScriptTaskCallback delegate) {
        this.log = log;
        this.delegate = delegate;
        this.instance = INSTANCES.incrementAndGet();
        log.debug("callback(%s-%s) <= %s", instance, requests.get(), delegate);
    }

    @Override
    public void invoke(final String methodName, final IScriptTaskParameters parameters,
            final IScriptTaskReturns returns) {
        final long request = requests.incrementAndGet();
        log.debug("< callback(%s-%s) < %s: %s", instance, request, methodName, parameters);
        delegate.invoke(methodName, parameters, returns);
        log.debug("> callback(%s-%s) > %s", instance, request, returns);
    }

    public static IScriptTaskCallback maybeWrap(final Log log, final IScriptTaskCallback delegate) {
        if (log.isDebugEnabled()) {
            return new LoggingDelegateScriptTaskCallback(log, delegate);
        } else {
            return delegate;
        }
    }

}
