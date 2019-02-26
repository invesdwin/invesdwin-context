package de.invesdwin.context.jfreechart.plot.renderer.custom;

import org.jfree.chart.renderer.xy.XYItemRenderer;

import de.invesdwin.context.jfreechart.panel.helper.config.IRendererType;
import de.invesdwin.context.jfreechart.panel.helper.config.SeriesInitialSettings;
import de.invesdwin.context.jfreechart.panel.helper.config.SeriesRendererType;
import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;
import de.invesdwin.context.jfreechart.plot.renderer.IUpDownColorRenderer;

public interface ICustomRendererType extends IRendererType, XYItemRenderer {

    String getName();

    @Override
    default boolean isSeriesRendererTypeConfigurable() {
        return true;
    }

    @Override
    default boolean isSeriesColorConfigurable() {
        return true;
    }

    @Override
    default SeriesRendererType getSeriesRendererType() {
        return SeriesRendererType.Custom;
    }

    @Override
    default boolean isUpColorConfigurable() {
        return this instanceof IUpDownColorRenderer;
    }

    @Override
    default boolean isDownColorConfigurable() {
        return this instanceof IUpDownColorRenderer;
    }

    @Override
    default void reset(final HighlightedLegendInfo highlighted, final SeriesInitialSettings initialSettings) {
        if (this instanceof IUpDownColorRenderer) {
            final IUpDownColorRenderer cThis = (IUpDownColorRenderer) this;
            cThis.setUpColor(initialSettings.getUpColor());
            cThis.setDownColor(initialSettings.getDownColor());
        }
        setSeriesPaint(0, initialSettings.getSeriesColor());
        setSeriesStroke(0, initialSettings.getSeriesStroke());
        highlighted.setRenderer(this);
    }

}
