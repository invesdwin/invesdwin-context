package de.invesdwin.context.jfreechart.axis.attached;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.axis.CategoryAxis;

import de.invesdwin.context.jfreechart.axis.AxisType;
import de.invesdwin.context.jfreechart.plot.IAxisPlot;

@NotThreadSafe
public class AttachedDomainCategoryAxis extends AAttachedCategoryAxis {

    public AttachedDomainCategoryAxis(final IAxisPlot plot, final int index, final CategoryAxis axis) {
        super(plot, index, axis);
    }

    @Override
    public AxisType getAxisType() {
        return AxisType.DOMAIN_AXIS;
    }

}
