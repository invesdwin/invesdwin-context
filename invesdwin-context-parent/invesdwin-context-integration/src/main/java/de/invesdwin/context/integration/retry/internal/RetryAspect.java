package de.invesdwin.context.integration.retry.internal;

import java.util.concurrent.Callable;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.invesdwin.aspects.ProceedingJoinPoints;
import de.invesdwin.context.beans.init.MergedContext;
import de.invesdwin.context.integration.IntegrationProperties;
import de.invesdwin.context.integration.retry.Retry;
import de.invesdwin.context.integration.retry.RetryDisabled;
import de.invesdwin.context.integration.retry.hook.IRetryHook;
import de.invesdwin.context.integration.retry.hook.RetryHookManager;
import de.invesdwin.context.integration.retry.task.BackOffPolicies;
import de.invesdwin.context.integration.retry.task.RetryOriginator;
import de.invesdwin.util.error.Throwables;
import io.netty.util.concurrent.FastThreadLocal;

/**
 * Because of throwing LoggedRuntimeExceptions it is impossible to use the ExceptionPolicies of spring because they
 * don't analyze the causes.
 * 
 * @see <a href="https://jira.springframework.org/browse/SPR-4855">Exception-Wrapping</a>
 * @author subes
 * 
 */
@ThreadSafe
@Aspect
@Configurable
public class RetryAspect implements InitializingBean {

    @Inject
    private RetryTemplate retryTemplate;
    @Inject
    private IRetryHook[] listeners;

    private final FastThreadLocal<Boolean> parentTransactionAlreadyConsidered = new FastThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    @Override
    public void afterPropertiesSet() throws Exception {
        if (retryTemplate == null || listeners == null) {
            //fix execution in test where the aspect is initialized with the wrong context by spring
            MergedContext.autowire(this);
        }
    }

    @Around("@annotation(org.springframework.integration.annotation.Gateway) || execution(* *(..)) &&  @annotation(de.invesdwin.context.integration.retry.Retry)")
    public Object retry(final ProceedingJoinPoint pjp) throws Throwable {
        final Retry annotation = ProceedingJoinPoints.getAnnotation(pjp, Retry.class);
        if (annotation == null || annotation.value()) {
            final ExceptionCauseRetryCallback<Object> retryCallback = new ExceptionCauseRetryCallback<Object>(
                    new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            try {
                                return pjp.proceed();
                            } catch (final Throwable t) {
                                throw Throwables.propagate(t);
                            }
                        }
                    }, new RetryOriginator(pjp), BackOffPolicies.fixedBackOff(annotation), null);
            try {
                return retryTemplate.execute(retryCallback);
            } catch (final Throwable e) {
                final Throwable cause = Throwables.ignoreType(e, WrappedRetryException.class);
                RetryHookManager.getEventTrigger()
                        .onRetryAborted(retryCallback.getOriginator(), retryCallback.getRetryCount(), cause);
                throw cause;
            }
        } else {
            return pjp.proceed();
        }
    }

    @Around("execution(* *(..)) && @annotation(org.springframework.transaction.annotation.Transactional) || execution(* org.springframework.transaction.interceptor.TransactionInterceptor.invoke(*))")
    public Object retryTransaction(final ProceedingJoinPoint pjp) throws Throwable {
        final boolean alreadyConsidered = parentTransactionAlreadyConsidered.get();
        final Transactional annotation = ProceedingJoinPoints.getAnnotation(pjp, Transactional.class);
        if (!alreadyConsidered) {
            //propagation never should allow retry, but smaller internal transactions should still have their own retry
            final boolean considerNow = annotation == null || annotation.propagation() == Propagation.NESTED
                    || annotation.propagation() == Propagation.REQUIRED
                    || annotation.propagation() == Propagation.REQUIRES_NEW;
            parentTransactionAlreadyConsidered.set(considerNow);
            try {
                return retry(pjp);
            } finally {
                parentTransactionAlreadyConsidered.remove();
            }
        } else {
            if (annotation != null && annotation.propagation() == Propagation.REQUIRES_NEW) {
                return retry(pjp);
            } else {
                return pjp.proceed();
            }
        }
    }

    @Around("execution(* *(..)) &&  @annotation(de.invesdwin.context.integration.retry.RetryDisabled)")
    public Object retryDisabled(final ProceedingJoinPoint pjp) throws Throwable {
        final RetryDisabled annotation = ProceedingJoinPoints.getAnnotation(pjp, RetryDisabled.class);
        if (annotation == null || annotation.value()) {
            final boolean registerThreadRetryDisabled = IntegrationProperties.registerThreadRetryDisabled();
            try {
                return pjp.proceed();
            } finally {
                IntegrationProperties.unregisterThreadRetryDisabled(registerThreadRetryDisabled);
            }
        } else {
            return pjp.proceed();
        }
    }

    @Around("execution(* *(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object retryDisabledRequestMapping(final ProceedingJoinPoint pjp) throws Throwable {
        return retryDisabled(pjp);
    }

}
