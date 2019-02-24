package de.invesdwin.context.jfreechart.plot;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.Dataset;

import de.invesdwin.context.jfreechart.dataset.DisabledXYDataset;
import de.invesdwin.context.jfreechart.dataset.IPlotSource;
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

    public static void updateRangeAxes(final XYPlot plot) {
        final Map<String, RangeAxisData> rangeAxisId_data = new LinkedHashMap<>();
        int rangeAxisIndex = -1;
        for (int datasetIndex = 0; datasetIndex < plot.getDatasetCount(); datasetIndex++) {
            final Dataset dataset = plot.getDataset(datasetIndex);
            if (dataset != null) {
                final IPlotSource plotSource = (IPlotSource) dataset;
                final boolean visible = !(dataset instanceof DisabledXYDataset);
                String rangeAxisId = plotSource.getRangeAxisId();
                if (!visible) {
                    rangeAxisId += "_invisible";
                }
                RangeAxisData data = rangeAxisId_data.get(rangeAxisId);
                if (data == null) {
                    rangeAxisIndex++;
                    data = new RangeAxisData(rangeAxisId, rangeAxisIndex);
                    rangeAxisId_data.put(rangeAxisId, data);
                }
                data.getDatasetIndexes().add(datasetIndex);
                if (visible) {
                    data.setPrecision(Integers.max(data.getPrecision(), plotSource.getPrecision()));
                    data.setVisible(true);
                }
            }
        }
        removeRangeAxes(plot);
        if (rangeAxisId_data.isEmpty()) {
            plot.setRangeAxis(newRangeAxis(0, false, false));
        } else {
            int countVisibleRangeAxes = 0;
            //first add the visible range axis, right=0 and left=1
            for (final RangeAxisData rangeAxisData : rangeAxisId_data.values()) {
                if (rangeAxisData.isVisible()) {
                    countVisibleRangeAxes++;
                    addRangeAxis(plot, countVisibleRangeAxes, rangeAxisData);
                }
            }
            //then the rest are the invisible ones
            for (final RangeAxisData rangeAxisData : rangeAxisId_data.values()) {
                if (!rangeAxisData.isVisible()) {
                    addRangeAxis(plot, countVisibleRangeAxes, rangeAxisData);
                }
            }
            configureRangeAxes(plot);
        }
    }

    private static void addRangeAxis(final XYPlot plot, final int countVisibleRangeAxes,
            final RangeAxisData rangeAxisData) {
        final boolean visible = rangeAxisData.isVisible() && countVisibleRangeAxes <= 2;
        final boolean autorange = rangeAxisData.isVisible();
        final NumberAxis rangeAxis = newRangeAxis(rangeAxisData.getPrecision(), visible, autorange);
        plot.setRangeAxis(rangeAxisData.getRangeAxisIndex(), rangeAxis);
        for (final int datasetIndex : rangeAxisData.getDatasetIndexes()) {
            plot.mapDatasetToDomainAxis(datasetIndex, 0);
            plot.mapDatasetToRangeAxis(datasetIndex, rangeAxisData.getRangeAxisIndex());
        }
    }

    public static void configureRangeAxes(final XYPlot plot) {
        for (int i = 0; i < plot.getRangeAxisCount(); i++) {
            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis(i);
            if (rangeAxis == null) {
                break;
            }
            rangeAxis.configure();
        }
    }

    public static void removeRangeAxes(final XYPlot plot) {
        final int rangeAxisCount = plot.getRangeAxisCount();
        for (int i = 0; i < rangeAxisCount; i++) {
            plot.setRangeAxis(i, null);
        }
    }

    public static NumberAxis newRangeAxis(final int precision, final boolean visible, final boolean autorange) {
        final NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setAutoRangeIncludesZero(false);
        rangeAxis.setNumberFormatOverride(Decimal
                .newDecimalFormatInstance(PercentScale.RATE.getFormat(Percent.ZERO_PERCENT, false, precision, false)));
        rangeAxis.setVisible(visible);
        rangeAxis.setAutoRange(autorange);
        if (!autorange) {
            rangeAxis.setRange(0, 1);
        }
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
