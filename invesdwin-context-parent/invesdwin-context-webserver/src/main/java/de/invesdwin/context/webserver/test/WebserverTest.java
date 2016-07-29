package de.invesdwin.context.webserver.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * With this annotation you can mark tests so that they enable the webserver during test execution.
 * 
 * You can also disable the webserver by putting "false" as the value to override an enabled webserver from a base
 * class.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface WebserverTest {

    boolean value() default true;

}
