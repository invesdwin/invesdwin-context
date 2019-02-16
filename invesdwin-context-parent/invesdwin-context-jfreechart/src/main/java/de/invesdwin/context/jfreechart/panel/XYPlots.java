package de.invesdwin.context.jfreechart.panel;

import java.text.DecimalFormat;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.Dataset;

import de.invesdwin.trading.charts.richclient.swing.chart.panel.jfreechart.datetime.IPlotSource;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

@Immutable
public final class XYPlots {

    private XYPlots() {}

    public static int getFreeDatasetIndex(final XYPlot plot) {
        for (int i = 0; i < plot.getDatasetCount(); i++) {
            if (plot.getDataset(i) == null) {
                return i;
            }
        }
        return plot.getDatasetCount();
    }

    public static void updateRangeAxisPrecision(final XYPlot plot) {
        int precision = 0;
        boolean datasetVisible = false;
        for (int datasetIndex = 0; datasetIndex < plot.getDatasetCount(); datasetIndex++) {
            final Dataset dataset = plot.getDataset(datasetIndex);
            if (dataset != null && !(dataset instanceof DisabledXYDataset)) {
                final IPlotSource plotSource = (IPlotSource) dataset;
                precision = Integers.max(precision, plotSource.getPrecision());
                datasetVisible = true;
            }
        }
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(newRangeAxisFormat(precision));
        rangeAxis.setTickLabelsVisible(datasetVisible);
        rangeAxis.configure();
    }

    public static DecimalFormat newRangeAxisFormat(final int decimalDigits) {
        return Decimal.newDecimalFormatInstance(
                PercentScale.RATE.getFormat(Percent.ZERO_PERCENT, false, decimalDigits, false));
    }

    public static NumberAxis newRangeAxis(final int precision) {
        final NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setAutoRangeIncludesZero(false);
        rangeAxis.setAutoRange(true);
        rangeAxis.setNumberFormatOverride(newRangeAxisFormat(precision));
        rangeAxis.setTickLabelsVisible(false);
        return rangeAxis;
    }

    public static boolean hasDataset(final XYPlot plot) {
        for (int datasetIndex = 0; datasetIndex < plot.getDatasetCount(); datasetIndex++) {
            if (plot.getDataset() != null) {
                return true;
            }
        }
        return false;
    }

    public static void removeDataset(final XYPlot plot, final int datasetIndex) {
        final int lastDatasetIndex = plot.getDatasetCount() - 1;
        for (int i = datasetIndex; i < lastDatasetIndex; i++) {
            plot.setDataset(i, plot.getDataset(i + 1));
            plot.setRenderer(i, plot.getRenderer(i + 1));
        }
        plot.setDataset(lastDatasetIndex, null);
        plot.setRenderer(lastDatasetIndex, null);
    }

}
