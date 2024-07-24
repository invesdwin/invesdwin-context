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

    public static final Paint DEFAULT_BACKGROUND_PAINT = Plot.DEFAULT_BACKGROUND_PAINT;
    public static final Paint DEFAULT_GRIDLINE_PAINT = Colors.fromHex("ECECEC");
    public static final BasicStroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(2f, BasicStroke.CAP_SQUARE,
            BasicStroke.JOIN_BEVEL);

    @Override
    protected void processXYPlot(final Set<Integer> duplicateAxisFilter, final XYPlot cPlot) {
        super.processXYPlot(duplicateAxisFilter, cPlot);
        cPlot.setBackgroundPaint(getBackgroundPaint());
        cPlot.setDomainGridlinePaint(getGridlinePaint());
        cPlot.setDomainGridlineStroke(getGridlineStroke());
        cPlot.setRangeGridlinePaint(getGridlinePaint());
        cPlot.setRangeGridlineStroke(getGridlineStroke());
        final Paint outlinePaint = getOutlinePaint();
        if (outlinePaint != null) {
            cPlot.setOutlinePaint(outlinePaint);
        } else {
            cPlot.setOutlineVisible(false);
        }
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
        return DEFAULT_GRIDLINE_STROKE;
    }

    protected Paint getGridlinePaint() {
        return DEFAULT_GRIDLINE_PAINT;
    }

    protected Paint getOutlinePaint() {
        return getGridlinePaint();
    }

    protected Paint getBackgroundPaint() {
        return DEFAULT_BACKGROUND_PAINT;
    }

    protected Locale getLocale() {
        return JFreeChartLocaleChanger.DEFAULT_LOCALE;
    }

}
