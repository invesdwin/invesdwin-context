package de.invesdwin.context.jfreechart.listener;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;

@Immutable
public class ChartMouseListenerSupport implements ChartMouseListener {

    @Override
    public void chartMouseClicked(final ChartMouseEvent event) {}

    @Override
    public void chartMouseMoved(final ChartMouseEvent event) {}

}
