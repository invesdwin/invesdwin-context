package de.invesdwin.context.log;

import javax.annotation.concurrent.ThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.test.ATest;

@ThreadSafe
public class LogTest extends ATest {

    @Test
    public void testJavaUtilLogging() {
        final java.util.logging.Logger log = java.util.logging.Logger.getLogger(this.getClass().getName());
        //if no hello is printed there is something wrong
        log.warning("helloWarning");

        /*
         * Does not seem to get logged currently even if the loglevel of the current package is info. Might be caused by
         * ch.qos.logback.classic.jul.LevelChangePropagator, seemingly only looks at the rootlogger. Though this ain't
         * important right now because the warn level is the thing that interests us in other frameworks.
         */
        log.info("helloInfo");
    }
}
