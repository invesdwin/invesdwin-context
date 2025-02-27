package de.invesdwin.context.integration.html.distribution;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;
import de.invesdwin.util.math.statistics.distribution.ADistributionComparator;
import de.invesdwin.util.math.statistics.distribution.AKolmogorovSmirnovTestComparator;
import de.invesdwin.util.math.statistics.distribution.ATTestComparator;
import de.invesdwin.util.math.statistics.distribution.AZScoreComparator;

@Immutable
public class DistributionMeasure {

    public static final AZScoreComparator<DistributionMeasure> COMPARATOR_ZSCORE = new AZScoreComparator<DistributionMeasure>() {

        @Override
        protected boolean isHigherBetter(final DistributionMeasure element) {
            return element.isHigherBetter();
        }

        @Override
        protected double getStdev(final DistributionMeasure element) {
            return element.getStdev().doubleValue();
        }

        @Override
        protected int getCount(final DistributionMeasure element) {
            return element.getCount();
        }

        @Override
        protected double getAvg(final DistributionMeasure element) {
            return element.getAvg().doubleValue();
        }
    };

    public static final ATTestComparator<DistributionMeasure> COMPARATOR_TTEST = new ATTestComparator<DistributionMeasure>() {

        @Override
        protected boolean isHigherBetter(final DistributionMeasure element) {
            return element.isHigherBetter();
        }

        @Override
        protected int getCount(final DistributionMeasure element) {
            return element.getCount();
        }

        @Override
        protected double[] getValues(final DistributionMeasure element, final int maxCount) {
            return element.getValuesAsDoubleArray(maxCount);
        }
    };

    public static final AKolmogorovSmirnovTestComparator<DistributionMeasure> COMPARATOR_KSTEST = new AKolmogorovSmirnovTestComparator<DistributionMeasure>() {

        @Override
        protected boolean isHigherBetter(final DistributionMeasure element) {
            return element.isHigherBetter();
        }

        @Override
        protected double[] getValues(final DistributionMeasure element) {
            return element.getValuesAsDoubleArray();
        }
    };

    @SuppressWarnings("unchecked")
    public static final List<ADistributionComparator<DistributionMeasure>> COMPARATORS = Arrays
            .asList(COMPARATOR_ZSCORE, COMPARATOR_TTEST, COMPARATOR_KSTEST);

    private final String sampleName;
    private final String measureName;
    private final List<Decimal> values;
    private final boolean higherBetter;
    private final int decimalPlaces;
    private final IComparator<ADecimal<?>> comparator;

    public DistributionMeasure(final String sampleName, final String measureName, final List<Decimal> values,
            final boolean higherBetter, final int decimalPlaces) {
        this.sampleName = sampleName;
        this.measureName = measureName;
        this.values = values;
        this.higherBetter = higherBetter;
        this.decimalPlaces = decimalPlaces;
        this.comparator = Decimal.COMPARATOR.asDescending(higherBetter);
    }

    public void sort() {
        comparator.asNotNullSafe().sort(values);
    }

    public String getSampleName() {
        return sampleName;
    }

    public String getMeasureName() {
        return measureName;
    }

    public List<Decimal> getValues() {
        return values;
    }

    public double[] getValuesAsDoubleArray() {
        final double[] values = new double[getCount()];
        for (int i = 0; i < values.length; i++) {
            values[i] = getValues().get(i).doubleValue();
        }
        return values;
    }

    public double[] getValuesAsDoubleArray(final int maxCount) {
        final double[] values = new double[maxCount];
        for (int i = 0; i < maxCount; i++) {
            values[i] = getValues().get(i).doubleValue();
        }
        return values;
    }

    public boolean isHigherBetter() {
        return higherBetter;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public Decimal getConfidenceLevelValue(final Percent confidenceLevel) {
        Assertions.assertThat(confidenceLevel)
                .isGreaterThanOrEqualTo(Percent.ZERO_PERCENT)
                .isLessThanOrEqualTo(Percent.ONE_HUNDRED_PERCENT);
        final Decimal confidenceLevelResult = values.get(Decimal.valueOf(values.size() - 1)
                .divide(Decimal.ONE_HUNDRED)
                .multiply(confidenceLevel.getValue(PercentScale.PERCENT))
                .intValue());
        return confidenceLevelResult;
    }

    public Decimal getStart() {
        if (higherBetter) {
            return getLast();
        } else {
            return getFirst();
        }
    }

    public Decimal getEnd() {
        if (higherBetter) {
            return getFirst();
        } else {
            return getLast();
        }
    }

    private Decimal getFirst() {
        return values.get(0);
    }

    private Decimal getLast() {
        return values.get(values.size() - 1);
    }

    public int getCount() {
        return values.size();
    }

    public Decimal getMin() {
        return Decimal.valueOf(getValues()).min();
    }

    public Decimal getMedian() {
        return Decimal.valueOf(getValues()).median();
    }

    public Decimal getMax() {
        return Decimal.valueOf(getValues()).max();
    }

    public Decimal getAvg() {
        return Decimal.valueOf(getValues()).avg();
    }

    public Decimal getStdev() {
        return Decimal.valueOf(getValues()).sampleStandardDeviation();
    }

    public Decimal getRange() {
        return getEnd().subtract(getStart()).abs();
    }

    public Decimal getIQRange() {
        return getQuartile1().subtract(getQuartile3()).abs();
    }

    public Decimal getQuartile1() {
        return getConfidenceLevelValue(Percent.TWENTYFIVE_PERCENT);
    }

    public Decimal getQuartile3() {
        return getConfidenceLevelValue(Percent.SEVENTYFIVE_PERCENT);
    }

}
