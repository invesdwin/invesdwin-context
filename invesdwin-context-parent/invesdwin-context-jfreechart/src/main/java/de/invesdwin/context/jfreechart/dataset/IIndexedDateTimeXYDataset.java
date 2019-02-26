package de.invesdwin.context.jfreechart.dataset;

import java.util.Date;

import org.jfree.data.xy.XYDataset;

import de.invesdwin.util.time.fdate.FDate;

public interface IIndexedDateTimeXYDataset extends XYDataset {

    double getXValueAsDateTime(int series, int item);

    int getDateTimeAsItemIndex(int series, Date time);

    default int getDateTimeAsItemIndex(final int series, final FDate time) {
        return getDateTimeAsItemIndex(series, time.dateValue());
    }

}
