package de.invesdwin.context.jfreechart.panel.helper;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public enum PriceRendererType {
    Candlesticks,
    Bars,
    Line,
    Step,
    Area;
}
