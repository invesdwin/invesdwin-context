package de.invesdwin.context.jfreechart.visitor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.Locale;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.axis.Axis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;

import de.invesdwin.context.jfreechart.FiniteTickUnitSource;
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

    @Override
    protected void processXYPlot(final Set<Integer> duplicateAxisFilter, final XYPlot cPlot) {
        super.processXYPlot(duplicateAxisFilter, cPlot);
        cPlot.setBackgroundPaint(getBackgroundPaint());
        cPlot.setDomainGridlinePaint(getGridlinePaint());
        cPlot.setDomainGridlineStroke(getGridlineStroke());
        cPlot.setRangeGridlinePaint(getGridlinePaint());
        cPlot.setRangeGridlineStroke(getGridlineStroke());
        cPlot.setOutlinePaint(getOutlinePaint());
        cPlot.setOutlineStroke(getOutlineStroke());
        cPlot.setOutlineVisible(isOutlineVisible());
    }

    @Override
    public void processAxis(final Axis axis) {
        super.processAxis(axis);
        axis.setTickMarksVisible(false);
        axis.setAxisLineVisible(false);
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

}
