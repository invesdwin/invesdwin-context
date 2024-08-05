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

    @Override
    protected void processChart(final JFreeChart chart) {
        super.processChart(chart);
        chart.setBackgroundPaint(getBackgroundPaint());
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

}
