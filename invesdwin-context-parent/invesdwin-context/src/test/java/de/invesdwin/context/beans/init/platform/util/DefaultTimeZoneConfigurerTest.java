package de.invesdwin.context.beans.init.platform.util;

import java.util.TimeZone;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.context.test.ATest;

@NotThreadSafe
public class DefaultTimeZoneConfigurerTest extends ATest {

    @Test
    public void testTimeZone() {
        log.info("%s", TimeZone.getDefault());
    }
}
