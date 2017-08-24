package de.invesdwin.context;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.BooleanUtils;

import de.invesdwin.context.beans.init.platform.DefaultPlatformInitializer;
import de.invesdwin.context.beans.init.platform.IPlatformInitializer;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.fdate.FDate;

/**
 * This is a separate class so that it can be used without initalizing the PreMergedContext.
 */
@Immutable
public final class PlatformInitializerProperties {

    public static final Instant START_OF_APPLICATION_CPU_TIME = new Instant();
    public static final FDate START_OF_APPLICATION_CLOCK_TIME = new FDate();

    /**
     * Use literal constant here to ensure inlining of these properties key constants.
     */
    public static final String PROPERTIES_PREFIX = "de.invesdwin.context.PlatformInitializerProperties";
    public static final String KEY_ALLOWED = PROPERTIES_PREFIX + ".ALLOWED";
    public static final String KEY_KEEP_SYSTEM_HOME_DURING_TESTS = PROPERTIES_PREFIX + ".KEEP_SYSTEM_HOME_DURING_TESTS";

    private static IPlatformInitializer initializer = new DefaultPlatformInitializer();

    static {
        final String expectedPropertiesPrefix = PlatformInitializerProperties.class.getName();
        if (!expectedPropertiesPrefix.equals(PROPERTIES_PREFIX)) {
            throw new IllegalStateException("PROPERTIES_PREFIX [" + PROPERTIES_PREFIX + "] does not equal expected ["
                    + expectedPropertiesPrefix + "]. Maybe you moved the class but did not update the constant?");
        }
    }

    private PlatformInitializerProperties() {}

    public static synchronized boolean isAllowed() {
        //CHECKSTYLE:OFF single line
        try {
            final String property = System.getProperty(KEY_ALLOWED);
            if (Strings.isBlank(property)) {
                return true;
            } else {
                return BooleanUtils.toBoolean(property);
            }
        } catch (final Throwable t) {
            //maybe some webstart security control prevents reading system properties
            return false;
        }
        //CHECKSTYLE:ON
    }

    public static synchronized void setAllowed(final boolean allowed) {
        //CHECKSTYLE:OFF single line
        System.setProperty(KEY_ALLOWED, String.valueOf(allowed));
        //CHECKSTYLE:ON
    }

    public static synchronized boolean isKeepSystemHomeDuringTests() {
        //CHECKSTYLE:OFF single line
        final String property = System.getProperty(KEY_KEEP_SYSTEM_HOME_DURING_TESTS);
        if (Strings.isBlank(property)) {
            return false;
        } else {
            return BooleanUtils.toBoolean(property);
        }
        //CHECKSTYLE:ON
    }

    public static synchronized void setKeepSystemHomeDuringTests(final boolean invesdwinInitSkip) {
        //CHECKSTYLE:OFF single line
        System.setProperty(KEY_KEEP_SYSTEM_HOME_DURING_TESTS, String.valueOf(invesdwinInitSkip));
        //CHECKSTYLE:ON
    }

    public static synchronized void assertInitializationNotSkipped() {
        if (!isAllowed()) {
            throw new IllegalStateException(
                    "invesdwin initialization was skipped either because there was an error or it was decided to do so, but this operation requires an initialization in order to work.");
        }
    }

    public static synchronized void logInitializationFailedIsIgnored(final Throwable t) {
        //only log the first warning and ignore any subsequent ones
        if (isAllowed()) {
            setAllowed(false); //mark initialization as skipped after the error
            //CHECKSTYLE:OFF ignore if not in standalone environment
            new RuntimeException(
                    "invesdwin initialization failed, ignoring for now since this might be a restricted environment in which partial operation is possible. Please set system property "
                            + KEY_ALLOWED + "=false to prevent initialization entirely.",
                    t).printStackTrace();
            //CHECKSTYLE:ON
        }
    }

    public static IPlatformInitializer getInitializer() {
        return initializer;
    }

    public static void setInitializer(final IPlatformInitializer initializer) {
        if (initializer == null) {
            throw new NullPointerException("initializer should not be null!");
        }
        PlatformInitializerProperties.initializer = initializer;
    }

}
