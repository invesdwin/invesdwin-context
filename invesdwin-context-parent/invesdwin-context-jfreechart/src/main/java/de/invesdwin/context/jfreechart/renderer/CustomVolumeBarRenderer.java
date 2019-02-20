package de.invesdwin.context.jfreechart.renderer;

import java.awt.Color;
import java.awt.Paint;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;

import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.util.error.UnknownArgumentException;

@NotThreadSafe
public class CustomVolumeBarRenderer extends XYBarRenderer implements IUpDownColorRenderer {

    private final CustomOhlcCandlestickRenderer candlestickRenderer;
    private Color upColor;
    private Color downColor;

    public CustomVolumeBarRenderer(final CustomOhlcCandlestickRenderer candlestickRenderer) {
        super(0.25f);
        this.candlestickRenderer = candlestickRenderer;

        setBarPainter(new StandardXYBarPainter());
        setShadowVisible(false);
        setSeriesPaint(0, candlestickRenderer.getSeriesPaint(0));
        setSeriesFillPaint(0, candlestickRenderer.getSeriesFillPaint(0));
        setSeriesStroke(0, PlotConfigurationHelper.DEFAULT_STROKE);
        setDrawBarOutline(false);

        this.upColor = candlestickRenderer.getUpColor();
        this.downColor = candlestickRenderer.getDownColor();
    }

    @Override
    public void setUpColor(final Color upColor) {
        this.upColor = upColor;
        fireChangeEvent();
    }

    @Override
    public Color getUpColor() {
        return upColor;
    }

    @Override
    public void setDownColor(final Color downColor) {
        this.downColor = downColor;
        fireChangeEvent();
    }

    @Override
    public Color getDownColor() {
        return downColor;
    }

    @Override
    public Paint getItemPaint(final int row, final int column) {
        final Paint itemPaint = candlestickRenderer.getItemPaint(row, column);
        if (itemPaint == candlestickRenderer.getUpColor()) {
            return upColor;
        } else if (itemPaint == candlestickRenderer.getDownColor()) {
            return downColor;
        } else {
            throw UnknownArgumentException.newInstance(Paint.class, itemPaint);
        }
    }

}
