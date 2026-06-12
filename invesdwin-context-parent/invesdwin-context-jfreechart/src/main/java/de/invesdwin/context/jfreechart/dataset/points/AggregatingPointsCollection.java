package de.invesdwin.context.jfreechart.dataset.points;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.iterable.WrapperCloseableIterable;
import de.invesdwin.util.error.FastNoSuchElementException;

/**
 * A simpler, faster and as it seems more precise algorithm to reduce points in a chart than ramer douglas peucker.
 * 
 * Based on aggregated bars collection with the difference that squares are used to draw lines.
 */
@NotThreadSafe
public class AggregatingPointsCollection<E extends IPoint> extends APointsCollection<E> {

    private static final int POINTS_IN_SQUARE = 4;

    private final int squaresMaxSize;
    private final List<Square> squares = new ArrayList<Square>();
    private Square inProgressSquare;

    /**
     * MaxSize will be rounded to an even number.
     */
    public AggregatingPointsCollection(final int maxSize) {
        final int internalMaxSize = maxSize / POINTS_IN_SQUARE;
        this.squaresMaxSize = internalMaxSize + internalMaxSize % 2;
    }

    @Override
    public int size() {
        int size = squares.size();
        if (inProgressSquare != null) {
            size++;
        }
        return size * POINTS_IN_SQUARE;
    }

    public int getMaxSize() {
        return squaresMaxSize * POINTS_IN_SQUARE;
    }

    @Override
    public boolean isEmpty() {
        return squares.isEmpty() && inProgressSquare == null;
    }

    @Override
    public boolean contains(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Iterator<Square> delegateSquares = WrapperCloseableIterable.maybeWrap(squares).iterator();
            private Square delegateInProgressSquare = inProgressSquare;
            private Iterator<E> curPointsIterator;

            @Override
            public boolean hasNext() {
                return (curPointsIterator != null && curPointsIterator.hasNext()) || delegateSquares.hasNext()
                        || delegateInProgressSquare != null;
            }

            @Override
            public E next() {
                if (curPointsIterator != null && !curPointsIterator.hasNext()) {
                    curPointsIterator = null;
                }
                if (curPointsIterator == null) {
                    if (delegateSquares.hasNext()) {
                        curPointsIterator = delegateSquares.next().getPoints().iterator();
                    } else if (delegateInProgressSquare != null) {
                        final Square ret = delegateInProgressSquare;
                        delegateInProgressSquare = null;
                        curPointsIterator = ret.getPoints().iterator();
                    } else {
                        throw FastNoSuchElementException
                                .getInstance("AggregatingPointsCollection: Iterator is exhausted");
                    }
                }
                return curPointsIterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Object[] toArray() {
        final List<IPoint> allPoints = new ArrayList<IPoint>();
        for (int i = 0; i < squares.size(); i++) {
            allPoints.addAll(squares.get(i).getPoints());
        }
        if (inProgressSquare != null) {
            allPoints.addAll(inProgressSquare.getPoints());
        }
        return allPoints.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        final List<IPoint> allPoints = new ArrayList<IPoint>();
        for (int i = 0; i < squares.size(); i++) {
            allPoints.addAll(squares.get(i).getPoints());
        }
        if (inProgressSquare != null) {
            allPoints.addAll(inProgressSquare.getPoints());
        }
        return allPoints.toArray(a);
    }

    @Override
    public boolean add(final E e) {
        aggregateInProgressSquare(e);
        if (inProgressSquare == null) {
            aggregateSquares();
        }
        return true;
    }

    private void aggregateInProgressSquare(final E newPoint) {
        // Look at the tail of the list to find the current minimum aggregation count
        final int currentMinAggregationCount = squares.isEmpty() ? 1
                : squares.get(squares.size() - 1).getAggregationCount();

        if (inProgressSquare == null) {
            inProgressSquare = new Square(newPoint);
        } else {
            inProgressSquare.add(newPoint);
        }

        final int size = inProgressSquare.getSize();
        inProgressSquare.aggregationCount = AggregatingOhlcPointsCollection.newAggregationCount(size);
        if (inProgressSquare.aggregationCount >= currentMinAggregationCount) {
            squares.add(inProgressSquare);
            inProgressSquare = null;
        }
    }

    private void aggregateSquares() {
        // Continue aggregating until the collection size is within limits
        while (squares.size() >= squaresMaxSize) {

            // Due to strictly descending order, the last element is guaranteed
            // to hold the lowest aggregation count layer.
            final int minAggregationCount = squares.get(squares.size() - 1).getAggregationCount();
            int mergeIndex = -1;

            // Scan backwards to find the LEFT-MOST pair matching the minAggregationCount.
            // We look for the first occurrence from the left, which means the last pair
            // we encounter while walking backwards.
            for (int i = squares.size() - 1; i >= 1; i--) {
                if (squares.get(i).getAggregationCount() == minAggregationCount
                        && squares.get(i - 1).getAggregationCount() == minAggregationCount) {
                    mergeIndex = i - 1; // Mark the chronologically earlier square
                }
            }

            // If a valid pair within the smallest layer was found, merge them
            if (mergeIndex != -1) {
                final Square curPoint = squares.get(mergeIndex);
                final int nextIndex = mergeIndex + 1;
                final Square nextPoint = squares.get(nextIndex);

                // Merge current into previous to preserve time order
                curPoint.add(nextPoint);
                curPoint.aggregationCount++;

                // Remove the redundant right-side square
                squares.remove(nextIndex);
            } else {
                // Guard rail: If no pairs exist in the lowest layer, we must break
                // to prevent an infinite loop (e.g., all elements have unique counts)
                break;
            }
        }
    }

    @Override
    public void clear() {
        squares.clear();
        inProgressSquare = null;
    }

    private final class Square {
        private final E start;
        private E end;
        private E high;
        private E low;
        private int aggregationCount;
        private int size;

        Square(final E point) {
            this.start = point;
            this.high = point;
            this.low = point;
            this.end = point;
            this.aggregationCount = 1;
            this.size = 1;
        }

        public List<E> getPoints() {
            return Arrays.asList(start, low, high, end);
        }

        public void add(final E point) {
            internalAdd(point);
            size++;
        }

        public void add(final Square square) {
            if (square.high.getY() > this.high.getY()) {
                this.high = square.high;
            }
            if (square.low.getY() < this.low.getY()) {
                this.low = square.low;
            }
            this.end = square.end;
            this.size += square.size;
        }

        private void internalAdd(final E point) {
            if (point.getY() > high.getY()) {
                high = point;
            }
            if (point.getY() < low.getY()) {
                low = point;
            }
            end = point;
        }

        public int getAggregationCount() {
            return aggregationCount;
        }

        public int getSize() {
            return size;
        }
    }
}