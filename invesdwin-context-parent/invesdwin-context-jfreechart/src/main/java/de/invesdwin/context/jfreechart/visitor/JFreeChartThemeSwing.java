package de.invesdwin.context.jfreechart.visitor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.util.Locale;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.Title;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;

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
    protected void processXYPlot(final Set<Integer> duplicateAxisFilter, final XYPlot plot) {
        super.processXYPlot(duplicateAxisFilter, plot);
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
    public void processAxis(final Axis axis) {
        super.processAxis(axis);
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

}
