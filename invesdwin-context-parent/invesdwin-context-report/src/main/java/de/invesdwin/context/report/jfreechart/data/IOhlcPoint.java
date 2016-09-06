package de.invesdwin.context.report.jfreechart.data;

import de.invesdwin.util.time.fdate.FDate;

public interface IOhlcPoint {

    FDate getTime();

    double getOpen();

    double getHigh();

    double getLow();

    double getClose();

    double getVolume();

}
