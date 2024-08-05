package de.invesdwin.context.jfreechart.axis.attached;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.axis.CategoryAxis;

import de.invesdwin.context.jfreechart.plot.IAxisPlot;

@NotThreadSafe
public abstract class AAttachedCategoryAxis extends AAttachedAxis {

    public AAttachedCategoryAxis(final IAxisPlot plot, final int index, final CategoryAxis axis) {
        super(plot, index, axis);
    }

    @Override
    public CategoryAxis getAxis() {
        return (CategoryAxis) super.getAxis();
    }

}