package de.invesdwin.context.log.log4j2;

import java.util.TimeZone;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Log4j2Properties {

    //CHECKSTYLE:OFF
    public static final TimeZone TIME_ZONE_OVERRIDE = TimeZone.getTimeZone("UTC");
    //CHECKSTYLE:ON
    /*
     * This property cannot be put into CommonProperties because the logging has to be initialized before
     * SystemProperties get loaded.
     */
    public static final String LAYOUT_PATTERN;
    public static final String KEEP_USER_TIMEZONE_PARAM = "keep.user.timezone";

    private Log4j2Properties() {}

    static {
        // 1. Date formatting (Using .SSS to match the comment, but .nnnnnnnnn is fully supported in Log4j2)
        //logback: final StringBuilder sb = new StringBuilder("%-46(%date{yyyy-MM-dd HH:mm:ss.SSS");
        final StringBuilder sb = new StringBuilder("%d{yyyy-MM-ddTHH:mm:ss}.%nanoTime");

        if (!isKeepDefaultTimezone()) {
            // Log4j2 timezone syntax: %d{yyyy-MM-dd HH:mm:ss.SSS}{GMT+1}
            sb.append("{");
            sb.append(TIME_ZONE_OVERRIDE.getID());
            sb.append("}");
        }

        //logback: sb.append("} [%.-21(%1X{transactions}|%thread))] %-5level %-60.-60(%logger{45}.%method) - %msg%n");
        // 2. Thread, MDC, Level, Logger, and Method
        // - [%X{transactions}|%.-21t]: Gets MDC map, truncates Thread to max 21 chars from the left
        // - %-5level: 5-character left-aligned log level
        // - %-45logger{45}.%-14.14M: Emulates the %-60.-60() grouping by splitting the 60
        //   characters between the logger (45) and the method name (max 14).
        sb.append(" [%1X{transactions}|%.-21t] %-5level %fixedLen{%logbackLogger{45}.%-14.14M}{60} - %msg%n");

        LAYOUT_PATTERN = sb.toString();
    }

    public static boolean isKeepDefaultTimezone() {
        //CHECKSTYLE:OFF
        final String keepTimeZone = System.getProperty(KEEP_USER_TIMEZONE_PARAM);
        //CHECKSTYLE:ON
        if (keepTimeZone == null) {
            return false;
        } else {
            return "true".equalsIgnoreCase(keepTimeZone);
        }
    }

}
