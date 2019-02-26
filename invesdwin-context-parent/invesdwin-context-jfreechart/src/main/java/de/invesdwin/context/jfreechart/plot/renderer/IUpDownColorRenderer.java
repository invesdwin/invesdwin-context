package de.invesdwin.context.jfreechart.plot.renderer;

import java.awt.Color;

import org.jfree.chart.renderer.xy.XYItemRenderer;

public interface IUpDownColorRenderer extends XYItemRenderer {

    void setUpColor(Color upColor);

    Color getUpColor();

    void setDownColor(Color downColor);

    Color getDownColor();

}
