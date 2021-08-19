package de.invesdwin.context.jfreechart.dataset;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.Range;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYRangeInfo;

import de.invesdwin.util.time.date.FDate;

@NotThreadSafe
public class ListOHLCDataset extends AbstractXYDataset implements OHLCDataset, PublicCloneable, XYRangeInfo {

    /** The series key. */
    private final Comparable<?> key;

    /** Storage for the data items. */
    private List<? extends TimeRangedOHLCDataItem> data;

    /**
     * Creates a new dataset.
     *
     * @param key
     *            the series key.
     * @param data
     *            the data items.
     */
    public ListOHLCDataset(final Comparable<?> key, final List<? extends TimeRangedOHLCDataItem> data) {
        this.key = key;
        this.data = data;
    }

    public List<? extends TimeRangedOHLCDataItem> getData() {
        return data;
    }

    /**
     * Returns the series key.
     *
     * @param series
     *            the series index (ignored).
     *
     * @return The series key.
     */
    @Override
    public Comparable<?> getSeriesKey(final int series) {
        return this.key;
    }

    /**
     * Returns the x-value for a data item.
     *
     * @param series
     *            the series index (ignored).
     * @param item
     *            the item index (zero-based).
     *
     * @return The x-value.
     */
    @Override
    public Number getX(final int series, final int item) {
        try {
            return this.data.get(item).getStartTime().millisValue();
        } catch (final IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Returns the x-value for a data item as a date.
     *
     * @param series
     *            the series index (ignored).
     * @param item
     *            the item index (zero-based).
     *
     * @return The x-value as a date.
     */
    public FDate getXDate(final int series, final int item) {
        try {
            return this.data.get(item).getStartTime();
        } catch (final IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Returns the y-value.
     *
     * @param series
     *            the series index (ignored).
     * @param item
     *            the item index (zero-based).
     *
     * @return The y value.
     */
    @Override
    public Number getY(final int series, final int item) {
        return getClose(series, item);
    }

    /**
     * Returns the high value.
     *
     * @param series
     *            the series index (ignored).
     * @param item
     *            the item index (zero-based).
     *
     * @return The high value.
     */
    @Override
    public Number getHigh(final int series, final int item) {
        try {
            return this.data.get(item).getHigh();
        } catch (final IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Returns the high-value (as a double primitive) for an item within a series.
     *
     * @param series
     *            the series (zero-based index).
     * @param item
     *            the item (zero-based index).
     *
     * @return The high-value.
     */
    @Override
    public double getHighValue(final int series, final int item) {
        try {
            return this.data.get(item).getHigh();
        } catch (final IndexOutOfBoundsException e) {
            return Double.NaN;
        }
    }

    /**
     * Returns the low value.
     *
     * @param series
     *            the series index (ignored).
     * @param item
     *            the item index (zero-based).
     *
     * @return The low value.
     */
    @Override
    public Number getLow(final int series, final int item) {
        try {
            return this.data.get(item).getLow();
        } catch (final IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Returns the low-value (as a double primitive) for an item within a series.
     *
     * @param series
     *            the series (zero-based index).
     * @param item
     *            the item (zero-based index).
     *
     * @return The low-value.
     */
    @Override
    public double getLowValue(final int series, final int item) {
        try {
            return this.data.get(item).getLow();
        } catch (final IndexOutOfBoundsException e) {
            return Double.NaN;
        }
    }

    /**
     * Returns the open value.
     *
     * @param series
     *            the series index (ignored).
     * @param item
     *            the item index (zero-based).
     *
     * @return The open value.
     */
    @Override
    public Number getOpen(final int series, final int item) {
        try {
            return this.data.get(item).getOpen();
        } catch (final IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Returns the open-value (as a double primitive) for an item within a series.
     *
     * @param series
     *            the series (zero-based index).
     * @param item
     *            the item (zero-based index).
     *
     * @return The open-value.
     */
    @Override
    public double getOpenValue(final int series, final int item) {
        try {
            return this.data.get(item).getOpen();
        } catch (final IndexOutOfBoundsException e) {
            return Double.NaN;
        }
    }

    /**
     * Returns the close value.
     *
     * @param series
     *            the series index (ignored).
     * @param item
     *            the item index (zero-based).
     *
     * @return The close value.
     */
    @Override
    public Number getClose(final int series, final int item) {
        try {
            return this.data.get(item).getClose();
        } catch (final IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Returns the close-value (as a double primitive) for an item within a series.
     *
     * @param series
     *            the series (zero-based index).
     * @param item
     *            the item (zero-based index).
     *
     * @return The close-value.
     */
    @Override
    public double getCloseValue(final int series, final int item) {
        try {
            return this.data.get(item).getClose();
        } catch (final IndexOutOfBoundsException e) {
            return Double.NaN;
        }
    }

    /**
     * Returns the trading volume.
     *
     * @param series
     *            the series index (ignored).
     * @param item
     *            the item index (zero-based).
     *
     * @return The trading volume.
     */
    @Override
    public Number getVolume(final int series, final int item) {
        try {
            return this.data.get(item).getVolume();
        } catch (final IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Returns the volume-value (as a double primitive) for an item within a series.
     *
     * @param series
     *            the series (zero-based index).
     * @param item
     *            the item (zero-based index).
     *
     * @return The volume-value.
     */
    @Override
    public double getVolumeValue(final int series, final int item) {
        try {
            return this.data.get(item).getVolume();
        } catch (final IndexOutOfBoundsException e) {
            return Double.NaN;
        }
    }

    /**
     * Returns the series count.
     *
     * @return 1.
     */
    @Override
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the item count for the specified series.
     *
     * @param series
     *            the series index (ignored).
     *
     * @return The item count.
     */
    @Override
    public int getItemCount(final int series) {
        return this.data.size();
    }

    /**
     * Tests this instance for equality with an arbitrary object.
     *
     * @param obj
     *            the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ListOHLCDataset)) {
            return false;
        }
        final ListOHLCDataset that = (ListOHLCDataset) obj;
        if (!this.key.equals(that.key)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public void fireDatasetChanged() {
        super.fireDatasetChanged();
    }

    /**
     * Returns an independent copy of this dataset.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException
     *             if there is a cloning problem.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        final ListOHLCDataset clone = (ListOHLCDataset) super.clone();
        clone.data = new ArrayList<>(clone.data);
        return clone;
    }

    @Override
    public Range getRangeBounds(final List visibleSeriesKeys, final Range xRange, final boolean includeInterval) {
        return Datasets.iterateToFindRangeBoundsOHLCDataset(this, visibleSeriesKeys, xRange, includeInterval);
    }

}
