package de.invesdwin.context.jfreechart.axis.attached;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.ui.RectangleEdge;

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

    @Override
    public AxisLocation getAxisLocation() {
        return getPlot().getRangeAxisLocation(getIndex());
    }

    @Override
    public void setAxisLocation(final AxisLocation location) {
        getPlot().setRangeAxisLocation(getIndex(), location);
    }

    @Override
    public void setAxisLocation(final AxisLocation location, final boolean notify) {
        getPlot().setRangeAxisLocation(getIndex(), location, notify);
    }

    @Override
    public RectangleEdge getAxisEdge() {
        return getPlot().getRangeAxisEdge(getIndex());
    }

}
