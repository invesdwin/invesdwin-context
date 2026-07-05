package de.invesdwin.context.log;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.logging.log4j.LogManager;

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
public final class Log implements ILog {

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
        this.delegate = LogManager.getLogger(name);
    }

    public Log(final Class<?> clazz) {
        this(clazz.getName());
    }

    public Log(final Object obj) {
        this(obj.getClass());
    }

    @Override
    public void trace(final String format, final Object... params) {
        super.trace(format(LogLevel.TRACE, format, params), params);
    }

    @Override
    public void debug(final String format, final Object... params) {
        super.debug(format(LogLevel.DEBUG, format, params), params);
    }

    @Override
    public void info(final String format, final Object... params) {
        super.info(format(LogLevel.INFO, format, params), params);
    }

    @Override
    public void warn(final String format, final Object... params) {
        super.warn(format(LogLevel.WARN, format, params), params);
    }

    @Override
    public void error(final String format, final Object... params) {
        super.error(format(LogLevel.ERROR, format, params), params);
    }

    @Override
    public void debug(final String format, final Object p0) {
        super.debug(format(LogLevel.DEBUG, format, p0), p0);
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1) {
        super.debug(format(LogLevel.DEBUG, format, p0, p1), p0, p1);
    }

    @Override
    public void error(final String format, final Object p0) {
        super.error(format(LogLevel.ERROR, format, p0), p0);
    }

    @Override
    public void error(final String format, final Object p0, final Object p1) {
        super.error(format(LogLevel.ERROR, format, p0, p1), p0, p1);
    }

    @Override
    public void info(final String format, final Object p0) {
        super.info(format(LogLevel.INFO, format, p0), p0);
    }

    @Override
    public void info(final String format, final Object p0, final Object p1) {
        super.info(format(LogLevel.INFO, format, p0, p1), p0, p1);
    }

    @Override
    public void trace(final String format, final Object p0) {
        super.trace(format(LogLevel.TRACE, format, p0), p0);
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1) {
        super.trace(format(LogLevel.TRACE, format, p0, p1), p0, p1);
    }

    @Override
    public void warn(final String format, final Object p0) {
        super.warn(format(LogLevel.WARN, format, p0), p0);
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1) {
        super.warn(format(LogLevel.WARN, format, p0, p1), p0, p1);
    }

    private String format(final LogLevel level, final String messagePattern, final Object p0) {
        if (!level.isEnabled(logger)) {
            return messagePattern;
        }
        return FormattedDelegateLog.format(messagePattern, new Object[] { p0 });
    }

    private String format(final LogLevel level, final String messagePattern, final Object p0, final Object p1) {
        if (!level.isEnabled(logger)) {
            return messagePattern;
        }
        return FormattedDelegateLog.format(messagePattern, new Object[] { p0, p1 });
    }

    private String format(final LogLevel level, final String messagePattern, final Object[] params) {
        if (!level.isEnabled(logger)) {
            return messagePattern;
        }
        return FormattedDelegateLog.format(messagePattern, params);
    }

}
