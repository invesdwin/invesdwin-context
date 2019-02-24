package de.invesdwin.context.jfreechart.dataset;

import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.OHLCDataItem;

import de.invesdwin.context.jfreechart.dataset.basis.ListOHLCDataset;
import de.invesdwin.util.math.Integers;

@NotThreadSafe
public class IndexedDateTimeOHLCDataset extends ListOHLCDataset implements IIndexedDateTimeXYDataset, IPlotSource {

    private XYPlot plot;
    private int precision;
    private String rangeAxisId;

    public IndexedDateTimeOHLCDataset(final Comparable<?> key, final List<OHLCDataItem> data) {
        super(key, data);
    }

    @Override
    public double getXValue(final int series, final int item) {
        if (item < 0 || item >= getItemCount(series)) {
            return Double.NaN;
        }
        return item;
    }

    @Override
    public double getXValueAsDateTime(final int series, final int item) {
        final int usedItem = Integers.between(item, 0, getItemCount(series) - 1);
        return super.getXValue(series, usedItem);
    }

    @Override
    public XYPlot getPlot() {
        return plot;
    }

    @Override
    public void setPlot(final XYPlot plot) {
        this.plot = plot;
    }

    @Override
    public int getPrecision() {
        return precision;
    }

    @Override
    public void setPrecision(final int precision) {
        this.precision = precision;
    }

    @Override
    public String getRangeAxisId() {
        return rangeAxisId;
    }

    @Override
    public void setRangeAxisId(final String rangeAxisId) {
        this.rangeAxisId = rangeAxisId;
    }
}