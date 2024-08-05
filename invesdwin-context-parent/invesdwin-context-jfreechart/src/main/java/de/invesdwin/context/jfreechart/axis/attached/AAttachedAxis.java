package de.invesdwin.context.jfreechart.axis.attached;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.axis.Axis;

import de.invesdwin.context.jfreechart.axis.AAxis;
import de.invesdwin.context.jfreechart.plot.IAxisPlot;

@NotThreadSafe
public abstract class AAttachedAxis extends AAxis implements IAttachedAxis {

    private final int index;

    public AAttachedAxis(final IAxisPlot plot, final int index, final Axis axis) {
        super(plot, axis);
        this.index = index;
    }

    @Override
    public final int getIndex() {
        return index;
    }

}
