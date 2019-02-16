package de.invesdwin.context.jfreechart.dataset;

import org.jfree.data.xy.XYDataset;

public interface IIndexedDateTimeXYDataset extends XYDataset {

    double getXValueAsDateTime(int series, int item);

}
