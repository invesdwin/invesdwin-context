package de.invesdwin.context.jfreechart.panel.helper;

import java.awt.event.KeyEvent;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.data.Range;

import de.invesdwin.context.jfreechart.panel.InteractiveChartPanel;
import de.invesdwin.util.math.Doubles;

@NotThreadSafe
public class PlotPanHelper {

    private static final double DEFAULT_SCROLL_FACTOR = 0.05D;
    private static final double FASTER_SCROLL_FACTOR = DEFAULT_SCROLL_FACTOR * 3D;
    private double scrollFactor = DEFAULT_SCROLL_FACTOR;

    private final InteractiveChartPanel chartPanel;

    public PlotPanHelper(final InteractiveChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }

    public void panLeft() {
        final Range range = chartPanel.getDomainAxis().getRange();
        final double length = range.getLength();
        final double newLowerBound = Doubles.max(range.getLowerBound() - length * scrollFactor,
                0 - chartPanel.getAllowedRangeGap());
        final Range newRange = new Range(newLowerBound, newLowerBound + length);
        chartPanel.getDomainAxis().setRange(newRange);
        chartPanel.update();
    }

    public void panRight() {
        final Range range = chartPanel.getDomainAxis().getRange();
        final double length = range.getLength();
        final double newUpperBound = Doubles.min(range.getUpperBound() + length * scrollFactor,
                chartPanel.getDataset().getItemCount(0) + chartPanel.getAllowedRangeGap());
        final Range newRange = new Range(newUpperBound - length, newUpperBound);
        chartPanel.getDomainAxis().setRange(newRange);
        chartPanel.update();
    }

    public void keyPressed(final KeyEvent e) {
        if (e.isControlDown()) {
            scrollFactor = FASTER_SCROLL_FACTOR;
        } else {
            scrollFactor = DEFAULT_SCROLL_FACTOR;
        }
    }

    public void keyReleased(final KeyEvent e) {
        scrollFactor = DEFAULT_SCROLL_FACTOR;
    }

}
