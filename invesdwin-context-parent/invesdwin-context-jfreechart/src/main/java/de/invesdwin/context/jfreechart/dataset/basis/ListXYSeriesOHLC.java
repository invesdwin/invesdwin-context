package de.invesdwin.context.jfreechart.dataset.basis;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

import de.invesdwin.util.lang.Reflections;

@NotThreadSafe
public class ListXYSeriesOHLC extends XYSeries {

    private static final MethodHandle MH_MINX_SETTER;
    private static final MethodHandle MH_MAXX_SETTER;
    private static final MethodHandle MH_MINY_SETTER;
    private static final MethodHandle MH_MAXY_SETTER;

    static {
        try {
            final Field minXField = XYSeries.class.getDeclaredField("minX");
            Reflections.makeAccessibleFinal(minXField);
            MH_MINX_SETTER = MethodHandles.lookup().unreflectSetter(minXField);
            final Field maxXField = XYSeries.class.getDeclaredField("maxX");
            Reflections.makeAccessibleFinal(maxXField);
            MH_MAXX_SETTER = MethodHandles.lookup().unreflectSetter(maxXField);
            final Field minYField = XYSeries.class.getDeclaredField("minY");
            Reflections.makeAccessibleFinal(minYField);
            MH_MINY_SETTER = MethodHandles.lookup().unreflectSetter(minYField);
            final Field maxYField = XYSeries.class.getDeclaredField("maxY");
            Reflections.makeAccessibleFinal(maxYField);
            MH_MAXY_SETTER = MethodHandles.lookup().unreflectSetter(maxYField);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ListXYSeriesOHLC(final Comparable<?> key) {
        super(key, false, false);
    }

    @SuppressWarnings("unchecked")
    public List<XYDataItemOHLC> getData() {
        return data;
    }

    public void setMinX(final double minX) {
        try {
            MH_MINX_SETTER.invoke(this, minX);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void setMaxX(final double maxX) {
        try {
            MH_MAXX_SETTER.invoke(this, maxX);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void setMinY(final double minY) {
        try {
            MH_MINY_SETTER.invoke(this, minY);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void setMaxY(final double maxY) {
        try {
            MH_MAXY_SETTER.invoke(this, maxY);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void updateBoundsForAddedItem(final XYDataItemOHLC item) {
        final double x = item.getXValue();
        setMinX(minIgnoreNaN(getMinX(), x));
        setMaxX(maxIgnoreNaN(getMaxX(), x));
        if (item.getY() != null) {
            final double y = item.getYValue();
            setMinY(minIgnoreNaN(getMinY(), y));
            setMaxY(maxIgnoreNaN(getMaxY(), y));
        }
    }

    private double minIgnoreNaN(final double a, final double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        }
        return Math.min(a, b);
    }

    private double maxIgnoreNaN(final double a, final double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        }
        return Math.max(a, b);
    }

    @Deprecated
    @Override
    public void add(final double x, final double y) {
        throw newUseGetDataException();
    }

    private UnsupportedOperationException newUseGetDataException() {
        return new UnsupportedOperationException("Use getData().add(...) instead");
    }

    @Deprecated
    @Override
    public void add(final double x, final double y, final boolean notify) {
        throw newUseGetDataException();
    }

    @Deprecated
    @Override
    public void add(final double x, final Number y) {
        throw newUseGetDataException();
    }

    @Deprecated
    @Override
    public void add(final double x, final Number y, final boolean notify) {
        throw newUseGetDataException();
    }

    @Deprecated
    @Override
    public void add(final Number x, final Number y) {
        throw newUseGetDataException();
    }

    @Deprecated
    @Override
    public void add(final Number x, final Number y, final boolean notify) {
        throw newUseGetDataException();
    }

    @Deprecated
    @Override
    public void add(final XYDataItem item) {
        throw newUseGetDataException();
    }

    @Deprecated
    @Override
    public void add(final XYDataItem item, final boolean notify) {
        throw newUseGetDataException();
    }

    @Deprecated
    @Override
    public XYDataItem addOrUpdate(final double x, final double y) {
        throw newUseGetDataException();
    }

    @Deprecated
    @Override
    public XYDataItem addOrUpdate(final Number x, final Number y) {
        throw newUseGetDataException();
    }

    @Deprecated
    @Override
    public XYDataItem addOrUpdate(final XYDataItem item) {
        throw newUseGetDataException();
    }

}
