package de.invesdwin.context.jfreechart.axis;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.util.EventListener;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.Tick;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;

import de.invesdwin.context.jfreechart.plot.IAxisPlot;

@NotThreadSafe
public abstract class AAxis implements IAxis {

    private final IAxisPlot plot;
    private final Axis axis;

    public AAxis(final IAxisPlot plot, final Axis axis) {
        this.plot = plot;
        this.axis = axis;
    }

    @Override
    public IAxisPlot getPlot() {
        return plot;
    }

    @Override
    public Axis getAxis() {
        return axis;
    }

    @Override
    public final boolean isVisible() {
        return axis.isVisible();
    }

    @Override
    public final void setVisible(final boolean flag) {
        axis.setVisible(flag);
    }

    @Override
    public final String getLabel() {
        return axis.getLabel();
    }

    @Override
    public final void setLabel(final String label) {
        axis.setLabel(label);
    }

    @Override
    public final AttributedString getAttributedLabel() {
        return axis.getAttributedLabel();
    }

    @Override
    public final void setAttributedLabel(final String label) {
        axis.setAttributedLabel(label);
    }

    @Override
    public final void setAttributedLabel(final AttributedString label) {
        axis.setAttributedLabel(label);
    }

    @Override
    public final AttributedString createAttributedLabel(final String label) {
        return axis.createAttributedLabel(label);
    }

    @Override
    public final Font getLabelFont() {
        return axis.getLabelFont();
    }

    @Override
    public final void setLabelFont(final Font font) {
        axis.setLabelFont(font);
    }

    @Override
    public final Paint getLabelPaint() {
        return axis.getLabelPaint();
    }

    @Override
    public final void setLabelPaint(final Paint paint) {
        axis.setLabelPaint(paint);
    }

    @Override
    public final RectangleInsets getLabelInsets() {
        return axis.getLabelInsets();
    }

    @Override
    public final void setLabelInsets(final RectangleInsets insets) {
        axis.setLabelInsets(insets);
    }

    @Override
    public final void setLabelInsets(final RectangleInsets insets, final boolean notify) {
        axis.setLabelInsets(insets);
    }

    @Override
    public final double getLabelAngle() {
        return axis.getLabelAngle();
    }

    @Override
    public final void setLabelAngle(final double angle) {
        axis.setLabelAngle(angle);
    }

    @Override
    public final AxisLabelLocation getLabelLocation() {
        return axis.getLabelLocation();
    }

    @Override
    public final void setLabelLocation(final AxisLabelLocation location) {
        axis.setLabelLocation(location);
    }

    @Override
    public final boolean isAxisLineVisible() {
        return axis.isAxisLineVisible();
    }

    @Override
    public final void setAxisLineVisible(final boolean visible) {
        axis.setAxisLineVisible(visible);
    }

    @Override
    public final Paint getAxisLinePaint() {
        return axis.getAxisLinePaint();
    }

    @Override
    public final void setAxisLinePaint(final Paint paint) {
        axis.setAxisLinePaint(paint);
    }

    @Override
    public final Stroke getAxisLineStroke() {
        return axis.getAxisLineStroke();
    }

    @Override
    public final void setAxisLineStroke(final Stroke stroke) {
        axis.setAxisLineStroke(stroke);
    }

    @Override
    public final boolean isTickLabelsVisible() {
        return axis.isTickLabelsVisible();
    }

    @Override
    public final void setTickLabelsVisible(final boolean flag) {
        axis.setTickLabelsVisible(flag);
    }

    @Override
    public final boolean isMinorTickMarksVisible() {
        return axis.isMinorTickMarksVisible();
    }

    @Override
    public final void setMinorTickMarksVisible(final boolean flag) {
        axis.setMinorTickMarksVisible(flag);
    }

    @Override
    public final Font getTickLabelFont() {
        return axis.getTickLabelFont();
    }

    @Override
    public final void setTickLabelFont(final Font font) {
        axis.setTickLabelFont(font);
    }

    @Override
    public final Paint getTickLabelPaint() {
        return axis.getTickLabelPaint();
    }

    @Override
    public final void setTickLabelPaint(final Paint paint) {
        axis.setTickLabelPaint(paint);
    }

    @Override
    public final RectangleInsets getTickLabelInsets() {
        return axis.getTickLabelInsets();
    }

    @Override
    public final void setTickLabelInsets(final RectangleInsets insets) {
        axis.setTickLabelInsets(insets);
    }

    @Override
    public final boolean isTickMarksVisible() {
        return axis.isTickMarksVisible();
    }

    @Override
    public final void setTickMarksVisible(final boolean flag) {
        axis.setTickMarksVisible(flag);
    }

    @Override
    public final float getTickMarkInsideLength() {
        return axis.getTickMarkInsideLength();
    }

    @Override
    public final void setTickMarkInsideLength(final float length) {
        axis.setTickMarkInsideLength(length);
    }

    @Override
    public final float getTickMarkOutsideLength() {
        return axis.getTickMarkOutsideLength();
    }

    @Override
    public final void setTickMarkOutsideLength(final float length) {
        axis.setTickMarkOutsideLength(length);
    }

    @Override
    public final Stroke getTickMarkStroke() {
        return axis.getTickMarkStroke();
    }

    @Override
    public final void setTickMarkStroke(final Stroke stroke) {
        axis.setTickMarkStroke(stroke);
    }

    @Override
    public final Paint getTickMarkPaint() {
        return axis.getTickMarkPaint();
    }

    @Override
    public final void setTickMarkPaint(final Paint paint) {
        axis.setTickMarkPaint(paint);
    }

    @Override
    public final float getMinorTickMarkInsideLength() {
        return axis.getMinorTickMarkInsideLength();
    }

    @Override
    public final void setMinorTickMarkInsideLength(final float length) {
        axis.setMinorTickMarkInsideLength(length);
    }

    @Override
    public final float getMinorTickMarkOutsideLength() {
        return axis.getMinorTickMarkOutsideLength();
    }

    @Override
    public final void setMinorTickMarkOutsideLength(final float length) {
        axis.setMinorTickMarkOutsideLength(length);
    }

    @Override
    public final double getFixedDimension() {
        return axis.getFixedDimension();
    }

    @Override
    public final void setFixedDimension(final double dimension) {
        axis.setFixedDimension(dimension);
    }

    @Override
    public final void configure() {
        axis.configure();
    }

    @Override
    public final AxisSpace reserveSpace(final Graphics2D g2, final Plot plot, final Rectangle2D plotArea,
            final RectangleEdge edge, final AxisSpace space) {
        return axis.reserveSpace(g2, plot, plotArea, edge, space);
    }

    @Override
    public final AxisState draw(final Graphics2D g2, final double cursor, final Rectangle2D plotArea,
            final Rectangle2D dataArea, final RectangleEdge edge, final PlotRenderingInfo plotState) {
        return axis.draw(g2, cursor, plotArea, dataArea, edge, plotState);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final List<Tick> refreshTicks(final Graphics2D g2, final AxisState state, final Rectangle2D dataArea,
            final RectangleEdge edge) {
        return axis.refreshTicks(g2, state, dataArea, edge);
    }

    @Override
    public final void addChangeListener(final AxisChangeListener listener) {
        axis.addChangeListener(listener);
    }

    @Override
    public final void removeChangeListener(final AxisChangeListener listener) {
        axis.removeChangeListener(listener);
    }

    @Override
    public final boolean hasListener(final EventListener listener) {
        return axis.hasListener(listener);
    }

}
