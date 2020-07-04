package de.invesdwin.context.jfreechart.dataset;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;

import javax.annotation.concurrent.Immutable;

import org.jfree.data.xy.XYDataItem;

import de.invesdwin.util.lang.reflection.Reflections;

@Immutable
public class MutableXYDataItemOHLC extends XYDataItem {

    public static final MutableXYDataItemOHLC DUMMY_VALUE = new MutableXYDataItemOHLC(MutableOHLCDataItem.DUMMY_VALUE) {

        @Override
        public void setX(final Number x) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setY(final double y) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setY(final Number y) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setOHLC(final TimeRangedOHLCDataItem ohlc) {
            throw new UnsupportedOperationException();
        }
    };

    private static final MethodHandle X_SETTER;

    private TimeRangedOHLCDataItem ohlc;

    static {
        try {
            final Lookup lookup = MethodHandles.lookup();
            final Field xField = Reflections.findField(XYDataItem.class, "x");
            Reflections.makeAccessible(xField);
            X_SETTER = lookup.unreflectSetter(xField);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public MutableXYDataItemOHLC(final TimeRangedOHLCDataItem ohlc) {
        super(ohlc.getStartTime().millisValue(), ohlc.getClose());
        this.ohlc = ohlc;
    }

    public void setOHLC(final TimeRangedOHLCDataItem ohlc) {
        if (ohlc != null) {
            setX(ohlc.getStartTime().millisValue());
            setY(ohlc.getClose());
        }
        this.ohlc = ohlc;
    }

    public void setX(final Number x) {
        try {
            X_SETTER.invoke(this, x);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public TimeRangedOHLCDataItem getOHLC() {
        return ohlc;
    }

}
