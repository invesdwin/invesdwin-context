package de.invesdwin.context.jfreechart.panel.helper;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;

import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;

@NotThreadSafe
public class PlotCrosshairHelper {

    private static final Cursor CROSSHAIR_CURSOR = new Cursor(Cursor.CROSSHAIR_CURSOR);
    private static final Color CROSSHAIR_COLOR = Color.BLACK;
    private static final Stroke CROSSHAIR_STROKE = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
            0, new float[] { 5, 6 }, 0);

    private final InteractiveChartPanel chartPanel;
    private final ValueMarker domainCrosshairMarker;
    private final ValueMarker lastDomainCrosshairMarker;
    private final ValueMarker rangeCrosshairMarker;
    private int crosshairLastMouseX;
    private int crosshairLastMouseY;

    public PlotCrosshairHelper(final InteractiveChartPanel chartPanel) {
        this.chartPanel = chartPanel;

        domainCrosshairMarker = new ValueMarker(0D);
        domainCrosshairMarker.setStroke(CROSSHAIR_STROKE);
        domainCrosshairMarker.setPaint(CROSSHAIR_COLOR);
        domainCrosshairMarker.setLabelPaint(CROSSHAIR_COLOR);
        domainCrosshairMarker.setLabelAnchor(RectangleAnchor.BOTTOM);
        domainCrosshairMarker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
        domainCrosshairMarker.setLabelOffset(new RectangleInsets(0, 4, 2, 0));
        try {
            lastDomainCrosshairMarker = (ValueMarker) domainCrosshairMarker.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        rangeCrosshairMarker = new ValueMarker(0D);
        rangeCrosshairMarker.setStroke(CROSSHAIR_STROKE);
        rangeCrosshairMarker.setPaint(CROSSHAIR_COLOR);
        rangeCrosshairMarker.setLabelPaint(CROSSHAIR_COLOR);
        rangeCrosshairMarker.setLabelAnchor(RectangleAnchor.RIGHT);
        rangeCrosshairMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        rangeCrosshairMarker.setLabelOffset(new RectangleInsets(0, 0, 1, 1));
    }

    public ValueMarker getDomainCrosshairMarker() {
        return domainCrosshairMarker;
    }

    public void updateCrosshair(final int mouseX, final int mouseY) {
        final Point mousePoint = new Point(mouseX, mouseY);

        // convert the Java2D coordinate to axis coordinates...
        final ChartRenderingInfo chartInfo = chartPanel.getChartPanel().getChartRenderingInfo();
        final Point2D java2DPoint = chartPanel.getChartPanel().translateScreenToJava2D(mousePoint);
        final PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();

        // see if the point is in one of the subplots; this is the
        // intersection of the range and domain crosshairs
        final int subplotIndex = plotInfo.getSubplotIndex(java2DPoint);

        if (subplotIndex >= 0) {
            // all subplots have the domain crosshair
            // the x coordinate is the same for all subplots
            final Rectangle2D dataArea = plotInfo.getDataArea();
            final int xx = (int) chartPanel.getCombinedPlot()
                    .getDomainAxis()
                    .java2DToValue(java2DPoint.getX(), dataArea, chartPanel.getCombinedPlot().getDomainAxisEdge());

            final Rectangle2D panelArea = chartPanel.getChartPanel().getScreenDataArea(mouseX, mouseY);

            domainCrosshairMarker.setValue(xx);
            lastDomainCrosshairMarker.setValue(xx);
            lastDomainCrosshairMarker.setLabel(chartPanel.getDomainAxis().getNumberFormatOverride().format(xx));
            final List<XYPlot> plots = chartPanel.getCombinedPlot().getSubplots();
            for (int i = 0; i < plots.size(); i++) {
                final XYPlot plot = plots.get(i);
                // set domain crosshair for each plot

                if (!plot.isDomainCrosshairLockedOnData()) {
                    if (i == plots.size() - 1) {
                        plot.addDomainMarker(lastDomainCrosshairMarker);
                    } else {
                        plot.addDomainMarker(domainCrosshairMarker);
                    }
                    plot.setDomainCrosshairLockedOnData(true); //our marker for enabled crosshair
                }
                if (subplotIndex == i) {
                    final double yy = plot.getRangeAxis()
                            .java2DToValue(mousePoint.getY(), panelArea, plot.getRangeAxisEdge());
                    rangeCrosshairMarker.setValue(yy);
                    final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
                    if (rangeAxis.isTickLabelsVisible()) {
                        final NumberFormat rangeAxisFormat = rangeAxis.getNumberFormatOverride();
                        rangeCrosshairMarker.setLabel(rangeAxisFormat.format(yy));
                    } else {
                        rangeCrosshairMarker.setLabel(null);
                    }
                    if (!plot.isRangeCrosshairLockedOnData()) {
                        plot.addRangeMarker(rangeCrosshairMarker);
                        plot.setRangeCrosshairLockedOnData(true); //our marker for enabled crosshair
                    }
                    chartPanel.setCursor(CROSSHAIR_CURSOR);
                } else {
                    // this subplot does not have the range
                    // crosshair, make sure its off
                    disableRangeCrosshair(plot);
                }
                crosshairLastMouseX = mouseX;
                crosshairLastMouseY = mouseY;
            }
        } else {
            disableCrosshair();
            crosshairLastMouseX = -1;
            crosshairLastMouseY = -1;
        }
    }

    public void disableCrosshair() {
        final List<XYPlot> subplotsList = chartPanel.getCombinedPlot().getSubplots();
        for (int i = 0; i < subplotsList.size(); i++) {
            final XYPlot subplot = subplotsList.get(i);
            disableCrosshair(subplot);
        }

        rangeCrosshairMarker.setValue(-1D);
        domainCrosshairMarker.setValue(-1D);
        lastDomainCrosshairMarker.setValue(-1D);
    }

    private void disableCrosshair(final XYPlot subplot) {
        disableRangeCrosshair(subplot);
        subplot.setDomainCrosshairLockedOnData(false);
        subplot.removeDomainMarker(domainCrosshairMarker);
        subplot.removeDomainMarker(lastDomainCrosshairMarker);
    }

    private void disableRangeCrosshair(final XYPlot subplot) {
        subplot.setRangeCrosshairLockedOnData(false);
        subplot.removeRangeMarker(rangeCrosshairMarker);
    }

    public void datasetChanged() {
        if (crosshairLastMouseX >= 0 && crosshairLastMouseY >= 0) {
            updateCrosshair(crosshairLastMouseX, crosshairLastMouseY);
        }
    }

    public void mouseMoved(final MouseEvent e) {
        final int mouseX = e.getX();
        final int mouseY = e.getY();
        updateCrosshair(mouseX, mouseY);
    }

}
