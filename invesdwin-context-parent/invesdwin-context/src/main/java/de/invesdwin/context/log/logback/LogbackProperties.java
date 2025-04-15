package de.invesdwin.context.log.logback;

import java.util.TimeZone;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class LogbackProperties {

    //CHECKSTYLE:OFF
    public static final TimeZone TIME_ZONE_OVERRIDE = TimeZone.getTimeZone("UTC");
    //CHECKSTYLE:ON
    /*
     * This property cannot be put into CommonProperties because the logging has to be initialized before
     * SystemProperties get loaded.
     */
    public static final String ENCODER_PATTERN;
    public static final String KEEP_USER_TIMEZONE_PARAM = "keep.user.timezone";

    private LogbackProperties() {}

    static {
        final StringBuilder sb = new StringBuilder("%-46(%date{yyyy-MM-dd HH:mm:ss.SSS");
        if (!isKeepDefaultTimezone()) {
            sb.append(",");
            sb.append(TIME_ZONE_OVERRIDE.getID());
        }
        sb.append("} [%.-21(%1X{transactions}|%thread))] %-5level %-60.-60(%logger{45}.%method) - %msg%n");
        ENCODER_PATTERN = sb.toString();
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
