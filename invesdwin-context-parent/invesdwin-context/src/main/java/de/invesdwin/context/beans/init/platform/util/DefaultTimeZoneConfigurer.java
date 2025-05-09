package de.invesdwin.context.beans.init.platform.util;

import java.util.TimeZone;

import javax.annotation.concurrent.Immutable;

import org.joda.time.DateTimeZone;
import org.springframework.context.i18n.LocaleContextHolder;

import de.invesdwin.context.PlatformInitializerProperties;
import de.invesdwin.context.log.Log;
import de.invesdwin.context.log.logback.LogbackProperties;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.date.timezone.FTimeZone;
import de.invesdwin.util.time.date.timezone.TimeZones;

@Immutable
public final class DefaultTimeZoneConfigurer {

    private static final String USER_TIMEZONE_PARAM = "user.timezone";
    private static final String SYSTEM_TIMEZONE;

    static {
        String systemTimeZone = null;
        try {
            //CHECKSTYLE:OFF
            systemTimeZone = System.getProperty(USER_TIMEZONE_PARAM);
            //CHECKSTYLE:ON
        } catch (final Throwable t) {
            //webstart safety for access control
            systemTimeZone = TimeZone.getDefault().getID();
        }
        SYSTEM_TIMEZONE = systemTimeZone;
        FDates.setSystemTimeZone(FTimeZone.valueOf(systemTimeZone));
    }

    private DefaultTimeZoneConfigurer() {}

    public static void configure() {
        //explicitly using fully qualified reference to not invoke static initializer for loading properties while
        final de.invesdwin.context.system.properties.SystemProperties systemProperties = new de.invesdwin.context.system.properties.SystemProperties();
        final TimeZone newTimeZone = LogbackProperties.TIME_ZONE_OVERRIDE;
        final Log log = new Log(DefaultTimeZoneConfigurer.class);
        if (!LogbackProperties.isKeepDefaultTimezone()) {
            if (!SYSTEM_TIMEZONE.equals(newTimeZone.getID())) {
                log.warn("Changing JVM default " + TimeZone.class.getSimpleName() + " from [" + SYSTEM_TIMEZONE
                        + "] to [" + newTimeZone.getID() + "] in order to have commonality between systems:"
                        + "\n- Use -D" + LogbackProperties.KEEP_USER_TIMEZONE_PARAM
                        + "=true to keep the system default." + " Additionally using -D" + USER_TIMEZONE_PARAM + "=<"
                        + TimeZone.class.getSimpleName() + "ID> allows to change the default of the JVM."
                        + "\n- Hide this warning by using -D" + USER_TIMEZONE_PARAM + "=" + newTimeZone.getID()
                        + " to specify the default to match the convention.");
                systemProperties.setString(USER_TIMEZONE_PARAM, newTimeZone.getID());
                setDefaultTimeZone(newTimeZone);
            }
        } else {
            setDefaultTimeZone(getSystemTimezone());
        }
        log.info("Using " + USER_TIMEZONE_PARAM + "=%s", TimeZone.getDefault().getID());
    }

    public static void setDefaultTimeZone(final TimeZone newTimeZone) {
        try {
            TimeZone.setDefault(newTimeZone);
            //joda needs another call explicitly since it might have cached the value too early...
            DateTimeZone.setDefault(DateTimeZone.forTimeZone(newTimeZone));
            //same with FDate
            FDates.setDefaultTimeZone(new FTimeZone(newTimeZone));
            LocaleContextHolder.setDefaultTimeZone(newTimeZone);
            Assertions.assertThat(getDefaultTimeZone().getId())
                    .as("java has inconsistent default %s", TimeZone.class.getSimpleName())
                    .isEqualTo(newTimeZone.getID());
        } catch (final Throwable t) {
            PlatformInitializerProperties.logInitializationFailedIsIgnored(t);
            //webstart safety for access control
            //we want to at least use in the strategy UTC even if it failed for the jvm
            FDates.setDefaultTimeZone(new FTimeZone(newTimeZone));
            LocaleContextHolder.setDefaultTimeZone(newTimeZone);
        }
    }

    public static FTimeZone getDefaultTimeZone() {
        if (PlatformInitializerProperties.isAllowed()) {
            final TimeZone defaultTimeZone = TimeZone.getDefault();
            Assertions.assertThat(DateTimeZone.getDefault().toTimeZone().getID())
                    .as("joda-time (%s) has inconsistent default %s", DateTimeZone.class.getSimpleName(),
                            TimeZone.class.getSimpleName())
                    .isEqualTo(defaultTimeZone.getID());
            Assertions.assertThat(FDates.getDefaultTimeZone().getId())
                    .as("invesdwin-util (%s) has inconsistent default %s", FDate.class.getSimpleName(),
                            TimeZone.class.getSimpleName())
                    .isEqualTo(defaultTimeZone.getID());
            return new FTimeZone(defaultTimeZone);
        } else {
            return FDates.getDefaultTimeZone();
        }
    }

    public static TimeZone getSystemTimezone() {
        return TimeZones.getTimeZone(SYSTEM_TIMEZONE);
    }

}
