package de.invesdwin.context.integration.network.request;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public enum DailyDownloadInterval {
    DAILY(Duration.ONE_DAY) {
        @Override
        public boolean isSameInterval(final FDate date1, final FDate date2) {
            return FDates.isSameJulianDay(date1, date2);
        }
    },
    //we use the maximum of 31 days for a month instead of 30 days
    MONTHLY(new Duration(31, FTimeUnit.DAYS)) {
        @Override
        public boolean isSameInterval(final FDate date1, final FDate date2) {
            return FDates.isSameMonth(date1, date2);
        }
    };

    private final Duration duration;

    DailyDownloadInterval(final Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public abstract boolean isSameInterval(FDate date1, FDate date2);
}
