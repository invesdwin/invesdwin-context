package de.invesdwin.context.jfreechart.visitor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.Locale;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.title.Title;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;

import de.invesdwin.context.jfreechart.FiniteTickUnitSource;
import de.invesdwin.context.jfreechart.axis.AxisType;
import de.invesdwin.context.jfreechart.axis.attached.IAttachedAxis;
import de.invesdwin.context.jfreechart.plot.IAxisPlot;
import de.invesdwin.context.jfreechart.plot.combined.ICombinedPlot;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.color.Colors;
import de.invesdwin.util.swing.HiDPI;

@Immutable
public class JFreeChartThemeSwing extends AJFreeChartVisitor {

    public static final Color DEFAULT_BACKGROUND_PAINT = (Color) Plot.DEFAULT_BACKGROUND_PAINT;
    public static final Paint DEFAULT_GRIDLINE_PAINT = Colors.fromHex("F2F2F2");
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(HiDPI.scale(1f), BasicStroke.CAP_SQUARE,
            BasicStroke.JOIN_BEVEL);
    public static final Paint DEFAULT_OUTLINE_PAINT = Colors.fromHex("CECECE");
    public static final Stroke DEFAULT_OUTLINE_STROKE = DEFAULT_GRIDLINE_STROKE;
    public static final boolean DEFAULT_OUTLINE_VISIBLE = true;
    public static final RectangleInsets DEFAULT_CHART_PADDING = new RectangleInsets(5, 0, 0, 0);
    public static final int DEFAULT_COMBINED_PLOT_GAP = 0;
    public static final double DEFAULT_TICK_LABEL_INSET_TOP_OR_BOTTOM = 4.0;
    public static final double DEFAULT_TICK_LABEL_INSET_LEFT_OR_RIGHT = 8.0;
    public static final double DEFAULT_TICK_LABEL_INSET_BETWEEN = 2.0;
    public static final RectangleInsets DEFAULT_TICK_LABEL_INSETS_BOTTOM = new RectangleInsets(
            DEFAULT_TICK_LABEL_INSET_BETWEEN, DEFAULT_TICK_LABEL_INSET_BETWEEN, DEFAULT_TICK_LABEL_INSET_TOP_OR_BOTTOM,
            DEFAULT_TICK_LABEL_INSET_BETWEEN);
    public static final RectangleInsets DEFAULT_TICK_LABEL_INSETS_TOP = new RectangleInsets(
            DEFAULT_TICK_LABEL_INSET_TOP_OR_BOTTOM, DEFAULT_TICK_LABEL_INSET_BETWEEN, DEFAULT_TICK_LABEL_INSET_BETWEEN,
            DEFAULT_TICK_LABEL_INSET_BETWEEN);
    public static final RectangleInsets DEFAULT_TICK_LABEL_INSETS_RIGHT = new RectangleInsets(
            DEFAULT_TICK_LABEL_INSET_BETWEEN, DEFAULT_TICK_LABEL_INSET_BETWEEN, DEFAULT_TICK_LABEL_INSET_BETWEEN,
            DEFAULT_TICK_LABEL_INSET_LEFT_OR_RIGHT);
    public static final RectangleInsets DEFAULT_TICK_LABEL_INSETS_LEFT = new RectangleInsets(
            DEFAULT_TICK_LABEL_INSET_BETWEEN, DEFAULT_TICK_LABEL_INSET_LEFT_OR_RIGHT, DEFAULT_TICK_LABEL_INSET_BETWEEN,
            DEFAULT_TICK_LABEL_INSET_BETWEEN);

    @Override
    protected void processChart(final JFreeChart chart) {
        super.processChart(chart);
        chart.setPadding(getChartPadding());
    }

    @Override
    protected void processCombinedPlot(final Set<Integer> duplicateAxisFilter, final ICombinedPlot plot) {
        super.processCombinedPlot(duplicateAxisFilter, plot);
        plot.setGap(getCombinedPlotGap());
    }

    @Override
    public void processTitle(final Title title) {
        super.processTitle(title);
        title.setHorizontalAlignment(HorizontalAlignment.LEFT);
    }

    @Override
    protected void processAxisPlot(final IAxisPlot plot) {
        super.processAxisPlot(plot);
        plot.setBackgroundPaint(getBackgroundPaint());
        plot.setDomainGridlinePaint(getGridlinePaint());
        plot.setDomainGridlineStroke(getGridlineStroke());
        plot.setRangeGridlinePaint(getGridlinePaint());
        plot.setRangeGridlineStroke(getGridlineStroke());
        plot.setOutlinePaint(getOutlinePaint());
        plot.setOutlineStroke(getOutlineStroke());
        plot.setOutlineVisible(isOutlineVisible());
    }

    @Override
    public void processAttachedAxis(final IAttachedAxis axis) {
        super.processAttachedAxis(axis);
        final RectangleEdge axisEdge = axis.getAxisEdge();
        if (axisEdge == RectangleEdge.LEFT) {
            axis.setTickLabelInsets(getTickLabelInsetsLeft());
        } else if (axisEdge == RectangleEdge.RIGHT) {
            axis.setTickLabelInsets(getTickLabelInsetsRight());
        } else if (axisEdge == RectangleEdge.TOP) {
            axis.setTickLabelInsets(getTickLabelInsetsTop());
        } else if (axisEdge == RectangleEdge.BOTTOM) {
            axis.setTickLabelInsets(getTickLabelInsetsBottom());
        } else {
            throw UnknownArgumentException.newInstance(RectangleEdge.class, axisEdge);
        }
    }

    @Override
    public void processAxis(final Axis axis, final AxisType axisType) {
        super.processAxis(axis, axisType);
        axis.setTickMarksVisible(false);
        axis.setAxisLineVisible(false);
        axis.setLabelLocation(AxisLabelLocation.MIDDLE);
        JFreeChartLocaleChanger.changeLocale(axis, getLocale());
        FiniteTickUnitSource.maybeWrap(axis);
    }

    protected Stroke getGridlineStroke() {
        return DEFAULT_GRIDLINE_STROKE;
    }

    protected Paint getGridlinePaint() {
        return DEFAULT_GRIDLINE_PAINT;
    }

    protected Paint getOutlinePaint() {
        return DEFAULT_OUTLINE_PAINT;
    }

    protected Stroke getOutlineStroke() {
        return DEFAULT_GRIDLINE_STROKE;
    }

    protected Paint getBackgroundPaint() {
        return DEFAULT_BACKGROUND_PAINT;
    }

    protected boolean isOutlineVisible() {
        return DEFAULT_OUTLINE_VISIBLE;
    }

    protected Locale getLocale() {
        return JFreeChartLocaleChanger.DEFAULT_LOCALE;
    }

    protected double getCombinedPlotGap() {
        return DEFAULT_COMBINED_PLOT_GAP;
    }

    protected RectangleInsets getChartPadding() {
        return DEFAULT_CHART_PADDING;
    }

    protected RectangleInsets getTickLabelInsetsBottom() {
        return DEFAULT_TICK_LABEL_INSETS_BOTTOM;
    }

    protected RectangleInsets getTickLabelInsetsTop() {
        return DEFAULT_TICK_LABEL_INSETS_TOP;
    }

    protected RectangleInsets getTickLabelInsetsRight() {
        return DEFAULT_TICK_LABEL_INSETS_RIGHT;
    }

    protected RectangleInsets getTickLabelInsetsLeft() {
        return DEFAULT_TICK_LABEL_INSETS_LEFT;
    }

}
