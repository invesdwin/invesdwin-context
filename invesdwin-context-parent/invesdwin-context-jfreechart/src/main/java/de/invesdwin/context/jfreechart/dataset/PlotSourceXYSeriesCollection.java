package de.invesdwin.context.jfreechart.dataset;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;

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

}
