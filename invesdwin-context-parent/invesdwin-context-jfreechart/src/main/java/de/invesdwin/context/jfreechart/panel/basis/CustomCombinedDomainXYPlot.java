package de.invesdwin.context.jfreechart.panel.basis;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;

import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;
import de.invesdwin.context.jfreechart.plot.XYPlots;

@NotThreadSafe
public class CustomCombinedDomainXYPlot extends CombinedDomainXYPlot {

    public static final Color DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    public static final int INVISIBLE_PLOT_WEIGHT = 0;
    public static final int INITIAL_PLOT_WEIGHT = 1000;
    public static final int MAIN_PLOT_WEIGHT = INITIAL_PLOT_WEIGHT * 2;
    public static final int EMPTY_PLOT_WEIGHT = INITIAL_PLOT_WEIGHT / 5;
    private final InteractiveChartPanel chartPanel;
    private final XYPlot trashPlot;

    public CustomCombinedDomainXYPlot(final InteractiveChartPanel chartPanel) {
        super(chartPanel.getDomainAxis());
        this.chartPanel = chartPanel;
        trashPlot = chartPanel.newPlot(0);
        trashPlot.getRangeAxis().setVisible(false);
        trashPlot.setDomainGridlinesVisible(false);
        trashPlot.setRangeGridlinesVisible(false);
        chartPanel.getPlotLegendHelper().addLegendAnnotation(trashPlot);
        add(trashPlot, INVISIBLE_PLOT_WEIGHT);
    }

    public XYPlot getTrashPlot() {
        return trashPlot;
    }

    public int getSubplotIndex(final int mouseX, final int mouseY) {
        final Point mousePoint = new Point(mouseX, mouseY);
        // convert the Java2D coordinate to axis coordinates...
        final ChartRenderingInfo chartInfo = chartPanel.getChartPanel().getChartRenderingInfo();
        final Point2D java2DPoint = chartPanel.getChartPanel().translateScreenToJava2D(mousePoint);
        final PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();

        // see if the point is in one of the subplots; this is the
        // intersection of the range and domain crosshairs
        final int subplotIndex = plotInfo.getSubplotIndex(java2DPoint);
        return subplotIndex;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<XYPlot> getSubplots() {
        return super.getSubplots();
    }

    public boolean isSubplotVisible(final XYPlot plot) {
        final List<XYPlot> plots = getSubplots();
        for (final XYPlot potentialPlot : plots) {
            if (potentialPlot == plot) {
                return true;
            }
        }
        return false;
    }

    public void removeEmptyPlotsAndResetTrashPlot() {
        final List<XYPlot> subplotsCopy = new ArrayList<>(getSubplots());
        for (int subplotIndex = 0; subplotIndex < subplotsCopy.size(); subplotIndex++) {
            final XYPlot subplot = subplotsCopy.get(subplotIndex);
            if (subplot != trashPlot && !XYPlots.hasDataset(subplot)) {
                remove(subplot);
            }
        }
        if (trashPlot.getWeight() != INVISIBLE_PLOT_WEIGHT) {
            for (int datasetIndex = 0; datasetIndex < trashPlot.getDatasetCount(); datasetIndex++) {
                trashPlot.setDataset(datasetIndex, null);
                trashPlot.setRenderer(datasetIndex, null);
            }
            trashPlot.clearAnnotations();
            chartPanel.getPlotLegendHelper().addLegendAnnotation(trashPlot);
            trashPlot.setBackgroundPaint(DEFAULT_BACKGROUND_COLOR);
            trashPlot.setWeight(INVISIBLE_PLOT_WEIGHT);
        }
    }

    @Deprecated
    @Override
    public void add(final XYPlot subplot) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(final XYPlot subplot, final int weight) {
        if (weight == 0) {
            super.add(subplot, 1);
            subplot.setWeight(0);
        } else {
            super.add(subplot, weight);
        }
    }

}
