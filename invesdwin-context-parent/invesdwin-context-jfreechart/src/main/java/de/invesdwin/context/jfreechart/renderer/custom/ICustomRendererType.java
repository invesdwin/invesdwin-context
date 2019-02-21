package de.invesdwin.context.jfreechart.renderer.custom;

import org.jfree.chart.renderer.xy.XYItemRenderer;

import de.invesdwin.context.jfreechart.panel.helper.config.IRendererType;
import de.invesdwin.context.jfreechart.panel.helper.config.SeriesRendererType;
import de.invesdwin.context.jfreechart.renderer.IUpDownColorRenderer;

public interface ICustomRendererType extends IRendererType, XYItemRenderer {

    String getName();

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

}
