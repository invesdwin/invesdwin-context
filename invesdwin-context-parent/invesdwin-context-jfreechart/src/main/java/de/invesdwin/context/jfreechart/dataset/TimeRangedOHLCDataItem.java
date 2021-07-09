package de.invesdwin.context.jfreechart.dataset;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Objects;
import de.invesdwin.util.time.date.FDate;

@NotThreadSafe
public class TimeRangedOHLCDataItem {

    protected FDate startTime;
    protected FDate endTime;
    protected double open;
    protected double high;
    protected double low;
    protected double close;
    protected double volume;

    public TimeRangedOHLCDataItem(final FDate startTime, final FDate endTime, final double open, final double high,
            final double low, final double close, final double volume) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public FDate getStartTime() {
        return startTime;
    }

    public FDate getEndTime() {
        return endTime;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public double getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("startTime", startTime)
                .add("endTime", endTime)
                .add("open", open)
                .add("high", high)
                .add("low", low)
                .add("close", close)
                .add("volume", volume)
                .toString();
    }

}
