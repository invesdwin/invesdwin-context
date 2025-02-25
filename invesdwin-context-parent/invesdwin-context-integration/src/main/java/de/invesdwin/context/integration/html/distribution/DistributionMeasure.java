package de.invesdwin.context.integration.html.distribution;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.lang.comparator.IComparator;
import de.invesdwin.util.math.decimal.ADecimal;
import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.math.decimal.scaled.Percent;
import de.invesdwin.util.math.decimal.scaled.PercentScale;

@Immutable
public class DistributionMeasure {

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
