package de.invesdwin.context.log.error;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.NoSuchElementException;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.context.log.Log;
import de.invesdwin.context.log.error.hook.ErrHookManager;
import de.invesdwin.util.collections.iterable.ICloseableIterator;
import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator;
import de.invesdwin.util.collections.iterable.buffer.NodeBufferingIterator.INode;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.duration.Duration;

/**
 * A util class to log exceptions and convert them to RuntimeExceptions that have an ID associated with them to be
 * rethrown.
 * 
 * @author subes
 * 
 */
@ThreadSafe
public final class Err {

    public static final UncaughtExceptionHandler UNCAUGHT_EXCEPTION_HANDLER;

    private static final Log LOG = new Log("de.invesdwin.ERROR");
    private static final Log LOG_DETAIL = new Log("de.invesdwin.ERROR_DETAIL");
    @GuardedBy("this.class")
    private static final NodeBufferingIterator<IntervalException> INTERVAL_EXCEPTIONS = new NodeBufferingIterator<>();

    private static final int MAX_INTERVAL_EXCEPTIONS = 1000;

    static {
        final UncaughtExceptionHandler handler = new ErrUncaughtExceptionHandler();
        if (PlatformInitializerProperties.isAllowed()) {
            PlatformInitializerProperties.getInitializer().initDefaultUncaughtExceptionHandler(handler);
        }
        UNCAUGHT_EXCEPTION_HANDLER = handler;
    }

    private Err() {
    }

    /**
     * Logs an Exception as an error and returns it so that it can be analyzed or rethrown.
     */
    public static LoggedRuntimeException process(final Throwable e) {
        return process(e, false);
    }

    static LoggedRuntimeException process(final Throwable e, final boolean uncaughtException) {
        try {
            if (e == null) {
                return null;
            }
            if (e instanceof LoggedRuntimeException) {
                return (LoggedRuntimeException) e;
            }

            final LoggedRuntimeException le = LoggedRuntimeException.newInstance(e);
            LOG.error(ThrowableConverter.loggedRuntimeExceptionToString(le, false));
            LOG_DETAIL.error(ThrowableConverter.loggedRuntimeExceptionToString(le, true));
            ErrHookManager.loggedException(le, uncaughtException);

            return le;
        } catch (final Throwable errorDuringLogging) {
            //Errors during logging should not be eaten! Thus we log it separately.
            LOG.catching(errorDuringLogging);
            LOG_DETAIL.catching(errorDuringLogging);
            LOG.catching(e);
            LOG_DETAIL.catching(e);
            throw LoggedRuntimeException.newInstance(e);
        }
    }

    /**
     * Checks two Exceptions for the same meaning. To do this, null, the class and the message is being checked.
     * 
     * <pre>
     * Err.isSameMeaning(null, null)   = true
     * Err.isSameMeaning(null, Throwable)  = false
     * Err.isSameMeaning(Throwable, null)  = false
     * Err.isSameMeaning(Throwable, Throwable) = true
     * Err.isSameMeaning(Throwable, Exception) = false
     * Err.isSameMeaning(Throwable("asd"), Throwable("asd")) = true
     * Err.isSameMeaning(Throwable("asd"), Throwable("asdf") = false
     * Err.isSameMeaning(Throwable("asd"), LoggedRuntimeException->Throwable("asd")) = true
     * </pre>
     */
    public static boolean isSameMeaning(final Throwable e1, final Throwable e2) {
        final Throwable ue1 = Throwables.ignoreType(e1, LoggedRuntimeException.class);
        final Throwable ue2 = Throwables.ignoreType(e2, LoggedRuntimeException.class);
        if (ue1 == ue2) {
            return true;
        } else if (ue1 == null || ue2 == null) {
            return false;
        } else if (!ue1.getClass().equals(ue2.getClass())) {
            return false;
        } else {
            return Strings.equals(ue1.getMessage(), ue2.getMessage());
        }
    }

    /**
     * Same as isSameMeaning() with the addition that the causes are checked for the same meaning by ignoring specific
     * parent types.
     */
    @SafeVarargs
    public static boolean isSameMeaningIgnoreType(final Throwable e1, final Throwable e2,
            final Class<? extends Throwable>... ignoredTypes) {
        final Throwable ue1 = Throwables.ignoreType(e1, ignoredTypes);
        final Throwable ue2 = Throwables.ignoreType(e2, ignoredTypes);
        return isSameMeaning(ue1, ue2);
    }

    /**
     * Same as isSameMeaning() with the addition that only causes with the same specified type are being checked.
     */
    public static boolean isSameMeaningForceType(final Throwable e1, final Throwable e2,
            final Class<? extends Throwable> forcedType) {
        final Throwable ue1 = Throwables.getCauseByType(e1, forcedType);
        final Throwable ue2 = Throwables.getCauseByType(e1, forcedType);
        return isSameMeaning(ue1, ue2);
    }

    public static String getDetailedStackTrace(final Throwable e) {
        return ThrowableConverter.throwableToString(e, true);
    }

    public static String getSimplifiedStackTrace(final Throwable e) {
        return ThrowableConverter.throwableToString(e, false);
    }

    /**
     * Only logs the exception in the given interval if it has the same meaning, otherwise returns the previous logged
     * exception and ignores the new one.
     */
    public static synchronized LoggedRuntimeException processInterval(final Exception exception,
            final Duration interval) {
        final ICloseableIterator<IntervalException> iterator = INTERVAL_EXCEPTIONS.iterator();
        try {
            while (true) {
                final IntervalException candidate = iterator.next();
                if (candidate.isTimeout()) {
                    iterator.remove();
                } else {
                    if (isSameMeaning(candidate.getException(), exception)) {
                        return candidate.getException();
                    }
                }
            }
        } catch (final NoSuchElementException e) {
            //end reached
        }
        final LoggedRuntimeException logged = process(exception);
        INTERVAL_EXCEPTIONS.add(new IntervalException(logged, interval));
        while (INTERVAL_EXCEPTIONS.size() > MAX_INTERVAL_EXCEPTIONS) {
            INTERVAL_EXCEPTIONS.next();
        }
        return logged;
    }

    private static final class IntervalException implements INode<IntervalException> {
        private final LoggedRuntimeException exception;
        private final Duration interval;
        private final Instant instant;
        private IntervalException next;

        private IntervalException(final LoggedRuntimeException exception, final Duration interval) {
            this.exception = exception;
            this.interval = interval;
            this.instant = new Instant();
        }

        public boolean isTimeout() {
            return instant.isGreaterThan(interval);
        }

        public LoggedRuntimeException getException() {
            return exception;
        }

        @Override
        public IntervalException getNext() {
            return next;
        }

        @Override
        public void setNext(final IntervalException next) {
            this.next = next;
        }

    }

}
