package de.invesdwin.context.jfreechart.dataset;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.jfreechart.dataset.basis.ListXYSeries;

@NotThreadSafe
public class IndexedDateTimeXYSeries extends ListXYSeries {

    public IndexedDateTimeXYSeries(final Comparable<?> key) {
        super(key);
    }

    @Override
    public Number getX(final int index) {
        return index;
    }

}
