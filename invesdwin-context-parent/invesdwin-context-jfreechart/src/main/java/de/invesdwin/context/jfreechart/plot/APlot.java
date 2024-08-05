package de.invesdwin.context.jfreechart.plot;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.event.AnnotationChangeEvent;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.MarkerChangeEvent;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetGroup;

@NotThreadSafe
public abstract class APlot implements IPlot {

    private final Plot plot;

    public APlot(final Plot plot) {
        this.plot = plot;
    }

    @Override
    public Plot getPlot() {
        return plot;
    }

    @Override
    public final JFreeChart getChart() {
        return plot.getChart();
    }

    @Override
    public final void setChart(final JFreeChart chart) {
        plot.setChart(chart);
    }

    @Override
    public final boolean fetchElementHintingFlag() {
        return plot.fetchElementHintingFlag();
    }

    @Override
    public final DatasetGroup getDatasetGroup() {
        return plot.getDatasetGroup();
    }

    @Override
    public final String getNoDataMessage() {
        return plot.getNoDataMessage();
    }

    @Override
    public final void setNoDataMessage(final String message) {
        plot.setNoDataMessage(message);
    }

    @Override
    public final Font getNoDataMessageFont() {
        return plot.getNoDataMessageFont();
    }

    @Override
    public final void setNoDataMessageFont(final Font font) {
        plot.setNoDataMessageFont(font);
    }

    @Override
    public final Paint getNoDataMessagePaint() {
        return plot.getNoDataMessagePaint();
    }

    @Override
    public final void setNoDataMessagePaint(final Paint paint) {
        plot.setNoDataMessagePaint(paint);
    }

    @Override
    public final String getPlotType() {
        return plot.getPlotType();
    }

    @Override
    public final Plot getParent() {
        return plot.getParent();
    }

    @Override
    public final void setParent(final Plot parent) {
        plot.setParent(parent);
    }

    @Override
    public final Plot getRootPlot() {
        return plot.getRootPlot();
    }

    @Override
    public final boolean isSubplot() {
        return plot.isSubplot();
    }

    @Override
    public final RectangleInsets getInsets() {
        return plot.getInsets();
    }

    @Override
    public final void setInsets(final RectangleInsets insets) {
        plot.setInsets(insets);
    }

    @Override
    public final void setInsets(final RectangleInsets insets, final boolean notify) {
        plot.setInsets(insets, notify);
    }

    @Override
    public final Paint getBackgroundPaint() {
        return plot.getBackgroundPaint();
    }

    @Override
    public final void setBackgroundPaint(final Paint paint) {
        plot.setBackgroundPaint(paint);
    }

    @Override
    public final float getBackgroundAlpha() {
        return plot.getBackgroundAlpha();
    }

    @Override
    public final void setBackgroundAlpha(final float alpha) {
        plot.setBackgroundAlpha(alpha);
    }

    @Override
    public final DrawingSupplier getDrawingSupplier() {
        return plot.getDrawingSupplier();
    }

    @Override
    public final void setDrawingSupplier(final DrawingSupplier supplier) {
        plot.setDrawingSupplier(supplier);
    }

    @Override
    public final void setDrawingSupplier(final DrawingSupplier supplier, final boolean notify) {
        plot.setDrawingSupplier(supplier, notify);
    }

    @Override
    public final Image getBackgroundImage() {
        return plot.getBackgroundImage();
    }

    @Override
    public final void setBackgroundImage(final Image image) {
        plot.setBackgroundImage(image);
    }

    @Override
    public final int getBackgroundImageAlignment() {
        return plot.getBackgroundImageAlignment();
    }

    @Override
    public final void setBackgroundImageAlignment(final int alignment) {
        plot.setBackgroundImageAlignment(alignment);
    }

    @Override
    public final float getBackgroundImageAlpha() {
        return plot.getBackgroundImageAlpha();
    }

    @Override
    public final void setBackgroundImageAlpha(final float alpha) {
        plot.setBackgroundImageAlpha(alpha);
    }

    @Override
    public final boolean isOutlineVisible() {
        return plot.isOutlineVisible();
    }

    @Override
    public final void setOutlineVisible(final boolean visible) {
        plot.setOutlineVisible(visible);
    }

    @Override
    public final Stroke getOutlineStroke() {
        return plot.getOutlineStroke();
    }

    @Override
    public final void setOutlineStroke(final Stroke stroke) {
        plot.setOutlineStroke(stroke);
    }

    @Override
    public final Paint getOutlinePaint() {
        return plot.getOutlinePaint();
    }

    @Override
    public final void setOutlinePaint(final Paint paint) {
        plot.setOutlinePaint(paint);
    }

    @Override
    public final float getForegroundAlpha() {
        return plot.getForegroundAlpha();
    }

    @Override
    public final void setForegroundAlpha(final float alpha) {
        plot.setForegroundAlpha(alpha);
    }

    @Override
    public final LegendItemCollection getLegendItems() {
        return plot.getLegendItems();
    }

    @Override
    public final boolean isNotify() {
        return plot.isNotify();
    }

    @Override
    public final void setNotify(final boolean notify) {
        plot.setNotify(notify);
    }

    @Override
    public final void addChangeListener(final PlotChangeListener listener) {
        plot.addChangeListener(listener);
    }

    @Override
    public final void removeChangeListener(final PlotChangeListener listener) {
        plot.removeChangeListener(listener);
    }

    @Override
    public final void notifyListeners(final PlotChangeEvent event) {
        plot.notifyListeners(event);
    }

    @Override
    public final void draw(final Graphics2D g2, final Rectangle2D area, final Point2D anchor,
            final PlotState parentState, final PlotRenderingInfo info) {
        plot.draw(g2, area, anchor, parentState, info);
    }

    @Override
    public final void drawBackground(final Graphics2D g2, final Rectangle2D area) {
        plot.drawBackground(g2, area);
    }

    @Override
    public final void drawBackgroundImage(final Graphics2D g2, final Rectangle2D area) {
        plot.drawBackgroundImage(g2, area);
    }

    @Override
    public final void drawOutline(final Graphics2D g2, final Rectangle2D area) {
        plot.drawOutline(g2, area);
    }

    @Override
    public final void handleClick(final int x, final int y, final PlotRenderingInfo info) {
        plot.handleClick(x, y, info);
    }

    @Override
    public final void zoom(final double percent) {
        plot.zoom(percent);
    }

    @Override
    public final void annotationChanged(final AnnotationChangeEvent event) {
        plot.annotationChanged(event);
    }

    @Override
    public final void axisChanged(final AxisChangeEvent event) {
        plot.axisChanged(event);
    }

    @Override
    public final void datasetChanged(final DatasetChangeEvent event) {
        plot.datasetChanged(event);
    }

    @Override
    public final void markerChanged(final MarkerChangeEvent event) {
        plot.markerChanged(event);
    }

}
