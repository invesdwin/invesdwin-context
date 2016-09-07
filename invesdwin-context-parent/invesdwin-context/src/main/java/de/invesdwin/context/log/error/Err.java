package de.invesdwin.context.log.error;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.beans.init.InvesdwinInitializationProperties;
import de.invesdwin.context.log.Log;
import de.invesdwin.context.log.error.hook.ErrHookManager;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.Strings;

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

    static {
        final UncaughtExceptionHandler handler = new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                process(e, true);
            }
        };
        if (InvesdwinInitializationProperties.isInvesdwinInitializationAllowed()) {
            InvesdwinInitializationProperties.getInitializer().initDefaultUncaughtExceptionHandler(handler);
        }
        UNCAUGHT_EXCEPTION_HANDLER = handler;
    }

    private Err() {}

    /**
     * Logs an Exception as an error and returns it so that it can be analyzed or rethrown.
     */
    public static LoggedRuntimeException process(final Throwable e) {
        return process(e, false);
    }

    private static LoggedRuntimeException process(final Throwable e, final boolean uncaughtException) {
        try {
            if (e == null) {
                return null;
            }
            if (e instanceof LoggedRuntimeException) {
                return (LoggedRuntimeException) e;
            }

            final LoggedRuntimeException le = LoggedRuntimeException.newInstance(e);
            LOG.error(ThrowableConverter.throwableToString(le, false));
            LOG_DETAIL.error(ThrowableConverter.throwableToString(le, true));
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

}
