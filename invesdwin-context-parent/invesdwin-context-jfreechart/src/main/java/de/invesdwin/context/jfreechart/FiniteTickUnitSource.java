package de.invesdwin.context.jfreechart;

import javax.annotation.concurrent.NotThreadSafe;

import org.fest.reflect.field.Invoker;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.TickUnitSource;

import de.invesdwin.util.lang.reflection.Reflections;

@NotThreadSafe
public final class FiniteTickUnitSource implements TickUnitSource {

    private static final double INFINITE_REPLACE_WITH = 0D;
    private final TickUnitSource delegate;

    private FiniteTickUnitSource(final TickUnitSource delegate) {
        this.delegate = delegate;
    }

    @Override
    public TickUnit getLargerTickUnit(final TickUnit unit) {
        if (shouldFixInfinite(unit.getSize())) {
            //prevent infinite loop inside NumberTickUnitSource
            return fixInfinite(unit);
        }
        return fixInfinite(delegate.getLargerTickUnit(unit));
    }

    @Override
    public TickUnit getCeilingTickUnit(final TickUnit unit) {
        if (shouldFixInfinite(unit.getSize())) {
            //prevent infinite loop inside NumberTickUnitSource
            return fixInfinite(unit);
        }
        return fixInfinite(delegate.getCeilingTickUnit(unit));
    }

    @Override
    public TickUnit getCeilingTickUnit(final double size) {
        if (shouldFixInfinite(size)) {
            //prevent infinite loop inside NumberTickUnitSource
            return new NumberTickUnit(fixInfinite(size));
        }
        return fixInfinite(delegate.getCeilingTickUnit(size));
    }

    public static boolean shouldFixInfinite(final double size) {
        return Double.isInfinite(size) || size == INFINITE_REPLACE_WITH;
    }

    public static TickUnit fixInfinite(final TickUnit unit) {
        final Invoker<Double> invoker = Reflections.field("size").ofType(double.class).in(unit);
        double newSize = invoker.get();
        newSize = fixInfinite(newSize);
        invoker.set(newSize);
        return unit;
    }

    public static double fixInfinite(final double newSize) {
        if (shouldFixInfinite(newSize)) {
            //should be the result of division by zero, need to fix it to not get an exception
            return INFINITE_REPLACE_WITH;
        } else {
            return newSize;
        }
    }

    public static FiniteTickUnitSource maybeWrap(final TickUnitSource delegate) {
        if (delegate instanceof FiniteTickUnitSource) {
            return (FiniteTickUnitSource) delegate;
        } else {
            return new FiniteTickUnitSource(delegate);
        }
    }

}
