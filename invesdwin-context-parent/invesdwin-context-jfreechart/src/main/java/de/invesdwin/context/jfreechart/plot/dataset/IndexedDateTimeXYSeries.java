package de.invesdwin.context.jfreechart.plot.dataset;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.jfreechart.plot.dataset.basis.ListXYSeriesOHLC;

@NotThreadSafe
public class IndexedDateTimeXYSeries extends ListXYSeriesOHLC {

    public IndexedDateTimeXYSeries(final String seriesKey) {
        super(seriesKey);
    }

    @Override
    public Number getX(final int index) {
        return index;
    }

}
