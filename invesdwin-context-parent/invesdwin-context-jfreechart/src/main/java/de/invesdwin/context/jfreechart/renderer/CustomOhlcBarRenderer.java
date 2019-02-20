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
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;

@NotThreadSafe
public class CustomOhlcBarRenderer extends HighLowRenderer implements IUpDownColorRenderer {

    private static final float STROKE_SCALING_MIN_WIDTH = 0.5F;
    private final CustomOhlcCandlestickRenderer candlestickRenderer;
    private double tickLength;

    public CustomOhlcBarRenderer(final CustomOhlcCandlestickRenderer candlestickRenderer) {
        this.candlestickRenderer = candlestickRenderer;
        setSeriesStroke(0, PlotConfigurationHelper.DEFAULT_PRICE_STROKE);
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
        candlestickRenderer.calculateItemStroke(state, STROKE_SCALING_MIN_WIDTH);
        setTickLength(stickWidth / 2);
        super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState,
                pass);
    }

    @Override
    public Stroke getItemStroke(final int row, final int column) {
        return candlestickRenderer.getItemStroke(row, column);
    }

    @Override
    public void setUpColor(final Color upColor) {
        candlestickRenderer.setUpColor(upColor);
        fireChangeEvent();
    }

    @Override
    public Color getUpColor() {
        return candlestickRenderer.getUpColor();
    }

    @Override
    public void setDownColor(final Color downColor) {
        candlestickRenderer.setDownColor(downColor);
        fireChangeEvent();
    }

    @Override
    public Color getDownColor() {
        return candlestickRenderer.getDownColor();
    }

}