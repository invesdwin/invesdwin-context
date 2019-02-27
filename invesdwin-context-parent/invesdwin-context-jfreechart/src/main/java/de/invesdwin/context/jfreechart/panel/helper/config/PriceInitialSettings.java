package de.invesdwin.context.jfreechart.panel.helper.config;

import java.awt.Color;
import java.awt.Stroke;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.renderer.xy.XYItemRenderer;

import de.invesdwin.context.jfreechart.plot.dataset.IndexedDateTimeOHLCDataset;
import de.invesdwin.context.jfreechart.plot.renderer.FastCandlestickRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.FastHighLowRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.FastStandardXYItemRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.FastXYAreaRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.FastXYStepRenderer;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.error.UnknownArgumentException;
import de.invesdwin.util.lang.Colors;

@NotThreadSafe
public class PriceInitialSettings {

    public static final Color DEFAULT_DOWN_COLOR = Colors.fromHex("#EF5350");
    public static final Color DEFAULT_UP_COLOR = Colors.fromHex("#26A69A");
    public static final Color DEFAULT_SERIES_COLOR = Colors.fromHex("#3C78D8");
    public static final Stroke DEFAULT_SERIES_STROKE = LineStyleType.Solid.getStroke(LineWidthType._1);
    public static final PriceRendererType DEFAULT_PRICE_RENDERER_TYPE = PriceRendererType.Candlestick;
    private static final boolean DEFAULT_PRICE_LINE_VISIBLE = true;

    private final PlotConfigurationHelper plotConfigurationHelper;

    private final FastCandlestickRenderer candlestickRenderer;
    private final FastHighLowRenderer barsRenderer;
    private final FastXYAreaRenderer areaRenderer;
    private final FastStandardXYItemRenderer lineRenderer;
    private final FastXYStepRenderer stepLineRenderer;

    /*
     * the renderers can diverge from these settings using the context menu configuration, though a reset will use these
     * values here again. using the setters will override the context menu configuration
     */
    private PriceRendererType priceRendererType = DEFAULT_PRICE_RENDERER_TYPE;
    private Color upColor;
    private Color downColor;
    private Color seriesColor;
    private Stroke seriesStroke;
    private boolean priceLineVisible;

    public PriceInitialSettings(final PlotConfigurationHelper plotConfigurationHelper) {
        this.plotConfigurationHelper = plotConfigurationHelper;

        final IndexedDateTimeOHLCDataset dataset = plotConfigurationHelper.getChartPanel().getDataset();
        this.candlestickRenderer = new FastCandlestickRenderer(dataset);
        this.barsRenderer = new FastHighLowRenderer(candlestickRenderer);
        this.areaRenderer = new FastXYAreaRenderer(dataset);
        this.lineRenderer = new FastStandardXYItemRenderer(dataset);
        this.stepLineRenderer = new FastXYStepRenderer(dataset);

        setUpColor(DEFAULT_UP_COLOR);
        setDownColor(DEFAULT_DOWN_COLOR);
        setSeriesColor(DEFAULT_SERIES_COLOR);
        setSeriesStroke(DEFAULT_SERIES_STROKE);
        setPriceLineVisible(DEFAULT_PRICE_LINE_VISIBLE);
    }

    public PriceRendererType getPriceRendererType() {
        return priceRendererType;
    }

    public void setPriceRendererType(final PriceRendererType priceRendererType) {
        if (priceRendererType != this.priceRendererType) {
            updatePriceRendererType(priceRendererType);
            plotConfigurationHelper.getChartPanel().update();
        }
        this.priceRendererType = priceRendererType;
    }

    void updatePriceRendererType(final PriceRendererType priceRendererType) {
        final XYItemRenderer renderer = plotConfigurationHelper.getChartPanel().getOhlcPlot().getRenderer(0);
        final XYItemRenderer newRenderer = getPriceRenderer(priceRendererType);
        newRenderer.setSeriesPaint(0, renderer.getSeriesPaint(0));
        newRenderer.setSeriesStroke(0, renderer.getSeriesStroke(0));
        plotConfigurationHelper.getChartPanel().getOhlcPlot().setRenderer(0, newRenderer);
    }

    public XYItemRenderer getPriceRenderer() {
        return getPriceRenderer(priceRendererType);
    }

    public XYItemRenderer getPriceRenderer(final PriceRendererType priceRendererType) {
        switch (priceRendererType) {
        case Area:
            return areaRenderer;
        case Line:
            return lineRenderer;
        case Step:
            return stepLineRenderer;
        case OHLC:
            return barsRenderer;
        case Candlestick:
            return candlestickRenderer;
        default:
            throw UnknownArgumentException.newInstance(PriceRendererType.class, priceRendererType);
        }
    }

    public PriceRendererType getPriceRendererType(final XYItemRenderer renderer) {
        if (renderer == areaRenderer) {
            return PriceRendererType.Area;
        } else if (renderer == lineRenderer) {
            return PriceRendererType.Line;
        } else if (renderer == stepLineRenderer) {
            return PriceRendererType.Step;
        } else if (renderer == barsRenderer) {
            return PriceRendererType.OHLC;
        } else if (renderer == candlestickRenderer) {
            return PriceRendererType.Candlestick;
        } else {
            throw UnknownArgumentException.newInstance(XYItemRenderer.class, renderer);
        }
    }

    public Color getUpColor() {
        return upColor;
    }

    public void setUpColor(final Color upColor) {
        this.upColor = upColor;
        updateUpColor();
    }

    private void updateUpColor() {
        candlestickRenderer.setUpColor(upColor);
    }

    public Color getDownColor() {
        return downColor;
    }

    public void setDownColor(final Color downColor) {
        this.downColor = downColor;
        updateDownColor();
    }

    private void updateDownColor() {
        candlestickRenderer.setDownColor(downColor);
    }

    public Color getSeriesColor() {
        return seriesColor;
    }

    public void setSeriesColor(final Color seriesColor) {
        this.seriesColor = seriesColor;
        updateSeriesColor();
    }

    private void updateSeriesColor() {
        this.candlestickRenderer.setSeriesPaint(0, seriesColor);
        this.barsRenderer.setSeriesPaint(0, seriesColor);
        this.lineRenderer.setSeriesPaint(0, seriesColor);
        this.areaRenderer.setSeriesPaint(0, seriesColor);
        this.stepLineRenderer.setSeriesPaint(0, seriesColor);
    }

    public void setSeriesStroke(final LineStyleType lineStyleType, final LineWidthType lineWidthType) {
        this.seriesStroke = lineStyleType.getStroke(lineWidthType);
        updateSeriesStroke();
    }

    private void updateSeriesStroke() {
        this.candlestickRenderer.setSeriesStroke(0, seriesStroke);
        this.barsRenderer.setSeriesStroke(0, seriesStroke);
        this.lineRenderer.setSeriesStroke(0, seriesStroke);
        this.areaRenderer.setSeriesStroke(0, seriesStroke);
        this.stepLineRenderer.setSeriesStroke(0, seriesStroke);
    }

    public void setSeriesStroke(final Stroke seriesStroke) {
        Assertions.checkNotNull(LineStyleType.valueOf(seriesStroke));
        this.seriesStroke = seriesStroke;
    }

    public Stroke getSeriesStroke() {
        return seriesStroke;
    }

    public boolean isPriceLineVisible() {
        return priceLineVisible;
    }

    public void setPriceLineVisible(final boolean priceLineVisible) {
        this.priceLineVisible = priceLineVisible;
        updatePriceLineVisible();
    }

    private void updatePriceLineVisible() {
        this.candlestickRenderer.setPriceLineVisible(priceLineVisible);
        this.barsRenderer.setPriceLineVisible(priceLineVisible);
        this.lineRenderer.setPriceLineVisible(priceLineVisible);
        this.areaRenderer.setPriceLineVisible(priceLineVisible);
        this.stepLineRenderer.setPriceLineVisible(priceLineVisible);
    }

    public void reset() {
        updateUpColor();
        updateDownColor();
        updateSeriesColor();
        updateSeriesStroke();
        updatePriceLineVisible();
        plotConfigurationHelper.getChartPanel().getOhlcPlot().setRenderer(0, getPriceRenderer(priceRendererType));
    }

    public PriceRendererType getCurrentPriceRendererType() {
        return getPriceRendererType(plotConfigurationHelper.getChartPanel().getOhlcPlot().getRenderer(0));
    }

}
