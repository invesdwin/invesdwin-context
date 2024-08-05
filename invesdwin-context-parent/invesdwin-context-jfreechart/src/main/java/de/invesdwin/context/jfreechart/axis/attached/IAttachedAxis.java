package de.invesdwin.context.jfreechart.axis.attached;

import de.invesdwin.context.jfreechart.axis.AxisType;
import de.invesdwin.context.jfreechart.axis.IAxis;

public interface IAttachedAxis extends IAxis {

    int getIndex();

    AxisType getAxisType();

}
