package de.invesdwin.context.jcache.util;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@Immutable
public final class Durations {

    private Durations() {}

    public static javax.cache.expiry.Duration toJCache(final Duration duration) {
        return new javax.cache.expiry.Duration(duration.getTimeUnit().timeUnitValue(), duration.longValue());
    }

    public static Duration fromJCache(final javax.cache.expiry.Duration duration) {
        return new Duration(duration.getDurationAmount(), FTimeUnit.valueOfTimeUnit(duration.getTimeUnit()));
    }

}
