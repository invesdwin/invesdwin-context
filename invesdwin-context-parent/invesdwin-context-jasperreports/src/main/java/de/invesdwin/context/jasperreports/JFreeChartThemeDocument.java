package de.invesdwin.context.jasperreports;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.title.Title;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;

import de.invesdwin.context.jfreechart.LegendItems;
import de.invesdwin.context.jfreechart.plot.IAxisPlot;
import de.invesdwin.context.jfreechart.visitor.JFreeChartThemeSwing;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Objects;

@Immutable
public class JFreeChartThemeDocument extends JFreeChartThemeSwing {

    public static final boolean DEFAULT_OUTLINE_VISIBLE = false;
    public static final Paint DEFAULT_GRID_PAINT = JFreeChartThemeSwing.DEFAULT_OUTLINE_PAINT;
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(2f, BasicStroke.CAP_SQUARE,
            BasicStroke.JOIN_BEVEL);
    public static final int DEFAULT_SCALE_LEGEND_LINE_WIDTH_ADDEND = 5;
    public static final double DEFAULT_SCALE_LEGEND_RECTANGLE_MULTIPLIER = 2D;
    public static final double DEFAULT_SCALE_LEGEND_SHAPE_MULTIPLIER = 1.5D;
    public static final RectangleInsets DEFAULT_CHART_PADDING = RectangleInsets.ZERO_INSETS;
    public static final int DEFAULT_COMBINED_PLOT_GAP = 30;
    public static final RectangleInsets DEFAULT_TITLE_TOP_PADDING = new RectangleInsets(1, 1, 15, 1);
    public static final RectangleInsets DEFAULT_TITLE_BOTTOM_PADDING = new RectangleInsets(10, 1, 1, 1);
    public static final double DEFAULT_TICK_LABEL_INSET_TOP_OR_BOTTOM = 4;
    public static final double DEFAULT_TICK_LABEL_INSET_LEFT_OR_RIGHT = 8;
    public static final double DEFAULT_TICK_LABEL_INSET_BETWEEN = 2;
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
        chart.setBackgroundPaint(getBackgroundPaint());
    }

    @Override
    public void processTitle(final Title title) {
        super.processTitle(title);
        if (title.getPosition() == RectangleEdge.TOP) {
            title.setPadding(getTitleTopPadding());
        } else if (title.getPosition() == RectangleEdge.BOTTOM) {
            title.setPadding(getTitleBottomPadding());
        }
    }

    @Override
    protected void processAxisPlot(final IAxisPlot plot) {
        super.processAxisPlot(plot);
        final LegendItemCollection legendItems = processLegendItems(plot.getLegendItems());
        plot.setFixedLegendItems(legendItems);
    }

    protected LegendItemCollection processLegendItems(final LegendItemCollection legendItems) {
        for (int i = 0; i < legendItems.getItemCount(); i++) {
            final LegendItem legendItem = legendItems.get(i);
            LegendItems.setLabel(legendItem, " " + legendItem.getLabel() + " ");
            if (legendItem.isLineVisible()) {
                processLegendItemLine(legendItem);
            }
            if (legendItem.isShapeVisible()) {
                final Shape shape = legendItem.getShape();
                if (shape instanceof Rectangle2D.Double) {
                    final Rectangle2D.Double cShape = (Double) shape;
                    processLegendItemRectangle(legendItem, cShape);
                } else if (shape instanceof Ellipse2D.Double) {
                    final Ellipse2D.Double cShape = (Ellipse2D.Double) shape;
                    processLegendItemEllipse(legendItem, cShape);
                } else if (shape instanceof Path2D) {
                    final Path2D cShape = (Path2D) shape;
                    processLegendItemPath2D(legendItem, cShape);
                    //CHECKSTYLE:OFF
                } else {
                    throw UnknownArgumentException.newInstance(Class.class, shape.getClass());
                }
            }
        }
        return legendItems;
    }

    protected void processLegendItemLine(final LegendItem legendItem) {
        final Stroke lineStroke = legendItem.getLineStroke();
        if (lineStroke instanceof BasicStroke) {
            final int lineWidthAddend = getScaleLegendLineWidthAddend();
            final BasicStroke cLineStroke = (BasicStroke) lineStroke;
            legendItem.setLineStroke(new BasicStroke(cLineStroke.getLineWidth() + lineWidthAddend,
                    cLineStroke.getEndCap(), cLineStroke.getLineJoin(), cLineStroke.getMiterLimit(),
                    cLineStroke.getDashArray(), cLineStroke.getDashPhase()));
        } else {
            throw UnknownArgumentException.newInstance(Class.class, lineStroke.getClass());
        }
    }

    protected void processLegendItemRectangle(final LegendItem legendItem, final Rectangle2D.Double shape) {
        final double multiplier = getScaleLegendRectangleMultiplier();
        legendItem.setShape(
                new Rectangle2D.Double(shape.x, shape.y, shape.width * multiplier, shape.height * multiplier));
    }

    protected void processLegendItemEllipse(final LegendItem legendItem, final Ellipse2D.Double shape) {
        final double multiplier = getScaleLegendShapeMultiplier();
        legendItem
                .setShape(new Ellipse2D.Double(shape.x, shape.y, shape.width * multiplier, shape.height * multiplier));
    }

    protected void processLegendItemPath2D(final LegendItem legendItem, final Path2D shape) {
        final double multiplier = getScaleLegendShapeMultiplier();
        final Path2D cShape = Objects.clone(shape);
        cShape.transform(AffineTransform.getScaleInstance(multiplier, multiplier));
        legendItem.setShape(cShape);
    }

    protected int getScaleLegendLineWidthAddend() {
        return DEFAULT_SCALE_LEGEND_LINE_WIDTH_ADDEND;
    }

    protected double getScaleLegendRectangleMultiplier() {
        return DEFAULT_SCALE_LEGEND_RECTANGLE_MULTIPLIER;
    }

    protected double getScaleLegendShapeMultiplier() {
        return DEFAULT_SCALE_LEGEND_SHAPE_MULTIPLIER;
    }

    @Override
    protected Paint getGridlinePaint() {
        return DEFAULT_GRID_PAINT;
    }

    @Override
    protected Stroke getGridlineStroke() {
        return DEFAULT_GRIDLINE_STROKE;
    }

    @Override
    protected boolean isOutlineVisible() {
        return DEFAULT_OUTLINE_VISIBLE;
    }

    @Override
    protected double getCombinedPlotGap() {
        return DEFAULT_COMBINED_PLOT_GAP;
    }

    @Override
    protected RectangleInsets getChartPadding() {
        return DEFAULT_CHART_PADDING;
    }

    protected RectangleInsets getTitleBottomPadding() {
        return DEFAULT_TITLE_BOTTOM_PADDING;
    }

    protected RectangleInsets getTitleTopPadding() {
        return DEFAULT_TITLE_TOP_PADDING;
    }

    @Override
    protected RectangleInsets getTickLabelInsetsBottom() {
        return DEFAULT_TICK_LABEL_INSETS_BOTTOM;
    }

    @Override
    protected RectangleInsets getTickLabelInsetsTop() {
        return DEFAULT_TICK_LABEL_INSETS_TOP;
    }

    @Override
    protected RectangleInsets getTickLabelInsetsRight() {
        return DEFAULT_TICK_LABEL_INSETS_RIGHT;
    }

    @Override
    protected RectangleInsets getTickLabelInsetsLeft() {
        return DEFAULT_TICK_LABEL_INSETS_LEFT;
    }

}
