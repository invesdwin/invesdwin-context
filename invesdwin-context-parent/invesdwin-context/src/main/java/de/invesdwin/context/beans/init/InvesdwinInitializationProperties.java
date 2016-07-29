package de.invesdwin.context.beans.init;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.BooleanUtils;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.time.Instant;
import de.invesdwin.util.time.fdate.FDate;

/**
 * This is a separate class so that it can be used without initalizing the PreMergedContext.
 */
@Immutable
public final class InvesdwinInitializationProperties {

    public static final Instant START_OF_APPLICATION_CPU_TIME = new Instant();
    public static final FDate START_OF_APPLICATION_CLOCK_TIME = new FDate();

    public static final String INVESDWIN_INITIALIZATION_ALLOWED = "invesdwin.initialization.allowed";

    private InvesdwinInitializationProperties() {}

    public static synchronized boolean isInvesdwinInitializationAllowed() {
        //CHECKSTYLE:OFF single line
        final String property = System.getProperty(INVESDWIN_INITIALIZATION_ALLOWED);
        if (Strings.isBlank(property)) {
            return true;
        } else {
            return BooleanUtils.toBoolean(property);
        }
        //CHECKSTYLE:ON
    }

    public static synchronized void setInvesdwinInitializationAllowed(final boolean invesdwinInitSkip) {
        //CHECKSTYLE:OFF single line
        System.setProperty(INVESDWIN_INITIALIZATION_ALLOWED, String.valueOf(invesdwinInitSkip));
        //CHECKSTYLE:ON
    }

    public static synchronized void assertInitializationNotSkipped() {
        if (!isInvesdwinInitializationAllowed()) {
            throw new IllegalStateException(
                    "invesdwin initialization was skipped either because there was an error or it was decided to do so, but this operation requires an initialization in order to work.");
        }
    }

    public static synchronized void logInitializationFailedIsIgnored(final Throwable t) {
        //only log the first warning and ignore any subsequent ones
        if (isInvesdwinInitializationAllowed()) {
            setInvesdwinInitializationAllowed(false); //mark initialization as skipped after the error
            //CHECKSTYLE:OFF ignore if not in standalone environment
            new RuntimeException(
                    "invesdwin initialization failed, ignoring for now since this might be a restricted environment in which partial operation is possible. Please set system property "
                            + INVESDWIN_INITIALIZATION_ALLOWED + "=false to prevent initialization entirely.", t).printStackTrace();
            //CHECKSTYLE:ON
        }
    }

}
