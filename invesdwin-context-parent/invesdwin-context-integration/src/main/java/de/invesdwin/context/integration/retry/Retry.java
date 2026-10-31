package de.invesdwin.context.integration.retry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Causes the method invocation to be retries when an IOException or a RetryLaterException occurs. When an interrupt
 * occurs, retries get aborted.
 * 
 * @author subes
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {

    /**
     * If the retry is enabled or not; can be used to disable retries for e.g. transactions.
     */
    boolean value() default true;

    /**
     * A negative value keeps the default exponential back off policy (from 1 second to 1 minute).
     * 
     * 0 uses NoBackOffPolicy. A positive values uses a FixedBackOffPolicy.
     */
    long fixedBackOffMillis() default -1;

    /**
     * A negative or 0 value allows infinite retries. A positive value aborts the retry after the retry count has been
     * reached.
     */
    int maxRetries() default -1;

    /**
     * What 'fallthrough' means, exactly: this annotated call is wrapped in a retry template that normally retries
     * RetryLater/IO-type failures in place until success or retry exhaustion. When fallthrough is true AND this very
     * call is already enclosed by an OUTER active retry on the same call scope (RetryScopes.hasOuterRetry(): nested
     * @Retry on this thread, or an outer retry explicitly installed into this thread - e.g. the demand-side @Retry of
     * the run that submitted this operation as a task), then a retryable failure is NOT retried here: it propagates
     * out of this call immediately (attempt 1 only), so any locks this body holds (table write locks, heartbeat pins)
     * unwind and the OUTER retry re-attempts the whole operation from a clean state. When there is no enclosing outer
     * retry, this site keeps its own in-place retry exactly as if fallthrough were false.
     *
     * Use at sites whose body holds resources that the failing chain needs in order to progress (update task bodies
     * running under a per-key write lock); it prevents unbounded in-place back-off storms that park other threads on
     * those locks.
     */
    boolean fallthrough() default false;

}
