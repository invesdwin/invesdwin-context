package de.invesdwin.context.jfreechart.dataset;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;

@Immutable
public class DisabledXYDataset extends AbstractXYDataset implements IPlotSource {

    private final XYDataset enabledDataset;
    private final IPlotSource plotSource;

    public DisabledXYDataset(final XYDataset enabledDataset) {
        this.enabledDataset = enabledDataset;
        this.plotSource = (IPlotSource) enabledDataset;
    }

    public XYDataset getEnabledDataset() {
        return enabledDataset;
    }

    @Override
    public int getItemCount(final int series) {
        return enabledDataset.getItemCount(series);
    }

    @Override
    public Number getX(final int series, final int item) {
        return enabledDataset.getX(series, item);
    }

    @Override
    public Number getY(final int series, final int item) {
        return Double.NaN;
    }

    @Override
    public int getSeriesCount() {
        return enabledDataset.getSeriesCount();
    }

    @Override
    public Comparable getSeriesKey(final int series) {
        return enabledDataset.getSeriesKey(series);
    }

    @Override
    public XYPlot getPlot() {
        return plotSource.getPlot();
    }

    @Override
    public void setPlot(final XYPlot plot) {
        plotSource.setPlot(plot);
    }

    @Override
    public int getPrecision() {
        return 0;
    }

    @Override
    public void setPrecision(final int precision) {
        plotSource.setPrecision(precision);
    }

    public static XYDataset maybeUnwrap(final XYDataset dataset) {
        if (dataset instanceof DisabledXYDataset) {
            final DisabledXYDataset cDataset = (DisabledXYDataset) dataset;
            return cDataset.getEnabledDataset();
        } else {
            return dataset;
        }
    }

}
