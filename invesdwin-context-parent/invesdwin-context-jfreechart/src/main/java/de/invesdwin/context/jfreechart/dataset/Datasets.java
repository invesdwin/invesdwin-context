package de.invesdwin.context.jfreechart.dataset;

import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.jfree.data.Range;
import org.jfree.data.statistics.BoxAndWhiskerXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.util.math.Integers;

@Immutable
public final class Datasets {

    private static final int INCREMENT_THRESHOLD = 10000;

    private Datasets() {
    }

    /**
     * Adapted from DatasetUtils.
     * 
     * Returns the range of y-values in the specified dataset for the data items belonging to the visible series and
     * with x-values in the given range.
     *
     * @param dataset
     *            the dataset ({@code null} not permitted).
     * @param visibleSeriesKeys
     *            the visible series keys ({@code null} not permitted).
     * @param xRange
     *            the x-range ({@code null} not permitted).
     * @param includeInterval
     *            a flag that determines whether or not the y-interval for the dataset is included (this only applies if
     *            the dataset is an instance of IntervalXYDataset).
     *
     * @return The y-range (possibly {@code null}).
     */
    @SuppressWarnings("rawtypes")
    public static Range iterateToFindRangeBounds(final XYDataset dataset, final List visibleSeriesKeys,
            final Range xRange, final boolean includeInterval) {
        // handle three cases by dataset type
        if (dataset instanceof OHLCDataset) {
            return iterateToFindRangeBoundsOHLCDataset((OHLCDataset) dataset, visibleSeriesKeys, xRange,
                    includeInterval);
        } else if (dataset instanceof BoxAndWhiskerXYDataset) {
            return iterateToFindRangeBoundsBoxAndWhiskerXYDataset((BoxAndWhiskerXYDataset) dataset, visibleSeriesKeys,
                    xRange, includeInterval);
        } else if (dataset instanceof IntervalXYDataset) {
            return iterateToFindRangeBoundsIntervalXYDataset((IntervalXYDataset) dataset, visibleSeriesKeys, xRange,
                    includeInterval);
        } else {
            return iterateToFindRangeBoundsXYDataset(dataset, visibleSeriesKeys, xRange, includeInterval);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Range iterateToFindRangeBoundsBoxAndWhiskerXYDataset(final BoxAndWhiskerXYDataset dataset,
            final List visibleSeriesKeys, final Range xRange, final boolean includeInterval) {
        if (includeInterval) {
            if (dataset.getSeriesCount() <= 0) {
                return null;
            }

            double minimum = Double.POSITIVE_INFINITY;
            double maximum = Double.NEGATIVE_INFINITY;

            final int firstX = Integers.max(0, (int) xRange.getLowerBound() - 1);
            final int lastX = Integers.min(dataset.getItemCount(0), (int) xRange.getUpperBound() + 1);
            final int increment = Integers.max(1, (int) xRange.getLength() / INCREMENT_THRESHOLD);

            // handle special case of BoxAndWhiskerXYDataset
            final BoxAndWhiskerXYDataset bx = dataset;
            final Iterator iterator = visibleSeriesKeys.iterator();
            while (iterator.hasNext()) {
                final Comparable seriesKey = (Comparable) iterator.next();
                final int series = dataset.indexOf(seriesKey);
                final int itemCount = Integers.min(lastX, dataset.getItemCount(series));
                for (int item = firstX; item < itemCount; item += increment) {
                    final double x = bx.getXValue(series, item);
                    if (xRange.contains(x)) {
                        final Number lvalue = bx.getMinRegularValue(series, item);
                        final Number uvalue = bx.getMaxRegularValue(series, item);
                        if (lvalue != null) {
                            minimum = Math.min(minimum, lvalue.doubleValue());
                        }
                        if (uvalue != null) {
                            maximum = Math.max(maximum, uvalue.doubleValue());
                        }
                    }
                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            } else {
                return new Range(minimum, maximum);
            }
        } else {
            return iterateToFindRangeBoundsXYDataset(dataset, visibleSeriesKeys, xRange, includeInterval);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Range iterateToFindRangeBoundsOHLCDataset(final OHLCDataset dataset, final List visibleSeriesKeys,
            final Range xRange, final boolean includeInterval) {
        if (includeInterval) {
            if (dataset.getSeriesCount() <= 0) {
                return null;
            }

            double minimum = Double.POSITIVE_INFINITY;
            double maximum = Double.NEGATIVE_INFINITY;

            final int firstX = Integers.max(0, (int) xRange.getLowerBound() - 1);
            final int lastX = Integers.min(dataset.getItemCount(0), (int) xRange.getUpperBound() + 1);
            final int increment = Integers.max(1, (int) xRange.getLength() / INCREMENT_THRESHOLD);

            // handle special case of OHLCDataset
            final OHLCDataset ohlc = dataset;
            final Iterator iterator = visibleSeriesKeys.iterator();
            while (iterator.hasNext()) {
                final Comparable seriesKey = (Comparable) iterator.next();
                final int series = dataset.indexOf(seriesKey);
                final int itemCount = Integers.min(lastX, dataset.getItemCount(series));
                for (int item = firstX; item < itemCount; item += increment) {
                    final double x = ohlc.getXValue(series, item);
                    if (xRange.contains(x)) {
                        final double lvalue = ohlc.getLowValue(series, item);
                        final double uvalue = ohlc.getHighValue(series, item);
                        if (!Double.isNaN(lvalue)) {
                            minimum = Math.min(minimum, lvalue);
                        }
                        if (!Double.isNaN(uvalue)) {
                            maximum = Math.max(maximum, uvalue);
                        }
                    }
                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            } else {
                return new Range(minimum, maximum);
            }
        } else {
            return iterateToFindRangeBoundsXYDataset(dataset, visibleSeriesKeys, xRange, includeInterval);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Range iterateToFindRangeBoundsIntervalXYDataset(final IntervalXYDataset dataset,
            final List visibleSeriesKeys, final Range xRange, final boolean includeInterval) {
        if (includeInterval) {
            if (dataset.getSeriesCount() <= 0) {
                return null;
            }

            double minimum = Double.POSITIVE_INFINITY;
            double maximum = Double.NEGATIVE_INFINITY;

            final int firstX = Integers.max(0, (int) xRange.getLowerBound() - 1);
            final int lastX = Integers.min(dataset.getItemCount(0), (int) xRange.getUpperBound() + 1);
            final int increment = Integers.max(1, (int) xRange.getLength() / INCREMENT_THRESHOLD);

            // handle special case of IntervalXYDataset
            final IntervalXYDataset ixyd = dataset;
            final Iterator iterator = visibleSeriesKeys.iterator();
            while (iterator.hasNext()) {
                final Comparable seriesKey = (Comparable) iterator.next();
                final int series = dataset.indexOf(seriesKey);
                final int itemCount = Integers.min(lastX, dataset.getItemCount(series));
                for (int item = firstX; item < itemCount; item += increment) {
                    final double x = ixyd.getXValue(series, item);
                    if (xRange.contains(x)) {
                        final double yvalue = ixyd.getYValue(series, item);
                        final double lvalue = ixyd.getStartYValue(series, item);
                        final double uvalue = ixyd.getEndYValue(series, item);
                        if (!Double.isNaN(yvalue)) {
                            minimum = Math.min(minimum, yvalue);
                            maximum = Math.max(maximum, yvalue);
                        }
                        if (!Double.isNaN(lvalue)) {
                            minimum = Math.min(minimum, lvalue);
                        }
                        if (!Double.isNaN(uvalue)) {
                            maximum = Math.max(maximum, uvalue);
                        }
                    }
                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            } else {
                return new Range(minimum, maximum);
            }
        } else {
            return iterateToFindRangeBoundsXYDataset(dataset, visibleSeriesKeys, xRange, includeInterval);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Range iterateToFindRangeBoundsXYDataset(final XYDataset dataset, final List visibleSeriesKeys,
            final Range xRange, final boolean includeInterval) {
        if (dataset.getSeriesCount() <= 0) {
            return null;
        }

        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;

        final int firstX = Integers.max(0, (int) xRange.getLowerBound() - 1);
        final int lastX = Integers.min(dataset.getItemCount(0), (int) xRange.getUpperBound() + 1);
        final int increment = Integers.max(1, (int) xRange.getLength() / INCREMENT_THRESHOLD);

        // standard case - plain XYDataset
        final Iterator iterator = visibleSeriesKeys.iterator();
        while (iterator.hasNext()) {
            final Comparable seriesKey = (Comparable) iterator.next();
            final int series = dataset.indexOf(seriesKey);
            final int itemCount = Integers.min(lastX, dataset.getItemCount(series));
            for (int item = firstX; item < itemCount; item += increment) {
                final double x = dataset.getXValue(series, item);
                final double y = dataset.getYValue(series, item);
                if (xRange.contains(x)) {
                    if (!Double.isNaN(y)) {
                        minimum = Math.min(minimum, y);
                        maximum = Math.max(maximum, y);
                    }
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        } else {
            return new Range(minimum, maximum);
        }
    }

}
