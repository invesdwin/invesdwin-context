package de.invesdwin.context.jfreechart.dataset;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.data.xy.OHLCDataItem;

import de.invesdwin.util.lang.Reflections;
import de.invesdwin.util.time.fdate.FDate;

@NotThreadSafe
public class MutableOHLCDataItem extends OHLCDataItem {

    public static final MutableOHLCDataItem DUMMY_VALUE = new MutableOHLCDataItem(FDate.MIN_DATE.dateValue()) {

        @Override
        public void setDate(final Date date) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setClose(final Number close) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setHigh(final Number high) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLow(final Number low) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setOpen(final Number open) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setVolume(final Number volume) {
            throw new UnsupportedOperationException();
        }
    };

    private static final MethodHandle DATE_SETTER;
    private static final MethodHandle OPEN_SETTER;
    private static final MethodHandle HIGH_SETTER;
    private static final MethodHandle LOW_SETTER;
    private static final MethodHandle CLOSE_SETTER;
    private static final MethodHandle VOLUME_SETTER;

    static {
        try {
            final Lookup lookup = MethodHandles.lookup();
            final Field dateField = Reflections.findField(OHLCDataItem.class, "date");
            Reflections.makeAccessible(dateField);
            DATE_SETTER = lookup.unreflectSetter(dateField);

            final Field openField = Reflections.findField(OHLCDataItem.class, "open");
            Reflections.makeAccessible(openField);
            OPEN_SETTER = lookup.unreflectSetter(openField);

            final Field highField = Reflections.findField(OHLCDataItem.class, "high");
            Reflections.makeAccessible(highField);
            HIGH_SETTER = lookup.unreflectSetter(highField);

            final Field lowField = Reflections.findField(OHLCDataItem.class, "low");
            Reflections.makeAccessible(lowField);
            LOW_SETTER = lookup.unreflectSetter(lowField);

            final Field closeField = Reflections.findField(OHLCDataItem.class, "close");
            Reflections.makeAccessible(closeField);
            CLOSE_SETTER = lookup.unreflectSetter(closeField);

            final Field volumeField = Reflections.findField(OHLCDataItem.class, "volume");
            Reflections.makeAccessible(volumeField);
            VOLUME_SETTER = lookup.unreflectSetter(volumeField);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public MutableOHLCDataItem(final Date date) {
        this(date, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
    }

    public MutableOHLCDataItem(final OHLCDataItem ohlc) {
        this(ohlc.getDate(), ohlc.getOpen().doubleValue(), ohlc.getHigh().doubleValue(), ohlc.getLow().doubleValue(),
                ohlc.getClose().doubleValue(), ohlc.getVolume().doubleValue());
    }

    public MutableOHLCDataItem(final Date date, final double open, final double high, final double low,
            final double close, final double volume) {
        super(date, open, high, low, close, volume);
    }

    public void setOHLC(final OHLCDataItem ohlc) {
        setDate(ohlc.getDate());
        setOpen(ohlc.getOpen());
        setHigh(ohlc.getHigh());
        setLow(ohlc.getLow());
        setClose(ohlc.getClose());
        setVolume(ohlc.getVolume());
    }

    public void setDate(final Date date) {
        try {
            DATE_SETTER.invoke(this, date);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void setOpen(final Number open) {
        try {
            OPEN_SETTER.invoke(this, open);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void setHigh(final Number high) {
        try {
            HIGH_SETTER.invoke(this, high);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void setLow(final Number low) {
        try {
            LOW_SETTER.invoke(this, low);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void setClose(final Number close) {
        try {
            CLOSE_SETTER.invoke(this, close);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void setVolume(final Number volume) {
        try {
            VOLUME_SETTER.invoke(this, volume);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
