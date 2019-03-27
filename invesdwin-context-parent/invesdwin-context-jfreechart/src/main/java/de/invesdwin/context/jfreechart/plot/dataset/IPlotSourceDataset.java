package de.invesdwin.context.jfreechart.plot.dataset;

import java.io.Closeable;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

public interface IPlotSourceDataset extends XYDataset, Closeable {

    XYPlot getPlot();

    void setPlot(XYPlot plot);

    @Override
    default void close() {
        setPlot(null);
    }

    int getPrecision();

    void setPrecision(int precision);

    String getRangeAxisId();

    void setRangeAxisId(String rangeAxisId);

    boolean isLegendValueVisible(int series, int item);

}
