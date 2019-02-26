package de.invesdwin.context.jfreechart.plot.dataset.basis.points;

import de.invesdwin.util.time.fdate.FDate;

public interface IOhlcPoint {

    FDate getTime();

    double getOpen();

    double getHigh();

    double getLow();

    double getClose();

    double getVolume();

}
