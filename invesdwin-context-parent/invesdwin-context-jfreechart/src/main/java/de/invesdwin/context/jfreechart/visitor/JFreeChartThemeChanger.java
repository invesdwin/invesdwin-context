package de.invesdwin.context.jfreechart.visitor;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.util.Locale;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;

import de.invesdwin.context.jfreechart.FiniteTickUnitSource;
import de.invesdwin.util.lang.color.Colors;

@Immutable
public class JFreeChartThemeChanger extends AJFreeChartVisitor {

    public static final Paint DEFAULT_PAINT_BACKGROUND = Plot.DEFAULT_BACKGROUND_PAINT;
    public static final Paint DEFAULT_PAINT_GRIDLINE = Colors.fromHex("F2F2F2");
    public static final BasicStroke DEFAULT_STROKE_GRIDLINE = new BasicStroke(2f, BasicStroke.CAP_SQUARE,
            BasicStroke.JOIN_BEVEL);

    @Override
    protected void processXYPlot(final Set<Integer> duplicateAxisFilter, final XYPlot cPlot) {
        super.processXYPlot(duplicateAxisFilter, cPlot);
        cPlot.setBackgroundPaint(getBackgroundPaint());
        cPlot.setDomainGridlinePaint(getGridlinePaint());
        cPlot.setDomainGridlineStroke(getGridlineStroke());
        cPlot.setRangeGridlinePaint(getGridlinePaint());
        cPlot.setRangeGridlineStroke(getGridlineStroke());
        cPlot.setOutlineVisible(false);
    }

    @Override
    public void processDomainAxis(final Axis axis) {
        super.processDomainAxis(axis);
        axis.setLabelLocation(AxisLabelLocation.LOW_END);
    }

    @Override
    public void processRangeAxis(final Axis axis) {
        super.processRangeAxis(axis);
        axis.setLabelLocation(AxisLabelLocation.HIGH_END);
    }

    @Override
    public void processAxis(final Axis axis) {
        super.processAxis(axis);
        axis.setTickMarksVisible(false);
        axis.setAxisLineVisible(false);
        JFreeChartLocaleChanger.changeLocale(axis, getLocale());
        FiniteTickUnitSource.maybeWrap(axis);
    }

    protected BasicStroke getGridlineStroke() {
        return DEFAULT_STROKE_GRIDLINE;
    }

    protected Paint getGridlinePaint() {
        return DEFAULT_PAINT_GRIDLINE;
    }

    protected Paint getBackgroundPaint() {
        return DEFAULT_PAINT_BACKGROUND;
    }

    protected Locale getLocale() {
        return JFreeChartLocaleChanger.DEFAULT_LOCALE;
    }

}
