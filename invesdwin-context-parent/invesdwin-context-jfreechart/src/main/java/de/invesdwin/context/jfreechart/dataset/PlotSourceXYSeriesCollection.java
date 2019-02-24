package de.invesdwin.context.jfreechart.dataset;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.invesdwin.context.jfreechart.dataset.basis.ListXYSeriesOHLC;

@NotThreadSafe
public class PlotSourceXYSeriesCollection extends XYSeriesCollection implements IPlotSource {

    private XYPlot plot;
    private int precision;
    private String rangeAxisId;

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

}
