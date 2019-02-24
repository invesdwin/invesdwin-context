package de.invesdwin.context.jfreechart.dataset;

import org.jfree.chart.plot.XYPlot;

public interface IPlotSource {

    XYPlot getPlot();

    void setPlot(XYPlot plot);

    int getPrecision();

    void setPrecision(int precision);

    String getRangeAxisId();

    void setRangeAxisId(String rangeAxisId);

}
