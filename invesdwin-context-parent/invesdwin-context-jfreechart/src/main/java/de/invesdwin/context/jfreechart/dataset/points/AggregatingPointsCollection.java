package de.invesdwin.context.jfreechart.dataset.points;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
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

    private List<Square> squares = new ArrayList<Square>();
    /**
     * Represents how many squares are aggregated in one square.
     */
    private int squaresAggregationCount = 1;

    private Square inProgressSquare;
    private int inProgressSquareAggregationCount;

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
        return squares.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private final Iterator<Square> delegateSquares = squares.iterator();
            private Square delegateInProgressSquare = inProgressSquare;
            private Iterator<E> curPointsIterator;

            @Override
            public boolean hasNext() {
                return curPointsIterator != null && curPointsIterator.hasNext() || delegateSquares.hasNext()
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
                                .getInstance("AggregatingPointsCollection: delegateInProgressSquare is null");
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
        for (final Square c : squares) {
            allPoints.addAll(c.getPoints());
        }
        if (inProgressSquare != null) {
            allPoints.addAll(inProgressSquare.getPoints());
        }
        return allPoints.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        final List<IPoint> allPoints = new ArrayList<IPoint>();
        for (final Square c : squares) {
            allPoints.addAll(c.getPoints());
        }
        if (inProgressSquare != null) {
            allPoints.addAll(inProgressSquare.getPoints());
        }
        return allPoints.toArray(a);
    }

    @Override
    public boolean add(final E e) {
        aggregateInProgressSquare(e);
        if (squares.size() >= squaresMaxSize && inProgressSquare == null) {
            aggregateSquares();
        }
        return true;
    }

    private void aggregateInProgressSquare(final E newPoint) {
        if (inProgressSquare == null) {
            if (squaresAggregationCount <= 1) {
                final Square square = new Square();
                square.add(newPoint);
                squares.add(square);
            } else {
                final Square square = new Square();
                square.add(newPoint);
                inProgressSquare = square;
            }
        } else {
            inProgressSquare.add(newPoint);
            inProgressSquareAggregationCount++;
            if (inProgressSquareAggregationCount >= squaresAggregationCount) {
                squares.add(inProgressSquare);
                inProgressSquare = null;
                inProgressSquareAggregationCount = 0;
            }
        }
    }

    private void aggregateSquares() {
        Assertions.assertThat(squares.size() % 2)
                .as("Size [%s] needs to be a factor of two when aggregating!", squares.size())
                .isZero();
        final List<Square> aggregatedSquares = new ArrayList<Square>(squares.size());
        for (int i = 0; i < squares.size(); i += 2) {
            final Square firstSquare = squares.get(i);
            final Square secondSquare = squares.get(i + 1);
            firstSquare.addAll(secondSquare.getPoints());
            aggregatedSquares.add(firstSquare);
        }
        squares = aggregatedSquares;
        squaresAggregationCount *= 2;
    }

    @Override
    public void clear() {
        squares.clear();
        squaresAggregationCount = 0;
        inProgressSquare = null;
        inProgressSquareAggregationCount = 0;
    }

    private class Square {
        private E start;
        private E end;
        private E high;
        private E low;

        public List<E> getPoints() {
            return Arrays.asList(start, low, high, end);
        }

        public void add(final E point) {
            if (start == null) {
                start = point;
            }
            if (high == null || point.getY() > high.getY()) {
                high = point;
            }
            if (low == null || point.getY() < low.getY()) {
                low = point;
            }
            end = point;
        }

        public void addAll(final List<E> points) {
            for (final E p : points) {
                add(p);
            }
        }

    }

}
