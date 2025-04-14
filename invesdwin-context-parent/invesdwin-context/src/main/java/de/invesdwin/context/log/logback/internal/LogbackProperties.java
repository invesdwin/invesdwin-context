package de.invesdwin.context.log.logback.internal;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class LogbackProperties {

    /*
     * This property cannot be put into CommonProperties because the logging has to be initialized before
     * SystemProperties get loaded.
     */
    public static final String ENCODER_PATTERN = "%-46(%date{yyyy-MM-dd HH:mm:ss.SSS,UTC} [%.-21(%1X{transactions}|%thread))] %-5level %-60.-60(%logger{45}.%method) - %msg%n";

    private LogbackProperties() {}

}
