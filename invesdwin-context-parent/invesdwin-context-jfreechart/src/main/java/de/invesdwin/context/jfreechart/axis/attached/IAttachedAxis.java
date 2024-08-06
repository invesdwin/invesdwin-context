package de.invesdwin.context.jfreechart.axis.attached;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.ui.RectangleEdge;

import de.invesdwin.context.jfreechart.axis.AxisType;
import de.invesdwin.context.jfreechart.axis.IAxis;

public interface IAttachedAxis extends IAxis {

    int getIndex();

    AxisType getAxisType();

    AxisLocation getAxisLocation();

    void setAxisLocation(AxisLocation location);

    void setAxisLocation(AxisLocation location, boolean notify);

    RectangleEdge getAxisEdge();

}
