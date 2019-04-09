package de.invesdwin.context.jfreechart.plot.dataset;

import java.io.Closeable;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.panel.helper.config.series.ISeriesProvider;
import de.invesdwin.util.math.expression.IExpression;

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

    String getSeriesTitle();

    void setSeriesTitle(String seriesTitle);

    ISeriesProvider getSeriesProvider();

    void setSeriesProvider(ISeriesProvider seriesProvider);

    void setSeriesArguments(IExpression[] seriesArguments);

    IExpression[] getSeriesArguments();

    default boolean hasSeriesArguments() {
        final IExpression[] args = getSeriesArguments();
        return args != null && args.length > 0;
    }

}
