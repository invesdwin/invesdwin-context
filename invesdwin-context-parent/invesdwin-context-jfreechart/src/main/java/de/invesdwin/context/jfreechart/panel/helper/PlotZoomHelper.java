package de.invesdwin.context.jfreechart.panel.helper;

import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;

import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;

@NotThreadSafe
public class PlotZoomHelper {

    private static final int MAX_ZOOM_ITEM_COUNT = 10000;
    private static final int MIN_ZOOM_ITEM_COUNT = 10;

    private static final double ZOOM_FACTOR = 0.1D;
    private static final double ZOOM_OUT_FACTOR = 1D + ZOOM_FACTOR;
    private static final double ZOOM_IN_FACTOR = 1 / ZOOM_OUT_FACTOR;

    private final InteractiveChartPanel chartPanel;

    public PlotZoomHelper(final InteractiveChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }

    public void mouseWheelMoved(final MouseWheelEvent e) {
        final double zoomFactor;
        final int clicks = e.getWheelRotation();
        if (clicks < 0) {
            zoomFactor = ZOOM_IN_FACTOR;
        } else {
            zoomFactor = ZOOM_OUT_FACTOR;
        }
        final Point2D point = this.chartPanel.getChartPanel().translateScreenToJava2D(e.getPoint());
        handleZoomable(point, zoomFactor);
    }

    private void handleZoomable(final Point2D point, final double zoomFactor) {
        final XYPlot plot = (XYPlot) this.chartPanel.getChart().getPlot();

        // don't zoom unless the mouse pointer is in the plot's data area
        final ChartRenderingInfo info = this.chartPanel.getChartPanel().getChartRenderingInfo();
        final PlotRenderingInfo pinfo = info.getPlotInfo();
        if (!pinfo.getDataArea().contains(point)) {
            return;
        }

        // do not notify while zooming each axis
        final boolean notifyState = plot.isNotify();
        plot.setNotify(false);

        plot.zoomDomainAxes(zoomFactor, pinfo, point, true);
        plot.setNotify(notifyState); // this generates the change event too
        chartPanel.update();
    }

    public void zoomOut() {
        final ChartRenderingInfo info = this.chartPanel.getChartPanel().getChartRenderingInfo();
        final PlotRenderingInfo pinfo = info.getPlotInfo();
        handleZoomable(new Point2D.Double(pinfo.getDataArea().getMaxX() - 1, pinfo.getDataArea().getCenterY()),
                ZOOM_OUT_FACTOR);
    }

    public void zoomIn() {
        final ChartRenderingInfo info = this.chartPanel.getChartPanel().getChartRenderingInfo();
        final PlotRenderingInfo pinfo = info.getPlotInfo();
        handleZoomable(new Point2D.Double(pinfo.getDataArea().getMaxX() - 1, pinfo.getDataArea().getCenterY()),
                ZOOM_IN_FACTOR);
    }

    public void limitRange() {
        Range range = chartPanel.getDomainAxis().getRange();
        boolean rangeChanged = false;
        final double minLowerBound = 0D - chartPanel.getAllowedRangeGap();
        if (range.getLowerBound() < minLowerBound) {
            range = new Range(minLowerBound, range.getUpperBound());
            rangeChanged = true;
        }
        final int maxUpperBound = chartPanel.getDataset().getItemCount(0) + chartPanel.getAllowedRangeGap();
        if (range.getUpperBound() > maxUpperBound) {
            range = new Range(range.getLowerBound(), maxUpperBound);
            rangeChanged = true;
        }
        final int itemCount = (int) range.getLength();
        if (itemCount <= MIN_ZOOM_ITEM_COUNT) {
            final int gap = MIN_ZOOM_ITEM_COUNT / 2;
            range = new Range(range.getCentralValue() - gap, range.getCentralValue() + gap);
            if (range.getUpperBound() > maxUpperBound) {
                range = new Range(maxUpperBound - MIN_ZOOM_ITEM_COUNT, maxUpperBound);
            }
            if (range.getLowerBound() < minLowerBound) {
                range = new Range(minLowerBound, MIN_ZOOM_ITEM_COUNT);
            }
            rangeChanged = true;
        }
        if (itemCount >= MAX_ZOOM_ITEM_COUNT) {
            final int gap = MAX_ZOOM_ITEM_COUNT / 2;
            range = new Range(range.getCentralValue() - gap, range.getCentralValue() + gap);
            if (range.getUpperBound() > maxUpperBound) {
                range = new Range(maxUpperBound - MAX_ZOOM_ITEM_COUNT, maxUpperBound);
            }
            if (range.getLowerBound() < minLowerBound) {
                range = new Range(minLowerBound, MAX_ZOOM_ITEM_COUNT);
            }
            rangeChanged = true;
        }
        if (rangeChanged) {
            chartPanel.getDomainAxis().setRange(range);
        }
    }

}
