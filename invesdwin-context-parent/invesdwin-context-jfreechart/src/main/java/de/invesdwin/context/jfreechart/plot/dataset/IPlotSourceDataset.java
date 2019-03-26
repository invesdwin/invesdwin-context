package de.invesdwin.context.jfreechart.plot.dataset;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

public interface IPlotSourceDataset extends XYDataset {

    XYPlot getPlot();

    /**
     * Will be called with null argument once the dataset is getting removed. This information can be used to clean up
     * stuff since the dataset should be used after that.
     */
    void setPlot(XYPlot plot);

    int getPrecision();

    void setPrecision(int precision);

    String getRangeAxisId();

    void setRangeAxisId(String rangeAxisId);

    boolean isLegendValueVisible(int series, int item);

}
