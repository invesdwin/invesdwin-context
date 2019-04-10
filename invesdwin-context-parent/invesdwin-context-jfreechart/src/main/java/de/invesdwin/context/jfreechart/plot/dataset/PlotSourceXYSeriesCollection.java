package de.invesdwin.context.jfreechart.plot.dataset;

import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.xy.XYRangeInfo;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.invesdwin.context.jfreechart.panel.helper.config.series.ISeriesProvider;
import de.invesdwin.context.jfreechart.plot.dataset.basis.ListXYSeriesOHLC;
import de.invesdwin.context.jfreechart.plot.dataset.basis.XYDataItemOHLC;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.expression.IExpression;

@NotThreadSafe
public class PlotSourceXYSeriesCollection extends XYSeriesCollection implements IPlotSourceDataset, XYRangeInfo {

    private XYPlot plot;
    private Integer precision;
    private String rangeAxisId;
    private String seriesTitle;
    private ISeriesProvider seriesProvider;
    private IExpression[] seriesArguments;

    public PlotSourceXYSeriesCollection(final String seriesTitle) {
        this.seriesTitle = seriesTitle;
        Assertions.checkNotNull(seriesTitle);
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
    public Integer getPrecision() {
        return precision;
    }

    @Override
    public void setPrecision(final Integer precision) {
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

    @Deprecated
    @Override
    public void addSeries(final XYSeries series) {
        if (!(series instanceof ListXYSeriesOHLC)) {
            throw new IllegalArgumentException("series must be of type: " + ListXYSeriesOHLC.class.getSimpleName());
        }
        super.addSeries(series);
    }

    public void addSeries(final ListXYSeriesOHLC series) {
        super.addSeries(series);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ListXYSeriesOHLC> getSeries() {
        return super.getSeries();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ListXYSeriesOHLC getSeries(final Comparable key) {
        return (ListXYSeriesOHLC) super.getSeries(key);
    }

    @Override
    public ListXYSeriesOHLC getSeries(final int series) {
        return (ListXYSeriesOHLC) super.getSeries(series);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Range getRangeBounds(final List visibleSeriesKeys, final Range xRange, final boolean includeInterval) {
        if (includeInterval) {
            double minimum = Double.POSITIVE_INFINITY;
            double maximum = Double.NEGATIVE_INFINITY;
            // handle special case of OHLCDataset
            final Iterator iterator = visibleSeriesKeys.iterator();
            while (iterator.hasNext()) {
                final Comparable seriesKey = (Comparable) iterator.next();
                final int series = indexOf(seriesKey);
                final int itemCount = getItemCount(series);
                final ListXYSeriesOHLC cSeries = getSeries().get(series);
                for (int item = 0; item < itemCount; item++) {
                    final double x = getXValue(series, item);
                    if (xRange.contains(x)) {
                        final XYDataItemOHLC ohlc = cSeries.getData().get(item);
                        final double lvalue = cSeries.getItemMinY(ohlc);
                        final double uvalue = cSeries.getItemMaxY(ohlc);
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
            return DatasetUtils.iterateToFindRangeBounds(this, visibleSeriesKeys, xRange, includeInterval);
        }
    }

    @Override
    public boolean isLegendValueVisible(final int series, final int item) {
        return !Double.isNaN(getYValue(series, item));
    }

    @Override
    public String getSeriesTitle() {
        return seriesTitle;
    }

    @Override
    public void setSeriesTitle(final String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }

    @Override
    public void setSeriesProvider(final ISeriesProvider seriesProvider) {
        this.seriesProvider = seriesProvider;
    }

    @Override
    public ISeriesProvider getSeriesProvider() {
        return seriesProvider;
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
