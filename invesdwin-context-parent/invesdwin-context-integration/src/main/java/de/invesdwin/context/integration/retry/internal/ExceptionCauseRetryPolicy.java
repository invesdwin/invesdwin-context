package de.invesdwin.context.integration.retry.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Named;
import javax.persistence.LockTimeoutException;
import javax.persistence.OptimisticLockException;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.integration.MessageTimeoutException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionSystemException;

import de.invesdwin.context.integration.retry.RetryDisabledException;
import de.invesdwin.context.integration.retry.RetryDisabledRuntimeException;
import de.invesdwin.context.integration.retry.RetryLaterException;
import de.invesdwin.context.integration.retry.RetryLaterRuntimeException;
import de.invesdwin.context.integration.retry.RetryOriginator;
import de.invesdwin.context.integration.retry.hook.RetryHookManager;
import de.invesdwin.context.log.error.LoggedRuntimeException;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.error.Throwables;

@NotThreadSafe
@Named
public class ExceptionCauseRetryPolicy extends NeverRetryPolicy implements FactoryBean<ExceptionCauseRetryPolicy> {

    public static final ExceptionCauseRetryPolicy INSTANCE = new ExceptionCauseRetryPolicy();

    private static final String ATTRIBUTE_LAST_LOGGED_RETRY_COUNT = "ATTRIBUTE_LAST_LOGGED_RETRY_COUNT";

    //<property name="disallowedCauses">
    //    <set>
    //        <!-- specific exceptions that are children of allowedCauses should still not be allowed -->
    //        <value>org.springframework.dao.OptimisticLockingFailureException</value>
    //        <value>javax.persistence.OptimisticLockException</value>
    //    </set>
    //</property>
    private final List<Class<? extends Exception>> disallowedCauses = Arrays.asList(
            OptimisticLockingFailureException.class, OptimisticLockException.class, ConstraintViolationException.class);
    //<property name="allowedCauses">
    //    <set>
    //        <!-- on connection problems there should always be retried -->
    //        <value>java.io.IOException</value>
    //        <value>org.springframework.integration.MessageTimeoutException</value>
    //        <value>org.springframework.dao.TransientDataAccessException</value>
    //        <value>javax.persistence.LockTimeoutException</value>
    //        <value>org.springframework.transaction.TransactionSystemException</value>
    //        <value>org.springframework.orm.jpa.JpaSystemException</value>
    //    </set>
    //</property>
    private final List<Class<? extends Exception>> allowedCauses = Arrays.asList(IOException.class,
            MessageTimeoutException.class, TransientDataAccessException.class, LockTimeoutException.class,
            TransactionSystemException.class, JpaSystemException.class, CannotCreateTransactionException.class,
            TimeoutException.class);
    //java.sql.SQLException: Lock wait timeout exceeded; try restarting transaction
    private final List<String> allowedCauseMessageParts = Arrays.asList("try restarting transaction");

    @Override
    public boolean canRetry(final RetryContext context) {
        //Allow first run
        if (super.canRetry(context)) {
            return true;
        }
        if (Threads.isInterrupted()) {
            return false;
        }
        //After we check for exception we want to decide on
        final boolean retry = decideRetry(context);
        if (retry) {
            maybeLogRetry(context);
        }
        return retry;
    }

    private boolean decideRetry(final RetryContext context) {
        final Throwable lastThrowable = context.getLastThrowable();
        Throwable cause = lastThrowable;
        while (cause != null) {
            if (cause instanceof RetryDisabledException || cause instanceof RetryDisabledRuntimeException) {
                //always disallowed
                return false;
            } else if (cause instanceof RetryLaterException || cause instanceof RetryLaterRuntimeException) {
                //always allowed
                return true;
            }
            cause = cause.getCause();
        }
        for (final Class<? extends Throwable> allowedCause : disallowedCauses) {
            if (Throwables.isCausedByType(lastThrowable, allowedCause)) {
                return false;
            }
        }
        for (final Class<? extends Throwable> allowedCause : allowedCauses) {
            if (Throwables.isCausedByType(lastThrowable, allowedCause)) {
                return true;
            }
        }
        for (final String allowedCauseMessagePart : allowedCauseMessageParts) {
            if (Throwables.isCausedByMessagePart(lastThrowable, allowedCauseMessagePart)) {
                return true;
            }
        }
        return false;
    }

    private void maybeLogRetry(final RetryContext context) {
        final int retryCount = context.getRetryCount();
        final Integer lastLoggedRetryCount = (Integer) context.getAttribute(ATTRIBUTE_LAST_LOGGED_RETRY_COUNT);
        if (lastLoggedRetryCount != null && lastLoggedRetryCount == retryCount) {
            //spring has called canRetry more than once during the retry handling
            return;
        }

        final RetryOriginator originator = (RetryOriginator) context
                .getAttribute(RetryOriginator.ATTRIBUTE_RETRY_ORIGINATOR);
        Assertions.checkNotNull(originator, "Attribute %s not set, please set it with an instance of %s",
                RetryOriginator.ATTRIBUTE_RETRY_ORIGINATOR, RetryOriginator.class.getSimpleName());
        final Throwable cause = Throwables.ignoreType(context.getLastThrowable(), WrappedRetryException.class,
                LoggedRuntimeException.class);
        Assertions.checkNotNull(cause);
        final int beforeRetryCount = retryCount - 1;
        RetryHookManager.getEventTrigger().onBeforeRetry(originator, beforeRetryCount, cause);

        context.setAttribute(ATTRIBUTE_LAST_LOGGED_RETRY_COUNT, retryCount);
    }

    @Override
    public ExceptionCauseRetryPolicy getObject() throws Exception {
        return INSTANCE;
    }

    @Override
    public Class<?> getObjectType() {
        return ExceptionCauseRetryPolicy.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
