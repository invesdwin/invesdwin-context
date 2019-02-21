package de.invesdwin.context.jfreechart.renderer.custom;

import java.awt.Color;
import java.awt.Paint;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;

import de.invesdwin.context.jfreechart.panel.helper.config.InitialSeriesSettings;
import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.config.PriceRendererType;
import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.context.jfreechart.renderer.IUpDownColorRenderer;
import de.invesdwin.context.jfreechart.renderer.OhlcCandlestickRenderer;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Colors;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

@NotThreadSafe
public class CustomVolumeBarRenderer extends XYBarRenderer implements IUpDownColorRenderer, ICustomRendererType {

    public static final Percent DEFAULT_VOLUME_TRANSPARENCY = new Percent(50D, PercentScale.PERCENT);

    private final OhlcCandlestickRenderer candlestickRenderer;
    private Color upColor;
    private Color downColor;

    public CustomVolumeBarRenderer(final PlotConfigurationHelper plotConfigurationHelper) {
        super(0.25f);
        this.candlestickRenderer = (OhlcCandlestickRenderer) plotConfigurationHelper
                .getPriceRenderer(PriceRendererType.Candlestick);

        setBarPainter(new StandardXYBarPainter());
        setShadowVisible(false);
        setSeriesPaint(0, plotConfigurationHelper.getPriceColor());
        setSeriesFillPaint(0, plotConfigurationHelper.getPriceColor());
        setSeriesStroke(0, plotConfigurationHelper.getPriceStroke());
        setDrawBarOutline(false);

        this.upColor = Colors.setTransparency(plotConfigurationHelper.getUpColor(), DEFAULT_VOLUME_TRANSPARENCY);
        this.downColor = Colors.setTransparency(plotConfigurationHelper.getDownColor(), DEFAULT_VOLUME_TRANSPARENCY);
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

    @Override
    public boolean isLineStyleConfigurable() {
        return false;
    }

    @Override
    public boolean isLineWidthConfigurable() {
        return false;
    }

    @Override
    public boolean isUpColorConfigurable() {
        return true;
    }

    @Override
    public boolean isDownColorConfigurable() {
        return true;
    }

    @Override
    public boolean isPriceColorConfigurable() {
        return false;
    }

    @Override
    public void reset(final HighlightedLegendInfo highlighted, final InitialSeriesSettings initialSettings) {
        setUpColor(initialSettings.getUpColor());
        setDownColor(initialSettings.getDownColor());
        setSeriesPaint(0, initialSettings.getPriceColor());
        setSeriesStroke(0, initialSettings.getPriceStroke());
        highlighted.setRenderer(this);
    }

    @Override
    public String getName() {
        return "Volume";
    }

}
