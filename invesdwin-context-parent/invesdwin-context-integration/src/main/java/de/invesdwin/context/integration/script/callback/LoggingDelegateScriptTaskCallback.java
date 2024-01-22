package de.invesdwin.context.integration.script.callback;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.context.log.Log;

@Immutable
public class LoggingDelegateScriptTaskCallback implements IScriptTaskCallback {

    private final Log log;
    private final IScriptTaskCallback delegate;
    private final AtomicLong sequence = new AtomicLong();

    public LoggingDelegateScriptTaskCallback(final Log log, final IScriptTaskCallback delegate) {
        this.log = log;
        this.delegate = delegate;
    }

    @Override
    public void invoke(final String methodName, final IScriptTaskParameters parameters,
            final IScriptTaskReturns returns) {
        final long seq = sequence.incrementAndGet();
        log.debug("< callback(%s) < %s: %s", seq, methodName, parameters);
        delegate.invoke(methodName, parameters, returns);
        log.debug("> callback(%s) > %s", seq, returns);
    }

    public static IScriptTaskCallback maybeWrap(final Log log, final IScriptTaskCallback delegate) {
        if (log.isDebugEnabled()) {
            return new LoggingDelegateScriptTaskCallback(log, delegate);
        } else {
            return delegate;
        }
    }

}
