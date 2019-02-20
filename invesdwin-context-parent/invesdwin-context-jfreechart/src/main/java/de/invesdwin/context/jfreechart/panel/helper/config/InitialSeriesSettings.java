package de.invesdwin.context.jfreechart.panel.helper.config;

import java.awt.Color;
import java.awt.Stroke;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.renderer.xy.XYItemRenderer;

import de.invesdwin.context.jfreechart.panel.helper.legend.HighlightedLegendInfo;

@NotThreadSafe
public class InitialSeriesSettings {

    private final SeriesRendererType renderer;
    private final Color color;
    private final LineStyleType lineStyleType;
    private final LineWidthType lineWidthType;

    public InitialSeriesSettings(final XYItemRenderer initialRenderer) {
        renderer = SeriesRendererType.valueOf(initialRenderer);
        color = (Color) initialRenderer.getSeriesPaint(0);
        final Stroke stroke = initialRenderer.getSeriesStroke(0);
        lineStyleType = LineStyleType.valueOf(stroke);
        lineWidthType = LineWidthType.valueOf(stroke);
    }

    public void reset(final HighlightedLegendInfo highlighted) {
        highlighted.setRenderer(renderer.newRenderer(lineStyleType, lineWidthType, color));
    }

}
