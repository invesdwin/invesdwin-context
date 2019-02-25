package de.invesdwin.context.jfreechart.renderer.custom;

import java.awt.Color;
import java.awt.Paint;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.renderer.xy.StandardXYBarPainter;

import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.config.PriceInitialSettings;
import de.invesdwin.context.jfreechart.panel.helper.config.PriceRendererType;
import de.invesdwin.context.jfreechart.panel.helper.config.SeriesInitialSettings;
import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.context.jfreechart.renderer.FastXYBarRenderer;
import de.invesdwin.context.jfreechart.renderer.IUpDownColorRenderer;
import de.invesdwin.context.jfreechart.renderer.FastCandlestickRenderer;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Colors;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

@NotThreadSafe
public class CustomVolumeBarRenderer extends FastXYBarRenderer implements IUpDownColorRenderer, ICustomRendererType {

    public static final Percent DEFAULT_VOLUME_TRANSPARENCY = new Percent(50D, PercentScale.PERCENT);

    private final FastCandlestickRenderer candlestickRenderer;
    private Color upColor;
    private Color downColor;

    public CustomVolumeBarRenderer(final PlotConfigurationHelper plotConfigurationHelper) {
        super(0.25f);
        final PriceInitialSettings config = plotConfigurationHelper.getPriceInitialSettings();
        this.candlestickRenderer = (FastCandlestickRenderer) config.getPriceRenderer(PriceRendererType.Candlestick);

        setBarPainter(new StandardXYBarPainter());
        setShadowVisible(false);
        setSeriesPaint(0, config.getSeriesColor());
        setSeriesStroke(0, config.getSeriesStroke());
        setDrawBarOutline(false);

        this.upColor = Colors.setTransparency(config.getUpColor(), DEFAULT_VOLUME_TRANSPARENCY);
        this.downColor = Colors.setTransparency(config.getDownColor(), DEFAULT_VOLUME_TRANSPARENCY);
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
    public boolean isLineStyleConfigurable() {
        return false;
    }

    @Override
    public boolean isLineWidthConfigurable() {
        return false;
    }

    @Override
    public boolean isSeriesColorConfigurable() {
        return false;
    }

    @Override
    public void reset(final HighlightedLegendInfo highlighted, final SeriesInitialSettings initialSettings) {
        setUpColor(initialSettings.getUpColor());
        setDownColor(initialSettings.getDownColor());
        setSeriesPaint(0, initialSettings.getSeriesColor());
        setSeriesStroke(0, initialSettings.getSeriesStroke());
        highlighted.setRenderer(this);
    }

    @Override
    public String getName() {
        return "Volume";
    }

}
