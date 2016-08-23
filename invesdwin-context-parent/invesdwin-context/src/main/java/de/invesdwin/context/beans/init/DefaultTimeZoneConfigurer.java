package de.invesdwin.context.beans.init;

import java.util.NoSuchElementException;
import java.util.TimeZone;

import javax.annotation.concurrent.Immutable;

import org.joda.time.DateTimeZone;

import de.invesdwin.context.log.Log;
import de.invesdwin.context.system.properties.SystemProperties;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.TimeZones;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class DefaultTimeZoneConfigurer {

    private static final String USER_TIMEZONE_PARAM = "user.timezone";
    private static final String KEEP_USER_TIMEZONE_PARAM = "keep.user.timezone";
    //CHECKSTYLE:OFF
    private static final String ORIGINAL_TIMEZONE = System.getProperty(USER_TIMEZONE_PARAM);
    //CHECKSTYLE:ON

    private DefaultTimeZoneConfigurer() {}

    public static void configure() {
        final SystemProperties systemProperties = new SystemProperties();
        final TimeZone newTimeZone = TimeZones.getTimeZone("UTC");
        final Log log = new Log(DefaultTimeZoneConfigurer.class);
        if (!getKeepDefaultTimezone()) {
            if (!ORIGINAL_TIMEZONE.equals(newTimeZone.getID())) {
                log.warn("Changing JVM default " + TimeZone.class.getSimpleName() + " from [" + ORIGINAL_TIMEZONE
                        + "] to [" + newTimeZone.getID() + "] in order to have commonality between systems:"
                        + "\n- Use -D" + KEEP_USER_TIMEZONE_PARAM + "=true to keep the system default."
                        + " Additionally using -D" + USER_TIMEZONE_PARAM + "=<" + TimeZone.class.getSimpleName()
                        + "ID> allows to change the default of the JVM." + "\n- Hide this warning by using -D"
                        + USER_TIMEZONE_PARAM + "=" + newTimeZone.getID()
                        + " to specifiy the default to match the convention.");
                systemProperties.setString(USER_TIMEZONE_PARAM, newTimeZone.getID());
                setDefaultTimeZone(newTimeZone);
            }
        } else {
            setDefaultTimeZone(TimeZones.getTimeZone(ORIGINAL_TIMEZONE));
        }
        log.info("Using " + USER_TIMEZONE_PARAM + "=%s", TimeZone.getDefault().getID());
    }

    public static void setDefaultTimeZone(final TimeZone newTimeZone) {
        TimeZone.setDefault(newTimeZone);
        //joda needs another call explicitly since it might have cached the value too early...
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(newTimeZone));
        //same with FDate
        FDate.setDefaultTimeZone(newTimeZone);
        Assertions.assertThat(getDefaultTimeZone().getID())
                .as("java has inconsistent default %s", TimeZone.class.getSimpleName())
                .isEqualTo(newTimeZone.getID());
    }

    public static TimeZone getDefaultTimeZone() {
        final TimeZone defaultTimeZone = TimeZone.getDefault();
        Assertions.assertThat(DateTimeZone.getDefault().toTimeZone().getID())
                .as("joda-time (%s) has inconsistent default %s", DateTimeZone.class.getSimpleName(),
                        TimeZone.class.getSimpleName())
                .isEqualTo(defaultTimeZone.getID());
        Assertions.assertThat(FDate.getDefaultTimeZone().getID())
                .as("invesdwin-util (%s) has inconsistent default %s", FDate.class.getSimpleName(),
                        TimeZone.class.getSimpleName())
                .isEqualTo(defaultTimeZone.getID());
        return defaultTimeZone;
    }

    private static boolean getKeepDefaultTimezone() {
        try {
            return new SystemProperties().getBoolean(KEEP_USER_TIMEZONE_PARAM);
        } catch (final NoSuchElementException e) {
            return false;
        }
    }

    public static String getOriginalTimezone() {
        return ORIGINAL_TIMEZONE;
    }

}
