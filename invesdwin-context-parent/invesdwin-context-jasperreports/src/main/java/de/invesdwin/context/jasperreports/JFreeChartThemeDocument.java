package de.invesdwin.context.jasperreports;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;

import de.invesdwin.context.jfreechart.util.LegendItems;
import de.invesdwin.context.jfreechart.visitor.JFreeChartThemeSwing;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Objects;

@Immutable
public class JFreeChartThemeDocument extends JFreeChartThemeSwing {

    public static final boolean DEFAULT_OUTLINE_VISIBLE = false;
    public static final Paint DEFAULT_GRID_PAINT = JFreeChartThemeSwing.DEFAULT_OUTLINE_PAINT;
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(2f, BasicStroke.CAP_SQUARE,
            BasicStroke.JOIN_BEVEL);

    @Override
    protected void processChart(final JFreeChart chart) {
        super.processChart(chart);
        chart.setBackgroundPaint(DEFAULT_BACKGROUND_PAINT);
    }

    @Override
    protected void processPlotRecursive(final Plot plot, final Set<Integer> duplicateAxisFilter) {
        super.processPlotRecursive(plot, duplicateAxisFilter);
    }

    @Override
    protected void processCategoryPlot(final Set<Integer> duplicateAxisFilter, final CategoryPlot plot) {
        super.processCategoryPlot(duplicateAxisFilter, plot);
        final LegendItemCollection legendItems = processLegendItems(plot);
        plot.setFixedLegendItems(legendItems);
    }

    @Override
    protected void processXYPlot(final Set<Integer> duplicateAxisFilter, final XYPlot plot) {
        super.processXYPlot(duplicateAxisFilter, plot);
        final LegendItemCollection legendItems = processLegendItems(plot);
        plot.setFixedLegendItems(legendItems);
    }

    private LegendItemCollection processLegendItems(final Plot plot) {
        final LegendItemCollection legendItems = plot.getLegendItems();
        for (int i = 0; i < legendItems.getItemCount(); i++) {
            final LegendItem legendItem = legendItems.get(i);
            LegendItems.setLabel(legendItem, " " + legendItem.getLabel() + " ");
            if (legendItem.isLineVisible()) {
                final Stroke lineStroke = legendItem.getLineStroke();
                if (lineStroke instanceof BasicStroke) {
                    final BasicStroke cLineStroke = (BasicStroke) lineStroke;
                    legendItem.setLineStroke(new BasicStroke(cLineStroke.getLineWidth() + 5, cLineStroke.getEndCap(),
                            cLineStroke.getLineJoin(), cLineStroke.getMiterLimit(), cLineStroke.getDashArray(),
                            cLineStroke.getDashPhase()));
                } else {
                    throw UnknownArgumentException.newInstance(Class.class, lineStroke.getClass());
                }
            }
            if (legendItem.isShapeVisible()) {
                final Shape shape = legendItem.getShape();
                if (shape instanceof Rectangle2D.Double) {
                    final Rectangle2D.Double cShape = (Rectangle2D.Double) shape;
                    legendItem
                            .setShape(new Rectangle2D.Double(cShape.x, cShape.y, cShape.width * 2, cShape.height * 2));
                } else if (shape instanceof Ellipse2D.Double) {
                    final Ellipse2D.Double cShape = (Ellipse2D.Double) shape;
                    legendItem.setShape(
                            new Ellipse2D.Double(cShape.x, cShape.y, cShape.width * 1.5, cShape.height * 1.5));
                } else if (shape instanceof GeneralPath) {
                    final GeneralPath cShape = Objects.clone((GeneralPath) shape);
                    cShape.transform(AffineTransform.getScaleInstance(1.5, 1.5));
                    legendItem.setShape(cShape);
                } else if (shape instanceof Polygon) {
                    final GeneralPath cShape = Objects.clone((GeneralPath) shape);
                    cShape.transform(AffineTransform.getScaleInstance(1.5, 1.5));
                    legendItem.setShape(cShape);
                } else {
                    throw UnknownArgumentException.newInstance(Class.class, shape.getClass());
                }
            }
        }
        return legendItems;
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
