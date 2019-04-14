package de.invesdwin.context.jfreechart.plot.dataset;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.panel.helper.config.series.expression.IExpressionSeriesProvider;
import de.invesdwin.context.jfreechart.panel.helper.config.series.indicator.IIndicatorSeriesProvider;
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

    @SuppressWarnings("rawtypes")
    @Deprecated
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
    public Integer getPrecision() {
        return 0;
    }

    @Override
    public void setPrecision(final Integer precision) {
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
    public IIndicatorSeriesProvider getIndicatorSeriesProvider() {
        return enabledDataset.getIndicatorSeriesProvider();
    }

    @Override
    public void setIndicatorSeriesProvider(final IIndicatorSeriesProvider indicatorSeriesProvider) {
        enabledDataset.setIndicatorSeriesProvider(indicatorSeriesProvider);
    }

    @Override
    public IExpression[] getIndicatorSeriesArguments() {
        return enabledDataset.getIndicatorSeriesArguments();
    }

    @Override
    public void setIndicatorSeriesArguments(final IExpression[] indicatorSeriesArguments) {
        enabledDataset.setIndicatorSeriesArguments(indicatorSeriesArguments);
    }

    @Override
    public IExpressionSeriesProvider getExpressionSeriesProvider() {
        return enabledDataset.getExpressionSeriesProvider();
    }

    @Override
    public void setExpressionSeriesProvider(final IExpressionSeriesProvider expressionSeriesProvider) {
        enabledDataset.setExpressionSeriesProvider(expressionSeriesProvider);
    }

    @Override
    public String getExpressionSeriesArguments() {
        return enabledDataset.getExpressionSeriesArguments();
    }

    @Override
    public void setExpressionSeriesArguments(final String expressionSeriesArguments) {
        enabledDataset.setExpressionSeriesArguments(expressionSeriesArguments);
    }

}
