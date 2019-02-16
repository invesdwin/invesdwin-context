package de.invesdwin.context.jfreechart.panel;

import java.awt.Color;
import java.awt.Graphics2D;
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

    private final XYItemRenderer enabledRenderer;

    public DisabledXYItemRenderer(final XYItemRenderer enabledRenderer) {
        this.enabledRenderer = enabledRenderer;
        setSeriesPaint(0, new Color(0, 0, 0, 0));
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

}
