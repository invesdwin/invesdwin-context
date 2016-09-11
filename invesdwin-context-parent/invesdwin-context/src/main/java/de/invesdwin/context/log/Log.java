package de.invesdwin.context.log;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Marker;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.Strings;

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
public final class Log extends org.slf4j.ext.XLogger {

    static {
        if (PlatformInitializerProperties.isAllowed()) {
            try {
                Assertions.assertThat(ContextProperties.getLogDirectory()).isNotNull();
            } catch (final Throwable t) {
                PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
            }
        }
    }

    public Log(@Nonnull final String name) {
        super(org.slf4j.LoggerFactory.getLogger(name));
    }

    public Log(@Nonnull final Class<?> clazz) {
        this(clazz.getName());
    }

    public Log(@Nonnull final Object obj) {
        this(obj.getClass());
    }

    @Override
    public void trace(final String format, final Object... args) {
        super.trace(replacePercentS(format), args);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object... args) {
        super.trace(marker, replacePercentS(format), args);
    }

    @Override
    public void debug(final String format, final Object... args) {
        super.debug(replacePercentS(format), args);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object... args) {
        super.debug(marker, replacePercentS(format), args);
    }

    @Override
    public void info(final String format, final Object... args) {
        super.info(replacePercentS(format), args);
    }

    @Override
    public void info(final Marker marker, final String format, final Object... args) {
        super.info(marker, replacePercentS(format), args);
    }

    @Override
    public void warn(final String format, final Object... args) {
        super.warn(replacePercentS(format), args);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object... args) {
        super.warn(marker, replacePercentS(format), args);
    }

    @Override
    public void error(final String format, final Object... args) {
        super.error(replacePercentS(format), args);
    }

    @Override
    public void error(final Marker marker, final String format, final Object... args) {
        super.error(marker, replacePercentS(format), args);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg) {
        super.debug(marker, replacePercentS(format), arg);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg1, final Object arg2) {
        super.debug(marker, replacePercentS(format), arg1, arg2);
    }

    @Override
    public void debug(final String format, final Object arg) {
        super.debug(replacePercentS(format), arg);
    }

    @Override
    public void debug(final String format, final Object arg1, final Object arg2) {
        super.debug(replacePercentS(format), arg1, arg2);
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg) {
        super.error(marker, replacePercentS(format), arg);
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg1, final Object arg2) {
        super.error(marker, replacePercentS(format), arg1, arg2);
    }

    @Override
    public void error(final String format, final Object arg) {
        super.error(replacePercentS(format), arg);
    }

    @Override
    public void error(final String format, final Object arg1, final Object arg2) {
        super.error(replacePercentS(format), arg1, arg2);
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg) {
        super.info(marker, replacePercentS(format), arg);
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
        super.info(marker, replacePercentS(format), arg1, arg2);
    }

    @Override
    public void info(final String format, final Object arg) {
        super.info(replacePercentS(format), arg);
    }

    @Override
    public void info(final String format, final Object arg1, final Object arg2) {
        super.info(replacePercentS(format), arg1, arg2);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg) {
        super.trace(marker, replacePercentS(format), arg);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg1, final Object arg2) {
        super.trace(marker, replacePercentS(format), arg1, arg2);
    }

    @Override
    public void trace(final String format, final Object arg) {
        super.trace(replacePercentS(format), arg);
    }

    @Override
    public void trace(final String format, final Object arg1, final Object arg2) {
        super.trace(replacePercentS(format), arg1, arg2);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg) {
        super.warn(marker, replacePercentS(format), arg);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
        super.warn(marker, replacePercentS(format), arg1, arg2);
    }

    @Override
    public void warn(final String format, final Object arg) {
        super.warn(replacePercentS(format), arg);
    }

    @Override
    public void warn(final String format, final Object arg1, final Object arg2) {
        super.warn(replacePercentS(format), arg1, arg2);
    }

    private String replacePercentS(final String format) {
        //CHECKSTYLE:OFF
        return Strings.replace(format, "%s", "{}");
        //CHECKSTYLE:ON
    }
}
