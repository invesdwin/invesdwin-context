package de.invesdwin.context.jfreechart.dataset;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

import de.invesdwin.util.lang.Reflections;

@NotThreadSafe
public class ListOHLCSeries extends XYSeries {

    private static final MethodHandle MH_MINX_SETTER;
    private static final MethodHandle MH_MAXX_SETTER;
    private static final MethodHandle MH_MINY_SETTER;
    private static final MethodHandle MH_MAXY_SETTER;

    static {
        try {
            final Field minXField = XYSeries.class.getDeclaredField("minX");
            Reflections.makeAccessible(minXField);
            MH_MINX_SETTER = MethodHandles.lookup().unreflectSetter(minXField);
            final Field maxXField = XYSeries.class.getDeclaredField("maxX");
            Reflections.makeAccessible(maxXField);
            MH_MAXX_SETTER = MethodHandles.lookup().unreflectSetter(maxXField);
            final Field minYField = XYSeries.class.getDeclaredField("minY");
            Reflections.makeAccessible(minYField);
            MH_MINY_SETTER = MethodHandles.lookup().unreflectSetter(minYField);
            final Field maxYField = XYSeries.class.getDeclaredField("maxY");
            Reflections.makeAccessible(maxYField);
            MH_MAXY_SETTER = MethodHandles.lookup().unreflectSetter(maxYField);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ListOHLCSeries(final Comparable<?> key) {
        super(key, false, false);
    }

    @SuppressWarnings("unchecked")
    public List<XYDataItem> getData() {
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

    public void updateBoundsForAddedItem(final XYDataItem item) {
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

}
