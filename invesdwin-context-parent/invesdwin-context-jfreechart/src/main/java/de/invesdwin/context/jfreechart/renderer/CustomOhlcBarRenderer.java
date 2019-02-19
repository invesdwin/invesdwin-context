package de.invesdwin.context.jfreechart.renderer;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;

@NotThreadSafe
public class CustomOhlcBarRenderer extends HighLowRenderer {

    private final CustomOhlcCandlestickRenderer candlestickRenderer;
    private double tickLength;

    public CustomOhlcBarRenderer(final CustomOhlcCandlestickRenderer candlestickRenderer) {
        this.candlestickRenderer = candlestickRenderer;
    }

    @Override
    public void setTickLength(final double length) {
        this.tickLength = length;
    }

    @Override
    public double getTickLength() {
        return tickLength;
    }

    @Override
    public Paint getItemPaint(final int row, final int column) {
        return candlestickRenderer.getItemPaint(row, column);
    }

    @Override
    public Range findRangeBounds(final XYDataset dataset) {
        return findRangeBounds(dataset, false);
    }

    @Override
    public XYItemRendererState initialise(final Graphics2D g2, final Rectangle2D dataArea, final XYPlot plot,
            final XYDataset dataset, final PlotRenderingInfo info) {
        return candlestickRenderer.initialise(g2, dataArea, plot, dataset, info);
    }

    //CHECKSTYLE:OFF
    @Override
    public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea,
            final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis,
            final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState,
            final int pass) {
        //CHECKSTYLE:ON

        final boolean horiz = candlestickRenderer.isHorizontal(plot);
        final double stickWidth = candlestickRenderer.calculateStickWidth(state, dataArea, horiz);
        setTickLength(stickWidth / 2);
        super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState,
                pass);
    }

}