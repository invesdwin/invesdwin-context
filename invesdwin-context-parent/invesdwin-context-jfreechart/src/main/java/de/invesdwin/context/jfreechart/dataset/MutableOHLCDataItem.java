package de.invesdwin.context.jfreechart.dataset;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class MutableOHLCDataItem extends TimeRangedOHLCDataItem {

    public static final MutableOHLCDataItem DUMMY_VALUE = new MutableOHLCDataItem(FDate.MIN_DATE, FDate.MIN_DATE) {

        @Override
        public void setStartTime(final FDate date) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setEndTime(final FDate date) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setClose(final double close) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setHigh(final double high) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLow(final double low) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setOpen(final double open) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setVolume(final double volume) {
            throw new UnsupportedOperationException();
        }
    };

    public MutableOHLCDataItem(final FDate date, final FDate endTime) {
        this(date, endTime, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
    }

    public MutableOHLCDataItem(final TimeRangedOHLCDataItem ohlc) {
        this(ohlc.getStartTime(), ohlc.getEndTime(), ohlc.getOpen(), ohlc.getHigh(), ohlc.getLow(), ohlc.getClose(),
                ohlc.getVolume());
    }

    public MutableOHLCDataItem(final FDate date, final FDate endTime, final double open, final double high,
            final double low, final double close, final double volume) {
        super(date, endTime, open, high, low, close, volume);
    }

    public void setOHLC(final TimeRangedOHLCDataItem ohlc) {
        setStartTime(ohlc.getStartTime());
        setEndTime(ohlc.getEndTime());
        setOpen(ohlc.getOpen());
        setHigh(ohlc.getHigh());
        setLow(ohlc.getLow());
        setClose(ohlc.getClose());
        setVolume(ohlc.getVolume());
    }

    public void setStartTime(final FDate startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(final FDate endTime) {
        this.endTime = endTime;
    }

    public void setOpen(final double open) {
        this.open = open;
    }

    public void setHigh(final double high) {
        this.high = high;
    }

    public void setLow(final double low) {
        this.low = low;
    }

    public void setClose(final double close) {
        this.close = close;
    }

    public void setVolume(final double volume) {
        this.volume = volume;
    }

}
