package de.invesdwin.context.jfreechart.renderer;

import java.awt.Color;
import java.awt.Paint;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;

import de.invesdwin.util.error.UnknownArgumentException;

@NotThreadSafe
public class VolumeRenderer extends XYBarRenderer {

    private final CustomCandlestickRenderer candlestickRenderer;
    private final Paint upPaint;
    private final Paint downPaint;

    public VolumeRenderer(final CustomCandlestickRenderer candlestickRenderer) {
        super(0.25f);
        this.candlestickRenderer = candlestickRenderer;

        setBarPainter(new StandardXYBarPainter());
        setShadowVisible(false);
        setSeriesPaint(0, candlestickRenderer.getSeriesPaint(0));
        setSeriesFillPaint(0, candlestickRenderer.getSeriesFillPaint(0));
        setDrawBarOutline(false);

        final Color upColor = (Color) candlestickRenderer.getUpPaint();
        this.upPaint = new Color(upColor.getRed(), upColor.getGreen(), upColor.getBlue(), 100);
        final Color downColor = (Color) candlestickRenderer.getDownPaint();
        this.downPaint = new Color(downColor.getRed(), downColor.getGreen(), downColor.getBlue(), 100);
    }

    @Override
    public Paint getItemPaint(final int row, final int column) {
        final Paint itemPaint = candlestickRenderer.getItemPaint(row, column);
        if (itemPaint == candlestickRenderer.getUpPaint()) {
            return upPaint;
        } else if (itemPaint == candlestickRenderer.getDownPaint()) {
            return downPaint;
        } else {
            throw UnknownArgumentException.newInstance(Paint.class, itemPaint);
        }
    }

}
