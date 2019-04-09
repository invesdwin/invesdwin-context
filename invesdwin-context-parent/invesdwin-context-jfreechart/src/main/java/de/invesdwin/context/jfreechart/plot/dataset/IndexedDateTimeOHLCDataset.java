package de.invesdwin.context.jfreechart.plot.dataset;

import java.util.Date;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.OHLCDataItem;

import de.invesdwin.context.jfreechart.panel.helper.config.series.ISeriesProvider;
import de.invesdwin.context.jfreechart.plot.dataset.basis.ListOHLCDataset;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.expression.IExpression;

@NotThreadSafe
public class IndexedDateTimeOHLCDataset extends ListOHLCDataset
        implements IIndexedDateTimeXYDataset, IPlotSourceDataset {

    private XYPlot plot;
    private int precision;
    private String rangeAxisId;
    private final String seriesTitle;
    private ISeriesProvider seriesProvider;
    private IExpression[] seriesArguments;

    public IndexedDateTimeOHLCDataset(final String seriesKey, final List<OHLCDataItem> data) {
        super(seriesKey, data);
        Assertions.checkNotNull(seriesKey);
        this.seriesTitle = seriesKey;
    }

    @Override
    public Number getX(final int series, final int item) {
        return getXValue(series, item);
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
        return getData().get(usedItem).getDate().getTime();
    }

    @Override
    public int getDateTimeAsItemIndex(final int series, final Date time) {
        return bisect(getData(), time);
    }

    private static int bisect(final List<OHLCDataItem> keys, final Date skippingKeysAbove) {
        int lo = 0;
        int hi = keys.size();
        while (lo < hi) {
            final int mid = (lo + hi) / 2;
            //if (x < list.get(mid)) {
            final Date midKey = keys.get(mid).getDate();
            final int compareTo = midKey.compareTo(skippingKeysAbove);
            switch (compareTo) {
            case -1:
                lo = mid + 1;
                break;
            case 0:
                return mid;
            case 1:
                hi = mid;
                break;
            default:
                throw UnknownArgumentException.newInstance(Integer.class, compareTo);
            }
        }
        if (lo <= 0) {
            return 0;
        }
        if (lo >= keys.size()) {
            lo = lo - 1;
        }
        final Date loTime = keys.get(lo).getDate();
        if (loTime.after(skippingKeysAbove)) {
            final int index = lo - 1;
            return index;
        } else {
            return lo;
        }
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

    @Override
    public boolean isLegendValueVisible(final int series, final int item) {
        return true;
    }

    @Override
    public String getSeriesTitle() {
        return seriesTitle;
    }

    @Override
    public ISeriesProvider getSeriesProvider() {
        return seriesProvider;
    }

    @Override
    public void setSeriesProvider(final ISeriesProvider seriesProvider) {
        this.seriesProvider = seriesProvider;
    }

    @Override
    public void setSeriesArguments(final IExpression[] seriesArguments) {
        this.seriesArguments = seriesArguments;
    }

    @Override
    public IExpression[] getSeriesArguments() {
        return seriesArguments;
    }

}