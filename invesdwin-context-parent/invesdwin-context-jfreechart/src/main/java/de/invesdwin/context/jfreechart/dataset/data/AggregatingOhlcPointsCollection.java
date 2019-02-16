package de.invesdwin.context.jfreechart.dataset.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.lang.ADelegateComparator;
import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FDates;

@NotThreadSafe
public class AggregatingOhlcPointsCollection<E extends IOhlcPoint> extends APointsCollection<E> {

    private static final ADelegateComparator<IOhlcPoint> OHCL_COMPARATOR = new ADelegateComparator<IOhlcPoint>() {
        @Override
        protected Comparable<?> getCompareCriteria(final IOhlcPoint e) {
            return e.getTime();
        }
    };

    private final IOhlcPointFactory<E> factory;
    private final int maxSize;

    private List<IOhlcPoint> ohlcPoints = new ArrayList<IOhlcPoint>();
    /**
     * Represents how many ohlcPoints are aggregated in one ohlcPoint.
     */
    private int ohlcPointsAggregationCount = 1;

    private IOhlcPoint inProgressOhlcPoint;
    private int inProgressOhlcPointAggregationCount;

    /**
     * MaxSize will be rounded to an even number.
     */
    public AggregatingOhlcPointsCollection(final IOhlcPointFactory<E> factory, final int maxSize) {
        this.factory = factory;
        this.maxSize = maxSize + maxSize % 2;
    }

    @Override
    public int size() {
        int size = ohlcPoints.size();
        if (inProgressOhlcPoint != null) {
            size++;
        }
        return size;
    }

    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public boolean isEmpty() {
        return ohlcPoints.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private final Iterator<IOhlcPoint> delegateOhlcPoints = ohlcPoints.iterator();
            private IOhlcPoint delegateInProgressOhlcPoint = inProgressOhlcPoint;

            @Override
            public boolean hasNext() {
                return delegateOhlcPoints.hasNext() || delegateInProgressOhlcPoint != null;
            }

            @Override
            public E next() {
                if (delegateOhlcPoints.hasNext()) {
                    return factory.newCopy(delegateOhlcPoints.next());
                } else if (delegateInProgressOhlcPoint != null) {
                    final IOhlcPoint ret = delegateInProgressOhlcPoint;
                    delegateInProgressOhlcPoint = null;
                    return factory.newCopy(ret);
                } else {
                    throw new FastNoSuchElementException("AggregatingOhlcPointsCollection: delegateOhlcPoints is null");
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Object[] toArray() {
        final List<E> allOhlcPoints = new ArrayList<E>();
        for (final IOhlcPoint p : ohlcPoints) {
            allOhlcPoints.add(factory.newCopy(p));
        }
        if (inProgressOhlcPoint != null) {
            allOhlcPoints.add(factory.newCopy(inProgressOhlcPoint));
        }
        return allOhlcPoints.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        final List<E> allOhlcPoints = new ArrayList<E>();
        for (final IOhlcPoint p : ohlcPoints) {
            allOhlcPoints.add(factory.newCopy(p));
        }
        if (inProgressOhlcPoint != null) {
            allOhlcPoints.add(factory.newCopy(inProgressOhlcPoint));
        }
        return allOhlcPoints.toArray(a);
    }

    @Override
    public boolean add(final E e) {
        aggregateInProgressOhlcPoint(e);
        if (ohlcPoints.size() >= getMaxSize() && inProgressOhlcPoint == null) {
            aggregateOhlcPoints();
        }
        return true;
    }

    private void aggregateInProgressOhlcPoint(final IOhlcPoint newOhlcPoint) {
        if (inProgressOhlcPoint == null) {
            if (ohlcPointsAggregationCount <= 1) {
                ohlcPoints.add(newOhlcPoint);
            } else {
                inProgressOhlcPoint = newOhlcPoint;
            }
        } else {
            final AggregatedOhlcPoint aggregatedOhlcPoint = new AggregatedOhlcPoint(inProgressOhlcPoint, newOhlcPoint);
            inProgressOhlcPoint = aggregatedOhlcPoint;
            inProgressOhlcPointAggregationCount++;
            if (inProgressOhlcPointAggregationCount >= ohlcPointsAggregationCount) {
                ohlcPoints.add(inProgressOhlcPoint);
                inProgressOhlcPoint = null;
                inProgressOhlcPointAggregationCount = 0;
            }
        }
    }

    private void aggregateOhlcPoints() {
        Assertions.assertThat(ohlcPoints.size() % 2)
                .as("Size [%s] needs to be a factor of two when aggregating!", ohlcPoints.size())
                .isZero();
        final List<IOhlcPoint> aggregatedOhlcPoints = new ArrayList<IOhlcPoint>(ohlcPoints.size());
        for (int i = 0; i < ohlcPoints.size(); i += 2) {
            final IOhlcPoint firstOhlcPoint = ohlcPoints.get(i);
            final IOhlcPoint secondOhlcPoint = ohlcPoints.get(i + 1);
            final AggregatedOhlcPoint aggregatedOhlcPoint = new AggregatedOhlcPoint(firstOhlcPoint, secondOhlcPoint);
            aggregatedOhlcPoints.add(aggregatedOhlcPoint);
        }
        ohlcPoints = aggregatedOhlcPoints;
        ohlcPointsAggregationCount *= 2;
        OHCL_COMPARATOR.assertOrder(ohlcPoints, true);
    }

    @Override
    public void clear() {
        ohlcPoints.clear();
        ohlcPointsAggregationCount = 0;
        inProgressOhlcPoint = null;
        inProgressOhlcPointAggregationCount = 0;
    }

    private static class AggregatedOhlcPoint implements IOhlcPoint {

        private final FDate time;
        private final double open;
        private final double high;
        private final double low;
        private final double close;
        private final double volume;

        AggregatedOhlcPoint(final IOhlcPoint first, final IOhlcPoint second) {
            this.time = FDates.min(first.getTime(), second.getTime());
            this.open = first.getOpen();
            this.high = Math.max(first.getHigh(), second.getHigh());
            this.low = Math.min(first.getLow(), second.getLow());
            this.close = second.getClose();
            this.volume = first.getVolume() + second.getVolume();
        }

        @Override
        public FDate getTime() {
            return time;
        }

        @Override
        public double getOpen() {
            return open;
        }

        @Override
        public double getHigh() {
            return high;
        }

        @Override
        public double getLow() {
            return low;
        }

        @Override
        public double getClose() {
            return close;
        }

        @Override
        public double getVolume() {
            return volume;
        }

    }

}
