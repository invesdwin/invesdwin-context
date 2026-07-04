package de.invesdwin.context.log.log4j2;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.logging.log4j.core.time.PreciseClock;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.time.date.FTimeUnit;

/**
 * To use this clock, specify the following JVM options:
 *
 * <pre>
 * -Dlog4j.Clock=org.apache.logging.log4j.core.util.LowGcPreciseClock
 *  --add-opens java.base/jdk.internal.misc=ALL-UNNAMED
 * </pre>
 * 
 * Adapted from java.time.Clock.currentInstant()
 * 
 * See also https://issues.apache.org/jira/browse/LOG4J2-3079 and FDateClockNanosInternal
 */
@SuppressWarnings("restriction")
@ThreadSafe
public class LowGcPreciseClock implements PreciseClock {
    /**
     * Nanos per second.
     */
    private static final long OFFSET_SEED = newEpochSecond();

    @GuardedBy("none for performance")
    private static long localOffset = OFFSET_SEED;

    private static long newEpochSecond() {
        //CHECKSTYLE:OFF
        return System.currentTimeMillis() / FTimeUnit.MILLISECONDS_IN_SECOND - 1024;
        //CHECKSTYLE:ON
    }

    @Override
    public void init(final org.apache.logging.log4j.core.time.MutableInstant mutableInstant) {
        long epochSecond = localOffset;
        long nanoAdjustment = jdk.internal.misc.VM.getNanoTimeAdjustment(epochSecond);

        if (nanoAdjustment == -1) {
            epochSecond = newEpochSecond();
            nanoAdjustment = jdk.internal.misc.VM.getNanoTimeAdjustment(epochSecond);
            if (nanoAdjustment == -1) {
                throw new InternalError("LocalOffset " + epochSecond + " is not in range");
            } else {
                localOffset = epochSecond;
            }
        }

        final long seconds = epochSecond + Longs.floorDiv(nanoAdjustment, FTimeUnit.NANOSECONDS_IN_SECOND);
        final int nanos = Integers.floorMod(nanoAdjustment, FTimeUnit.NANOSECONDS_IN_SECOND);

        /* this bit is original log4j code */
        mutableInstant.initFromEpochSecond(seconds, nanos);
    }

    @Override
    public long currentTimeMillis() {
        //CHECKSTYLE:OFF
        return System.currentTimeMillis();
        //CHECKSTYLE:ON
    }
}