package de.invesdwin.context.jfreechart.panel.helper;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
import de.invesdwin.context.jfreechart.plot.annotation.priceline.XYPriceLineAnnotation;
import de.invesdwin.util.lang.Colors;

@NotThreadSafe
public class PlotCrosshairHelper {
    private static final Font CROSSHAIR_FONT = XYPriceLineAnnotation.FONT;
    private static final Cursor CROSSHAIR_CURSOR = new Cursor(Cursor.CROSSHAIR_CURSOR);
    private static final Color CROSSHAIR_COLOR = Color.BLACK;
    private static final Stroke CROSSHAIR_STROKE = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
            0, new float[] { 5, 6 }, 0);

    private final InteractiveChartPanel chartPanel;
    private final ValueMarker domainCrosshairMarker;
    private final ValueMarker lastDomainCrosshairMarker;
    private final ValueMarker rangeCrosshairMarkerRight;
    private final ValueMarker rangeCrosshairMarkerLeft;
    private int crosshairLastMouseX;
    private int crosshairLastMouseY;

    public PlotCrosshairHelper(final InteractiveChartPanel chartPanel) {
        this.chartPanel = chartPanel;

        domainCrosshairMarker = new ValueMarker(0D);
        domainCrosshairMarker.setStroke(CROSSHAIR_STROKE);
        domainCrosshairMarker.setPaint(CROSSHAIR_COLOR);
        domainCrosshairMarker.setLabelFont(CROSSHAIR_FONT);
        domainCrosshairMarker.setLabelPaint(CROSSHAIR_COLOR);
        domainCrosshairMarker.setLabelBackgroundColor(Colors.INVISIBLE_COLOR);
        domainCrosshairMarker.setLabelAnchor(RectangleAnchor.BOTTOM);
        domainCrosshairMarker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
        domainCrosshairMarker.setLabelOffset(new RectangleInsets(0, 4, 2, 0));
        try {
            lastDomainCrosshairMarker = (ValueMarker) domainCrosshairMarker.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        rangeCrosshairMarkerRight = new ValueMarker(0D);
        rangeCrosshairMarkerRight.setStroke(CROSSHAIR_STROKE);
        rangeCrosshairMarkerRight.setPaint(CROSSHAIR_COLOR);
        rangeCrosshairMarkerRight.setLabelFont(CROSSHAIR_FONT);
        rangeCrosshairMarkerRight.setLabelPaint(CROSSHAIR_COLOR);
        rangeCrosshairMarkerRight.setLabelBackgroundColor(Colors.INVISIBLE_COLOR);
        rangeCrosshairMarkerRight.setLabelAnchor(RectangleAnchor.RIGHT);
        rangeCrosshairMarkerRight.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        rangeCrosshairMarkerRight.setLabelOffset(new RectangleInsets(0, 0, 1, 1));
        rangeCrosshairMarkerLeft = new ValueMarker(0D);
        rangeCrosshairMarkerLeft.setStroke(CROSSHAIR_STROKE);
        rangeCrosshairMarkerLeft.setPaint(Colors.INVISIBLE_COLOR);
        rangeCrosshairMarkerLeft.setLabelFont(CROSSHAIR_FONT);
        rangeCrosshairMarkerLeft.setLabelPaint(CROSSHAIR_COLOR);
        rangeCrosshairMarkerLeft.setLabelBackgroundColor(Colors.INVISIBLE_COLOR);
        rangeCrosshairMarkerLeft.setLabelAnchor(RectangleAnchor.LEFT);
        rangeCrosshairMarkerLeft.setLabelTextAnchor(TextAnchor.TOP_LEFT);
        rangeCrosshairMarkerLeft.setLabelOffset(new RectangleInsets(0, 2, 1, 0));
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
                    final NumberAxis rangeAxisRight = (NumberAxis) plot.getRangeAxis();
                    final double yyRight = rangeAxisRight.java2DToValue(mousePoint.getY(), panelArea,
                            plot.getRangeAxisEdge());
                    rangeCrosshairMarkerRight.setValue(yyRight);
                    if (rangeAxisRight.isTickLabelsVisible()) {
                        rangeCrosshairMarkerRight.setLabel(rangeAxisRight.getNumberFormatOverride().format(yyRight));
                        final NumberAxis rangeAxisLeft = (NumberAxis) plot.getRangeAxis(1);
                        if (rangeAxisLeft != null) {
                            rangeCrosshairMarkerLeft.setValue(yyRight);
                            final double yyLeft = rangeAxisLeft.java2DToValue(mousePoint.getY(), panelArea,
                                    plot.getRangeAxisEdge(1));
                            rangeCrosshairMarkerLeft.setLabel(rangeAxisLeft.getNumberFormatOverride().format(yyLeft));
                        } else {
                            rangeCrosshairMarkerLeft.setValue(-1);
                            rangeCrosshairMarkerLeft.setLabel(null);
                        }
                    } else {
                        rangeCrosshairMarkerRight.setLabel(null);
                        rangeCrosshairMarkerRight.setValue(-1D);
                        rangeCrosshairMarkerLeft.setLabel(null);
                        rangeCrosshairMarkerLeft.setValue(-1D);
                    }
                    if (!plot.isRangeCrosshairLockedOnData()) {
                        plot.addRangeMarker(rangeCrosshairMarkerRight);
                        if (rangeCrosshairMarkerLeft.getLabel() != null) {
                            plot.addRangeMarker(rangeCrosshairMarkerLeft);
                        }
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

        rangeCrosshairMarkerRight.setValue(-1D);
        rangeCrosshairMarkerLeft.setValue(-1D);
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
        subplot.removeRangeMarker(rangeCrosshairMarkerRight);
        subplot.removeRangeMarker(rangeCrosshairMarkerLeft);
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
