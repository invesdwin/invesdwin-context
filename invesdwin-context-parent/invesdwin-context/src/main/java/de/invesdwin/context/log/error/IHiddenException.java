package de.invesdwin.context.log.error;

/**
 * Marker interface for exceptions that should not be shown in the UI, these exceptions will also hide their stack
 * traces in the common.log/console, only error.log will contain full logs. Though Throwables.isDebugStackTraceEnabled()
 * will enable full stack traces again in the common.log/console.
 */
public interface IHiddenException {

}
