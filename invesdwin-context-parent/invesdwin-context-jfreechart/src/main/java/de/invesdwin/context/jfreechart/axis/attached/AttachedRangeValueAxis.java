package de.invesdwin.context.jfreechart.axis.attached;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.axis.ValueAxis;

import de.invesdwin.context.jfreechart.axis.AxisType;
import de.invesdwin.context.jfreechart.plot.IAxisPlot;

@NotThreadSafe
public class AttachedRangeValueAxis extends AAttachedValueAxis {

    public AttachedRangeValueAxis(final IAxisPlot plot, final int index, final ValueAxis axis) {
        super(plot, index, axis);
    }

    @Override
    public AxisType getAxisType() {
        return AxisType.RANGE_AXIS;
    }

}
