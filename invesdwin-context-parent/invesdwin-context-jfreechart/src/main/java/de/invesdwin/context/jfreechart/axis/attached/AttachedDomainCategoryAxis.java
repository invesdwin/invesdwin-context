package de.invesdwin.context.jfreechart.axis.attached;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.ui.RectangleEdge;

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

    @Override
    public AxisLocation getAxisLocation() {
        return getPlot().getDomainAxisLocation(getIndex());
    }

    @Override
    public void setAxisLocation(final AxisLocation location) {
        getPlot().setDomainAxisLocation(getIndex(), location);
    }

    @Override
    public void setAxisLocation(final AxisLocation location, final boolean notify) {
        getPlot().setDomainAxisLocation(getIndex(), location, notify);
    }

    @Override
    public RectangleEdge getAxisEdge() {
        return getPlot().getDomainAxisEdge(getIndex());
    }

}
