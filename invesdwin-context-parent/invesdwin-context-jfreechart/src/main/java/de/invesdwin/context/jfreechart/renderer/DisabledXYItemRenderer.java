package de.invesdwin.context.jfreechart.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;

@NotThreadSafe
public class DisabledXYItemRenderer extends AbstractXYItemRenderer {

    public static final Color INVISIBLE_COLOR = new Color(0, 0, 0, 0);

    private final XYItemRenderer enabledRenderer;

    public DisabledXYItemRenderer(final XYItemRenderer enabledRenderer) {
        if (enabledRenderer instanceof DisabledXYItemRenderer) {
            throw new IllegalArgumentException(
                    "enabledRenderer should not be an instance of " + DisabledXYItemRenderer.class.getSimpleName());
        }
        this.enabledRenderer = enabledRenderer;
    }

    public XYItemRenderer getEnabledRenderer() {
        return enabledRenderer;
    }

    //CHECKSTYLE:OFF
    @Override
    public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea,
            final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis,
            final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState,
            final int pass) {}
    //CHECKSTYLE:ON

    public static XYItemRenderer maybeUnwrap(final XYItemRenderer renderer) {
        if (renderer instanceof DisabledXYItemRenderer) {
            final DisabledXYItemRenderer cRenderer = (DisabledXYItemRenderer) renderer;
            return cRenderer.getEnabledRenderer();
        } else {
            return renderer;
        }
    }

    @Override
    public void setSeriesStroke(final int series, final Stroke stroke, final boolean notify) {
        enabledRenderer.setSeriesStroke(series, stroke, notify);
    }

    @Override
    public void setSeriesStroke(final int series, final Stroke stroke) {
        enabledRenderer.setSeriesStroke(series, stroke);
    }

    @Override
    public Stroke getSeriesStroke(final int series) {
        return enabledRenderer.getSeriesStroke(series);
    }

    @Override
    public void setSeriesPaint(final int series, final Paint paint, final boolean notify) {
        enabledRenderer.setSeriesPaint(series, paint, notify);
    }

    @Override
    public void setSeriesPaint(final int series, final Paint paint) {
        enabledRenderer.setSeriesPaint(series, paint);
    }

    @Override
    public Paint getSeriesPaint(final int series) {
        return enabledRenderer.getSeriesPaint(series);
    }

    @Override
    public void setSeriesFillPaint(final int series, final Paint paint, final boolean notify) {
        enabledRenderer.setSeriesFillPaint(series, paint, notify);
    }

    @Override
    public void setSeriesFillPaint(final int series, final Paint paint) {
        enabledRenderer.setSeriesFillPaint(series, paint);
    }

    @Override
    public Paint getSeriesFillPaint(final int series) {
        return enabledRenderer.getSeriesFillPaint(series);
    }

    @Override
    public Paint getItemPaint(final int row, final int column) {
        return INVISIBLE_COLOR;
    }

}
