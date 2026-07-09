package de.invesdwin.context.log;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.context.ContextProperties;
import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.string.description.TextDescription;
import de.invesdwin.util.log.ILog;
import de.invesdwin.util.log.LogLevel;

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

    private final org.apache.logging.log4j.Logger logger;

    public Log(final String name) {
        this.logger = org.apache.logging.log4j.LogManager.getLogger(name);
    }

    public Log(final Class<?> clazz) {
        this(clazz.getName());
    }

    public Log(final Object obj) {
        this(obj.getClass());
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(final String msg) {
        logger.trace(msg);
    }

    @Override
    public void trace(final String format, final Object p0) {
        if (logger.isTraceEnabled()) {
            logger.trace(new TextDescription(format, p0));
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1) {
        if (logger.isTraceEnabled()) {
            logger.trace(new TextDescription(format, p0, p1));
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2) {
        if (logger.isTraceEnabled()) {
            logger.trace(new TextDescription(format, p0, p1, p2));
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (logger.isTraceEnabled()) {
            logger.trace(new TextDescription(format, p0, p1, p2, p3));
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        if (logger.isTraceEnabled()) {
            logger.trace(new TextDescription(format, p0, p1, p2, p3, p4));
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        if (logger.isTraceEnabled()) {
            logger.trace(new TextDescription(format, p0, p1, p2, p3, p4, p5));
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        if (logger.isTraceEnabled()) {
            logger.trace(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6));
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        if (logger.isTraceEnabled()) {
            logger.trace(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7));
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        if (logger.isTraceEnabled()) {
            logger.trace(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7, p8));
        }
    }

    @Override
    public void trace(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        if (logger.isTraceEnabled()) {
            logger.trace(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9));
        }
    }

    @Override
    public void trace(final String format, final Object... params) {
        if (logger.isTraceEnabled()) {
            logger.trace(new TextDescription(format, params));
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(final String msg) {
        logger.debug(msg);
    }

    @Override
    public void debug(final String format, final Object p0) {
        if (logger.isDebugEnabled()) {
            logger.debug(new TextDescription(format, p0));
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1) {
        if (logger.isDebugEnabled()) {
            logger.debug(new TextDescription(format, p0, p1));
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2) {
        if (logger.isDebugEnabled()) {
            logger.debug(new TextDescription(format, p0, p1, p2));
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (logger.isDebugEnabled()) {
            logger.debug(new TextDescription(format, p0, p1, p2, p3));
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        if (logger.isDebugEnabled()) {
            logger.debug(new TextDescription(format, p0, p1, p2, p3, p4));
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        if (logger.isDebugEnabled()) {
            logger.debug(new TextDescription(format, p0, p1, p2, p3, p4, p5));
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        if (logger.isDebugEnabled()) {
            logger.debug(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6));
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        if (logger.isDebugEnabled()) {
            logger.debug(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7));
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        if (logger.isDebugEnabled()) {
            logger.debug(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7, p8));
        }
    }

    @Override
    public void debug(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        if (logger.isDebugEnabled()) {
            logger.debug(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9));
        }
    }

    @Override
    public void debug(final String format, final Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug(new TextDescription(format, params));
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(final String msg) {
        logger.info(msg);
    }

    @Override
    public void info(final String format, final Object p0) {
        if (logger.isInfoEnabled()) {
            logger.info(new TextDescription(format, p0));
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1) {
        if (logger.isInfoEnabled()) {
            logger.info(new TextDescription(format, p0, p1));
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2) {
        if (logger.isInfoEnabled()) {
            logger.info(new TextDescription(format, p0, p1, p2));
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (logger.isInfoEnabled()) {
            logger.info(new TextDescription(format, p0, p1, p2, p3));
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        if (logger.isInfoEnabled()) {
            logger.info(new TextDescription(format, p0, p1, p2, p3, p4));
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        if (logger.isInfoEnabled()) {
            logger.info(new TextDescription(format, p0, p1, p2, p3, p4, p5));
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        if (logger.isInfoEnabled()) {
            logger.info(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6));
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        if (logger.isInfoEnabled()) {
            logger.info(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7));
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        if (logger.isInfoEnabled()) {
            logger.info(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7, p8));
        }
    }

    @Override
    public void info(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        if (logger.isInfoEnabled()) {
            logger.info(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9));
        }
    }

    @Override
    public void info(final String format, final Object... params) {
        if (logger.isInfoEnabled()) {
            logger.info(new TextDescription(format, params));
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(final String msg) {
        logger.warn(msg);
    }

    @Override
    public void warn(final String format, final Object p0) {
        if (logger.isWarnEnabled()) {
            logger.warn(new TextDescription(format, p0));
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1) {
        if (logger.isWarnEnabled()) {
            logger.warn(new TextDescription(format, p0, p1));
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2) {
        if (logger.isWarnEnabled()) {
            logger.warn(new TextDescription(format, p0, p1, p2));
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (logger.isWarnEnabled()) {
            logger.warn(new TextDescription(format, p0, p1, p2, p3));
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        if (logger.isWarnEnabled()) {
            logger.warn(new TextDescription(format, p0, p1, p2, p3, p4));
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        if (logger.isWarnEnabled()) {
            logger.warn(new TextDescription(format, p0, p1, p2, p3, p4, p5));
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        if (logger.isWarnEnabled()) {
            logger.warn(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6));
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        if (logger.isWarnEnabled()) {
            logger.warn(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7));
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        if (logger.isWarnEnabled()) {
            logger.warn(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7, p8));
        }
    }

    @Override
    public void warn(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        if (logger.isWarnEnabled()) {
            logger.warn(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9));
        }
    }

    @Override
    public void warn(final String format, final Object... params) {
        if (logger.isWarnEnabled()) {
            logger.warn(new TextDescription(format, params));
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(final String msg) {
        logger.error(msg);
    }

    @Override
    public void error(final String format, final Object p0) {
        if (logger.isErrorEnabled()) {
            logger.error(new TextDescription(format, p0));
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1) {
        if (logger.isErrorEnabled()) {
            logger.error(new TextDescription(format, p0, p1));
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2) {
        if (logger.isErrorEnabled()) {
            logger.error(new TextDescription(format, p0, p1, p2));
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (logger.isErrorEnabled()) {
            logger.error(new TextDescription(format, p0, p1, p2, p3));
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        if (logger.isErrorEnabled()) {
            logger.error(new TextDescription(format, p0, p1, p2, p3, p4));
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        if (logger.isErrorEnabled()) {
            logger.error(new TextDescription(format, p0, p1, p2, p3, p4, p5));
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        if (logger.isErrorEnabled()) {
            logger.error(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6));
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        if (logger.isErrorEnabled()) {
            logger.error(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7));
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        if (logger.isErrorEnabled()) {
            logger.error(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7, p8));
        }
    }

    @Override
    public void error(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        if (logger.isErrorEnabled()) {
            logger.error(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9));
        }
    }

    @Override
    public void error(final String format, final Object... params) {
        if (logger.isErrorEnabled()) {
            logger.error(new TextDescription(format, params));
        }
    }

    @Override
    public boolean isFatalEnabled() {
        return logger.isFatalEnabled();
    }

    @Override
    public void fatal(final String msg) {
        logger.fatal(msg);
    }

    @Override
    public void fatal(final String format, final Object p0) {
        if (logger.isFatalEnabled()) {
            logger.fatal(new TextDescription(format, p0));
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1) {
        if (logger.isFatalEnabled()) {
            logger.fatal(new TextDescription(format, p0, p1));
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2) {
        if (logger.isFatalEnabled()) {
            logger.fatal(new TextDescription(format, p0, p1, p2));
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3) {
        if (logger.isFatalEnabled()) {
            logger.fatal(new TextDescription(format, p0, p1, p2, p3));
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4) {
        if (logger.isFatalEnabled()) {
            logger.fatal(new TextDescription(format, p0, p1, p2, p3, p4));
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5) {
        if (logger.isFatalEnabled()) {
            logger.fatal(new TextDescription(format, p0, p1, p2, p3, p4, p5));
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6) {
        if (logger.isFatalEnabled()) {
            logger.fatal(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6));
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7) {
        if (logger.isFatalEnabled()) {
            logger.fatal(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7));
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8) {
        if (logger.isFatalEnabled()) {
            logger.fatal(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7, p8));
        }
    }

    @Override
    public void fatal(final String format, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9) {
        if (logger.isFatalEnabled()) {
            logger.fatal(new TextDescription(format, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9));
        }
    }

    @Override
    public void fatal(final String format, final Object... params) {
        if (logger.isFatalEnabled()) {
            logger.fatal(new TextDescription(format, params));
        }
    }

    @Override
    public void catching(final Throwable throwable) {
        logger.catching(throwable);
    }

    @Override
    public void catching(final LogLevel level, final Throwable throwable) {
        logger.catching(level.asLog4j2Level(), throwable);
    }

}
