package de.invesdwin.context.jfreechart.plot.annotation.priceline;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.util.LineUtils;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.plot.XYPlots;
import de.invesdwin.util.lang.Colors;
import de.invesdwin.util.math.decimal.scaled.Percent;

@NotThreadSafe
public class XYPriceLineAnnotation extends AbstractXYAnnotation implements IPriceLineRenderer {

    public static final Font FONT = XYPlots.DEFAULT_FONT;
    public static final Percent TRANSPARENCY = Percent.ZERO_PERCENT;

    private static final ValueAxis ABSOLUTE_AXIS = XYPlots.DRAWING_ABSOLUTE_AXIS;
    /** The line stroke. */
    private final XYDataset dataset;
    private final XYItemRenderer renderer;
    private Stroke stroke;
    private boolean priceLineVisible;
    private boolean priceLabelEnabled;

    public XYPriceLineAnnotation(final XYDataset dataset, final XYItemRenderer renderer) {
        this.dataset = dataset;
        this.renderer = renderer;
        this.stroke = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 1.5f, 1.5f },
                0.0f);

    }

    public void setStroke(final Stroke stroke) {
        this.stroke = stroke;
        fireAnnotationChanged();
    }

    public Stroke getStroke() {
        return stroke;
    }

    @Override
    public void setPriceLineVisible(final boolean priceLineVisible) {
        this.priceLineVisible = priceLineVisible;
        fireAnnotationChanged();
    }

    @Override
    public boolean isPriceLineVisible() {
        return priceLineVisible;
    }

    @Override
    public void setPriceLabelEnabled(final boolean priceLabelEnabled) {
        this.priceLabelEnabled = priceLabelEnabled;
    }

    @Override
    public boolean isPriceLabelEnabled() {
        return priceLabelEnabled;
    }

    @Override
    public void draw(final Graphics2D g2, final XYPlot plot, final Rectangle2D dataArea, final ValueAxis domainAxis,
            final ValueAxis rangeAxis, final int rendererIndex, final PlotRenderingInfo info) {
        if (!priceLineVisible) {
            return;
        }

        final PlotOrientation orientation = plot.getOrientation();
        final RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);

        final int lastItem = dataset.getItemCount(0) - 1;
        final double x1 = dataArea.getMinX();
        final double x2 = dataArea.getMaxX();

        final double lastPrice = dataset.getYValue(0, lastItem);
        final double y = rangeAxis.valueToJava2D(lastPrice, dataArea, rangeEdge);

        float j2DX1 = 0.0f;
        float j2DX2 = 0.0f;
        float j2DY1 = 0.0f;
        float j2DY2 = 0.0f;
        if (orientation == PlotOrientation.VERTICAL) {
            j2DX1 = (float) x1;
            j2DY1 = (float) y;
            j2DX2 = (float) x2;
            j2DY2 = (float) y;
        } else if (orientation == PlotOrientation.HORIZONTAL) {
            j2DY1 = (float) x1;
            j2DX1 = (float) y;
            j2DY2 = (float) x2;
            j2DX2 = (float) y;
        }
        final Color paint = Colors.setTransparency((Color) renderer.getItemPaint(0, lastItem), TRANSPARENCY);
        g2.setPaint(paint);
        g2.setStroke(this.stroke);
        final Line2D line = new Line2D.Float(j2DX1, j2DY1, j2DX2, j2DY2);
        // line is clipped to avoid JRE bug 6574155, for more info
        // see JFreeChart bug 2221495
        final boolean visible = LineUtils.clipLine(line, dataArea);
        if (visible) {
            g2.draw(line);

            if (priceLabelEnabled) {
                final NumberAxis cRangeAxis = (NumberAxis) rangeAxis;
                final NumberFormat rangeAxisFormat = cRangeAxis.getNumberFormatOverride();

                final XYTextAnnotation priceAnnotation = new XYTextAnnotation(rangeAxisFormat.format(lastPrice),
                        x2 - 1D, y + 1D);
                priceAnnotation.setPaint(paint);
                priceAnnotation.setFont(FONT);
                priceAnnotation.setTextAnchor(TextAnchor.TOP_RIGHT);
                priceAnnotation.draw(g2, plot, dataArea, ABSOLUTE_AXIS, ABSOLUTE_AXIS, rendererIndex, info);
            }
        }

    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj == this;
    }

}