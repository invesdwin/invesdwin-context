package de.invesdwin.context.log;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Marker;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.LogLevel;
import de.invesdwin.util.log.modify.FormattedDelegateLog;

/**
 * A utility that provides standard mechanisms for logging certain kinds of activities.
 * 
 * Warnings over Var-Args overridings of non-Var-Args method can be ignored because the impl only calls the overwritten
 * methods.
 * 
 * The simplified syntax from http://code.google.com/p/log5j/ is not being implemented here, because the identification
 * of the callers via stacktrade does not resolve inheritance.
 * 
 * This class directly contains factory methods in its constructor so that there is no need to import two classes
 * everywhere.
 * 
 * @author subes
 * 
 */
@ThreadSafe
public final class Log extends org.slf4j.ext.XLogger implements ILog {

    static {
        if (PlatformInitializerProperties.isAllowed()) {
            try {
                Assertions.assertThat(ContextProperties.getLogDirectory()).isNotNull();
            } catch (final Throwable t) {
                PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
            }
        }
    }

    public Log(final String name) {
        super(org.slf4j.LoggerFactory.getLogger(name));
    }

    public Log(final Class<?> clazz) {
        this(clazz.getName());
    }

    public Log(final Object obj) {
        this(obj.getClass());
    }

    @Override
    public void trace(final String format, final Object... args) {
        super.trace(format(LogLevel.TRACE, format, args), args);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object... args) {
        super.trace(marker, format(LogLevel.TRACE, format, args), args);
    }

    @Override
    public void debug(final String format, final Object... args) {
        super.debug(format(LogLevel.DEBUG, format, args), args);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object... args) {
        super.debug(marker, format(LogLevel.DEBUG, format, args), args);
    }

    @Override
    public void info(final String format, final Object... args) {
        super.info(format(LogLevel.INFO, format, args), args);
    }

    @Override
    public void info(final Marker marker, final String format, final Object... args) {
        super.info(marker, format(LogLevel.INFO, format, args), args);
    }

    @Override
    public void warn(final String format, final Object... args) {
        super.warn(format(LogLevel.WARN, format, args), args);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object... args) {
        super.warn(marker, format(LogLevel.WARN, format, args), args);
    }

    @Override
    public void error(final String format, final Object... args) {
        super.error(format(LogLevel.ERROR, format, args), args);
    }

    @Override
    public void error(final Marker marker, final String format, final Object... args) {
        super.error(marker, format(LogLevel.ERROR, format, args), args);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg) {
        super.debug(marker, format(LogLevel.DEBUG, format, arg), arg);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg1, final Object arg2) {
        super.debug(marker, format(LogLevel.DEBUG, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void debug(final String format, final Object arg) {
        super.debug(format(LogLevel.DEBUG, format, arg), arg);
    }

    @Override
    public void debug(final String format, final Object arg1, final Object arg2) {
        super.debug(format(LogLevel.DEBUG, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg) {
        super.error(marker, format(LogLevel.ERROR, format, arg), arg);
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg1, final Object arg2) {
        super.error(marker, format(LogLevel.ERROR, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void error(final String format, final Object arg) {
        super.error(format(LogLevel.ERROR, format, arg), arg);
    }

    @Override
    public void error(final String format, final Object arg1, final Object arg2) {
        super.error(format(LogLevel.ERROR, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg) {
        super.info(marker, format(LogLevel.INFO, format, arg), arg);
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
        super.info(marker, format(LogLevel.INFO, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void info(final String format, final Object arg) {
        super.info(format(LogLevel.INFO, format, arg), arg);
    }

    @Override
    public void info(final String format, final Object arg1, final Object arg2) {
        super.info(format(LogLevel.INFO, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg) {
        super.trace(marker, format(LogLevel.TRACE, format, arg), arg);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg1, final Object arg2) {
        super.trace(marker, format(LogLevel.TRACE, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void trace(final String format, final Object arg) {
        super.trace(format(LogLevel.TRACE, format, arg), arg);
    }

    @Override
    public void trace(final String format, final Object arg1, final Object arg2) {
        super.trace(format(LogLevel.TRACE, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg) {
        super.warn(marker, format(LogLevel.WARN, format, arg), arg);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
        super.warn(marker, format(LogLevel.WARN, format, arg1, arg2), arg1, arg2);
    }

    @Override
    public void warn(final String format, final Object arg) {
        super.warn(format(LogLevel.WARN, format, arg), arg);
    }

    @Override
    public void warn(final String format, final Object arg1, final Object arg2) {
        super.warn(format(LogLevel.WARN, format, arg1, arg2), arg1, arg2);
    }

    private String format(final LogLevel level, final String messagePattern, final Object arg) {
        if (!level.isEnabled(logger)) {
            return messagePattern;
        }
        return FormattedDelegateLog.format(messagePattern, new Object[] { arg });
    }

    private String format(final LogLevel level, final String messagePattern, final Object arg1, final Object arg2) {
        if (!level.isEnabled(logger)) {
            return messagePattern;
        }
        return FormattedDelegateLog.format(messagePattern, new Object[] { arg1, arg2 });
    }

    private String format(final LogLevel level, final String messagePattern, final Object[] argArray) {
        if (!level.isEnabled(logger)) {
            return messagePattern;
        }
        return FormattedDelegateLog.format(messagePattern, argArray);
    }

}
