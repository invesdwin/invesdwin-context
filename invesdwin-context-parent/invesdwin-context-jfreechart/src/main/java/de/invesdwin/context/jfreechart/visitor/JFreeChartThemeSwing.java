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
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.title.Title;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;

import de.invesdwin.context.jfreechart.FiniteTickUnitSource;
import de.invesdwin.context.jfreechart.axis.AxisType;
import de.invesdwin.context.jfreechart.plot.IAxisPlot;
import de.invesdwin.context.jfreechart.plot.combined.ICombinedPlot;
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

    @Override
    protected void processChart(final JFreeChart chart) {
        super.processChart(chart);
        //        chart.setBackgroundPaint(DEFAULT_BACKGROUND_PAINT);
        //TODO: gaps
        //        chart.setPadding(DEFAULT_CHART_PADDING);
    }

    @Override
    protected void processCombinedPlot(final Set<Integer> duplicateAxisFilter, final ICombinedPlot plot) {
        super.processCombinedPlot(duplicateAxisFilter, plot);
        //TODO: gaps
        //        plot.setGap(getCombinedPlotGap());
    }

    @Override
    public void processTitle(final Title title) {
        super.processTitle(title);
        title.setHorizontalAlignment(HorizontalAlignment.LEFT);

        final RectangleInsets padding = title.getPadding();
        if (title.getPosition() == RectangleEdge.TOP) {
            title.setPadding(padding.getTop(), padding.getLeft(), padding.getBottom() + 15, padding.getRight());
        } else if (title.getPosition() == RectangleEdge.BOTTOM) {
            title.setPadding(padding.getTop() + 10, padding.getLeft(), padding.getBottom(), padding.getRight());
        }
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
    public void processAxis(final Axis axis, final AxisType axisType) {
        super.processAxis(axis, axisType);
        axis.setTickMarksVisible(false);
        axis.setAxisLineVisible(false);
        axis.setLabelLocation(AxisLabelLocation.MIDDLE);
        axis.setTickLabelInsets(new RectangleInsets(2.0, 8.0, 2.0, 8.0));
        JFreeChartLocaleChanger.changeLocale(axis, getLocale());
        FiniteTickUnitSource.maybeWrap(axis);
        if (axis instanceof ValueAxis) {
            final ValueAxis cAxis = (ValueAxis) axis;
            cAxis.setLowerMargin(ValueAxis.DEFAULT_LOWER_MARGIN * 2);
            cAxis.setUpperMargin(ValueAxis.DEFAULT_UPPER_MARGIN * 2);
        }
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

}
