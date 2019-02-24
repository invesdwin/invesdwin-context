package de.invesdwin.context.jfreechart.renderer.custom;

import java.awt.Color;
import java.awt.Paint;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.panel.helper.config.PlotConfigurationHelper;
import de.invesdwin.context.jfreechart.panel.helper.config.PriceInitialSettings;
import de.invesdwin.context.jfreechart.panel.helper.config.SeriesInitialSettings;
import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.context.jfreechart.renderer.DisabledXYItemRenderer;
import de.invesdwin.util.lang.Colors;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

/**
 * Instead of drawing an outline, this one draws a line so that at start and end of series the line does not go to zero.
 * 
 * @author subes
 *
 */
@NotThreadSafe
public class CustomEquityChangeRenderer extends ACustomEquityChangeRenderer implements ICustomRendererType {

    public static final Color UP_COLOR = CustomProfitLossRenderer.UP_COLOR;
    public static final Color DOWN_COLOR = CustomProfitLossRenderer.DOWN_COLOR;
    private static final Percent AREA_TRANSPARENCY = new Percent(90, PercentScale.PERCENT);
    private static final Percent LINE_TRANSPARENCY = new Percent(30, PercentScale.PERCENT);

    private final XYDataset dataset;
    private Color upColor;
    private Color downColor;

    public CustomEquityChangeRenderer(final PlotConfigurationHelper plotConfigurationHelper, final XYDataset dataset) {
        this.dataset = dataset;
        final PriceInitialSettings config = plotConfigurationHelper.getPriceInitialSettings();

        setSeriesPaint(0, Colors.setTransparency(UP_COLOR, LINE_TRANSPARENCY));
        setSeriesStroke(0, config.getSeriesStroke());

        this.upColor = Colors.setTransparency(UP_COLOR, AREA_TRANSPARENCY);
        this.downColor = Colors.setTransparency(DOWN_COLOR, AREA_TRANSPARENCY);
    }

    @Override
    public Paint getItemPaint(final int row, final int column) {
        final double yValue = dataset.getYValue(row, column);
        if (Double.isNaN(yValue)) {
            return DisabledXYItemRenderer.INVISIBLE_COLOR;
        } else if (yValue >= 0) {
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
        return true;
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