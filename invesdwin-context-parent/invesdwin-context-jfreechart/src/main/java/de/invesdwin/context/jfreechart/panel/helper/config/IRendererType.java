package de.invesdwin.context.jfreechart.panel.helper.config;

import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;

public interface IRendererType {

    SeriesRendererType getSeriesRendererType();

    boolean isLineStyleConfigurable();

    boolean isLineWidthConfigurable();

    /**
     * When this returns true, the renderer needs to implements IUpDownColorRenderer
     */
    boolean isUpColorConfigurable();

    /**
     * When this returns true, the renderer needs to implements IUpDownColorRenderer
     */
    boolean isDownColorConfigurable();

    boolean isPriceColorConfigurable();

    void reset(HighlightedLegendInfo highlighted, InitialSeriesSettings initialSettings);

}
