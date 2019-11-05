package de.invesdwin.context.integration.retry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Causes the method invocation to be retries when an IOException or a RetryAgainException occurs. When an interrupt
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

}
