package de.invesdwin.context.jfreechart.plot.dataset;

import org.jfree.chart.plot.XYPlot;

public interface IPlotSourceDataset {

    XYPlot getPlot();

    void setPlot(XYPlot plot);

    int getPrecision();

    void setPrecision(int precision);

    String getRangeAxisId();

    void setRangeAxisId(String rangeAxisId);

    boolean isLegendValueVisible(int series, int item);

}
