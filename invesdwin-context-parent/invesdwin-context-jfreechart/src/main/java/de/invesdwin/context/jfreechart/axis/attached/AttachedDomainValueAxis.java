package de.invesdwin.context.jfreechart.axis.attached;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.axis.ValueAxis;

import de.invesdwin.context.jfreechart.axis.AxisType;
import de.invesdwin.context.jfreechart.plot.IAxisPlot;

@NotThreadSafe
public class AttachedDomainValueAxis extends AAttachedValueAxis {

    public AttachedDomainValueAxis(final IAxisPlot plot, final int index, final ValueAxis axis) {
        super(plot, index, axis);
    }

    @Override
    public AxisType getAxisType() {
        return AxisType.DOMAIN_AXIS;
    }

}
