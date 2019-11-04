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
public @interface RetryDisabled {

    /**
     * If the retry is enabled or not; can be used to disable retries for e.g. transactions.
     */
    boolean value() default true;

}
