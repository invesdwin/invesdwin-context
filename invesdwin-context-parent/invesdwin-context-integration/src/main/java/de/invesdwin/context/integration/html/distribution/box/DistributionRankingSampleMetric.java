package de.invesdwin.context.integration.html.distribution.box;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.context.integration.html.distribution.DistributionMeasure;
import de.invesdwin.util.math.statistics.distribution.ADistributionComparator;

@NotThreadSafe
public class DistributionRankingSampleMetric {

    private final List<DistributionMeasure> samples;
    private final ADistributionComparator<DistributionMeasure> comparator;

    public DistributionRankingSampleMetric(final List<DistributionMeasure> samples,
            final ADistributionComparator<DistributionMeasure> comparator) {
        this.samples = new ArrayList<>(samples);
        this.comparator = comparator;
        this.comparator.sort(this.samples);
    }

    public List<DistributionMeasure> getSamples() {
        return samples;
    }

    public ADistributionComparator<DistributionMeasure> getComparator() {
        return comparator;
    }

}