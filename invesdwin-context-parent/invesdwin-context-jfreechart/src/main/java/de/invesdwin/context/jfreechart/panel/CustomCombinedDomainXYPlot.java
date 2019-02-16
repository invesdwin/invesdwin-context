package de.invesdwin.context.jfreechart.panel;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;

import de.invesdwin.trading.charts.richclient.swing.chart.panel.ChartPanel;

@NotThreadSafe
public class CustomCombinedDomainXYPlot extends CombinedDomainXYPlot {

    private final ChartPanel chartPanel;

    public CustomCombinedDomainXYPlot(final ChartPanel chartPanel) {
        super(chartPanel.getDomainAxis());
        this.chartPanel = chartPanel;
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

    public void removeEmptyPlots() {
        final List<XYPlot> subplotsCopy = new ArrayList<>(getSubplots());
        for (int subplotIndex = 0; subplotIndex < subplotsCopy.size(); subplotIndex++) {
            final XYPlot subplot = subplotsCopy.get(subplotIndex);
            if (!XYPlots.hasDataset(subplot)) {
                remove(subplot);
            }
        }
    }

}
