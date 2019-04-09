package de.invesdwin.context.jfreechart.plot.dataset;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.panel.helper.config.series.ISeriesProvider;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.expression.IExpression;

@Immutable
public class DisabledXYDataset extends AbstractXYDataset implements IPlotSourceDataset {

    private final IPlotSourceDataset enabledDataset;

    public DisabledXYDataset(final IPlotSourceDataset enabledDataset) {
        Assertions.checkNotNull(enabledDataset);
        if (enabledDataset instanceof DisabledXYDataset) {
            throw new IllegalArgumentException(
                    "enabledDataset should not be an instance of " + DisabledXYDataset.class.getSimpleName());
        }
        this.enabledDataset = enabledDataset;
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
        return enabledDataset.getPlot();
    }

    @Override
    public void setPlot(final XYPlot plot) {
        enabledDataset.setPlot(plot);
    }

    @Override
    public int getPrecision() {
        return 0;
    }

    @Override
    public void setPrecision(final int precision) {
        enabledDataset.setPrecision(precision);
    }

    public static XYDataset maybeUnwrap(final XYDataset dataset) {
        if (dataset instanceof DisabledXYDataset) {
            final DisabledXYDataset cDataset = (DisabledXYDataset) dataset;
            return cDataset.getEnabledDataset();
        } else {
            return dataset;
        }
    }

    @Override
    public String getRangeAxisId() {
        return enabledDataset.getRangeAxisId();
    }

    @Override
    public void setRangeAxisId(final String rangeAxisId) {
        enabledDataset.setRangeAxisId(rangeAxisId);
    }

    @Override
    public boolean isLegendValueVisible(final int series, final int item) {
        return false;
    }

    @Override
    public void close() {
        enabledDataset.close();
    }

    @Override
    public String getSeriesTitle() {
        return enabledDataset.getSeriesTitle();
    }

    @Override
    public void setSeriesTitle(final String seriesTitle) {
        enabledDataset.setSeriesTitle(seriesTitle);
    }

    @Override
    public ISeriesProvider getSeriesProvider() {
        return enabledDataset.getSeriesProvider();
    }

    @Override
    public void setSeriesProvider(final ISeriesProvider seriesProvider) {
        enabledDataset.setSeriesProvider(seriesProvider);
    }

    @Override
    public IExpression[] getSeriesArguments() {
        return enabledDataset.getSeriesArguments();
    }

    @Override
    public void setSeriesArguments(final IExpression[] seriesArguments) {
        enabledDataset.setSeriesArguments(seriesArguments);
    }

}
