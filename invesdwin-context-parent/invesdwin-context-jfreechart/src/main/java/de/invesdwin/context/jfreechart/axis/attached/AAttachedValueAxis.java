package de.invesdwin.context.jfreechart.axis.attached;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.axis.ValueAxis;

import de.invesdwin.context.jfreechart.plot.IAxisPlot;

@NotThreadSafe
public abstract class AAttachedValueAxis extends AAttachedAxis {

    public AAttachedValueAxis(final IAxisPlot plot, final int index, final ValueAxis axis) {
        super(plot, index, axis);
    }

    @Override
    public ValueAxis getAxis() {
        return (ValueAxis) super.getAxis();
    }

}
