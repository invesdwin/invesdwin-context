package de.invesdwin.context.jfreechart.dataset.points;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.FastNoSuchElementException;
import de.invesdwin.util.math.Doubles;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

@NotThreadSafe
public class AggregatingOhlcPointsCollection<E extends IOhlcPoint> extends APointsCollection<E> {

    private final IOhlcPointFactory<E> factory;
    private final int maxSize;
    private final List<AggregatedOhlcPoint> ohlcPoints = new ArrayList<AggregatedOhlcPoint>();
    private AggregatedOhlcPoint inProgressOhlcPoint;

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
        return ohlcPoints.isEmpty() && inProgressOhlcPoint == null;
    }

    @Override
    public boolean contains(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Iterator<AggregatedOhlcPoint> delegateOhlcPoints = ohlcPoints.iterator();
            private AggregatedOhlcPoint delegateInProgressOhlcPoint = inProgressOhlcPoint;

            @Override
            public boolean hasNext() {
                return delegateOhlcPoints.hasNext() || delegateInProgressOhlcPoint != null;
            }

            @Override
            public E next() {
                if (delegateOhlcPoints.hasNext()) {
                    return factory.newCopy(delegateOhlcPoints.next());
                } else if (delegateInProgressOhlcPoint != null) {
                    final AggregatedOhlcPoint ret = delegateInProgressOhlcPoint;
                    delegateInProgressOhlcPoint = null;
                    return factory.newCopy(ret);
                } else {
                    throw FastNoSuchElementException
                            .getInstance("AggregatingOhlcPointsCollection: Iterator is exhausted");
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
        for (int i = 0; i < ohlcPoints.size(); i++) {
            allOhlcPoints.add(factory.newCopy(ohlcPoints.get(i)));
        }
        if (inProgressOhlcPoint != null) {
            allOhlcPoints.add(factory.newCopy(inProgressOhlcPoint));
        }
        return allOhlcPoints.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        final List<E> allOhlcPoints = new ArrayList<E>();
        for (int i = 0; i < ohlcPoints.size(); i++) {
            allOhlcPoints.add(factory.newCopy(ohlcPoints.get(i)));
        }
        if (inProgressOhlcPoint != null) {
            allOhlcPoints.add(factory.newCopy(inProgressOhlcPoint));
        }
        return allOhlcPoints.toArray(a);
    }

    @Override
    public boolean add(final E e) {
        aggregateInProgressOhlcPoint(e);
        if (inProgressOhlcPoint == null) {
            aggregateOhlcPoints();
        }
        return true;
    }

    private void aggregateInProgressOhlcPoint(final IOhlcPoint newOhlcPoint) {
        // Look at the tail of the list to find the current minimum aggregation count
        final int currentMinAggregationCount = ohlcPoints.isEmpty() ? 1
                : ohlcPoints.get(ohlcPoints.size() - 1).getAggregationCount();

        if (inProgressOhlcPoint == null) {
            inProgressOhlcPoint = new AggregatedOhlcPoint(newOhlcPoint);
        } else {
            inProgressOhlcPoint.add(newOhlcPoint);
        }
        final int size = inProgressOhlcPoint.getSize();
        inProgressOhlcPoint.aggregationCount = newAggregationCount(size);
        if (inProgressOhlcPoint.aggregationCount >= currentMinAggregationCount) {
            ohlcPoints.add(inProgressOhlcPoint);
            inProgressOhlcPoint = null;
        }
    }

    //    public static void main(final String[] args) {
    //        for (int size = 0; size <= 16; size++) {
    //            final int layer = newAggregationCount(size);
    //            // The exact, pure power-of-two size required to fully fill this layer
    //            final int perfectSizeForLayer = 1 << (layer - 1);
    //
    //            System. out. println("Size: " + size
    //                    + " => L" + layer
    //                    + " (Perfect Layer Size: " + perfectSizeForLayer + ")");
    //        }
    //    }
    //    Size: 0 => L0 (Perfect Layer Size: -2147483648)
    //    Size: 1 => L1 (Perfect Layer Size: 1)
    //    Size: 2 => L2 (Perfect Layer Size: 2)
    //    Size: 3 => L2 (Perfect Layer Size: 2)
    //    Size: 4 => L3 (Perfect Layer Size: 4)
    //    Size: 5 => L3 (Perfect Layer Size: 4)
    //    Size: 6 => L3 (Perfect Layer Size: 4)
    //    Size: 7 => L3 (Perfect Layer Size: 4)
    //    Size: 8 => L4 (Perfect Layer Size: 8)
    //    Size: 9 => L4 (Perfect Layer Size: 8)
    //    Size: 10 => L4 (Perfect Layer Size: 8)
    //    Size: 11 => L4 (Perfect Layer Size: 8)
    //    Size: 12 => L4 (Perfect Layer Size: 8)
    //    Size: 13 => L4 (Perfect Layer Size: 8)
    //    Size: 14 => L4 (Perfect Layer Size: 8)
    //    Size: 15 => L4 (Perfect Layer Size: 8)
    //    Size: 16 => L5 (Perfect Layer Size: 16)
    public static int newAggregationCount(final int size) {
        if (size <= 0) {
            return 0;
        }
        // Standard floor log2(size) + 1 calculation
        return 32 - Integer.numberOfLeadingZeros(size);
    }

    private void aggregateOhlcPoints() {
        // Continue aggregating until the collection size is within limits
        while (ohlcPoints.size() >= getMaxSize()) {

            // Due to strictly descending order, the last element is guaranteed
            // to hold the lowest aggregation count layer.
            final int minAggregationCount = ohlcPoints.get(ohlcPoints.size() - 1).getAggregationCount();
            int mergeIndex = -1;

            // Scan backwards to find the LEFT-MOST pair matching the minAggregationCount.
            // We look for the first occurrence from the left, which means the last pair
            // we encounter while walking backwards.
            for (int i = ohlcPoints.size() - 1; i >= 1; i--) {
                if (ohlcPoints.get(i).getAggregationCount() == minAggregationCount
                        && ohlcPoints.get(i - 1).getAggregationCount() == minAggregationCount) {
                    mergeIndex = i - 1; // Mark the chronologically earlier point index
                }
            }

            // If a valid pair within the smallest layer was found, merge them
            if (mergeIndex != -1) {
                final AggregatedOhlcPoint curPoint = ohlcPoints.get(mergeIndex);
                final int nextIndex = mergeIndex + 1;
                final AggregatedOhlcPoint nextPoint = ohlcPoints.get(nextIndex);

                // Construct the combined OHLC candle. The new candle layer increases
                // to (current layer + 1).
                curPoint.add(nextPoint);
                curPoint.aggregationCount += 1;

                // Update the list in-place
                ohlcPoints.remove(nextIndex);
            } else {
                // Guard rail: If no pairs exist in the lowest layer, we must break
                // to prevent an infinite loop (e.g., all elements have unique counts)
                break;
            }
        }
    }

    @Override
    public void clear() {
        ohlcPoints.clear();
        inProgressOhlcPoint = null;
    }

    private static class AggregatedOhlcPoint implements IOhlcPoint {
        private FDate time;
        private final double open;
        private double high;
        private double low;
        private double close;
        private double volume;
        private int aggregationCount;
        private int size;

        AggregatedOhlcPoint(final IOhlcPoint single) {
            this.time = single.getTime();
            this.open = single.getOpen();
            this.high = single.getHigh();
            this.low = single.getLow();
            this.close = single.getClose();
            this.volume = single.getVolume();
            this.aggregationCount = 1;
            this.size = 1;
        }

        public void add(final IOhlcPoint point) {
            internalAdd(point);
            this.size++;
        }

        public void add(final AggregatedOhlcPoint point) {
            internalAdd(point);
            size += point.size;
        }

        public void internalAdd(final IOhlcPoint point) {
            this.time = FDates.min(time, point.getTime());
            this.high = Doubles.max(high, point.getHigh());
            this.low = Doubles.min(low, point.getLow());
            this.close = point.getClose();
            this.volume = volume + point.getVolume();
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

        public int getAggregationCount() {
            return aggregationCount;
        }

        public int getSize() {
            return size;
        }
    }
}