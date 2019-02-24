package de.invesdwin.context.jfreechart.renderer.custom;

import java.awt.Color;
import java.awt.Paint;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.config.PriceInitialSettings;
import de.invesdwin.context.jfreechart.panel.helper.config.SeriesInitialSettings;
import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.context.jfreechart.renderer.IUpDownColorRenderer;
import de.invesdwin.context.jfreechart.renderer.XYAreaLineRenderer;

/**
 * Instead of drawing an outline, this one draws a line so that at start and end of series the line does not go to zero.
 * 
 * @author subes
 *
 */
@NotThreadSafe
public class CustomProfitLossRenderer extends XYAreaLineRenderer implements ICustomRendererType, IUpDownColorRenderer {

    private final XYDataset dataset;
    private Color upColor;
    private Color downColor;

    public CustomProfitLossRenderer(final PlotConfigurationHelper plotConfigurationHelper, final XYDataset dataset) {
        this.dataset = dataset;

        final PriceInitialSettings config = plotConfigurationHelper.getPriceInitialSettings();

        setSeriesPaint(0, config.getSeriesColor());
        setSeriesStroke(0, config.getSeriesStroke());

        this.upColor = config.getUpColor();
        this.downColor = config.getDownColor();
    }

    @Override
    public Paint getItemPaint(final int row, final int column) {
        if (dataset.getYValue(row, column) >= 0D) {
            return upColor;
        } else {
            return downColor;
        }
    }

    @Override
    public boolean isLineStyleConfigurable() {
        return true;
    }

    @Override
    public boolean isLineWidthConfigurable() {
        return true;
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
        return "ProfitLoss";
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

}