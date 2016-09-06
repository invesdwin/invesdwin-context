package de.invesdwin.context.report.jfreechart.visitor;

import javax.annotation.concurrent.Immutable;

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.axis.ValueAxis;

@Immutable
public class JFreeChartWeekendGapRemover extends AJFreeChartVisitor {

    @Override
    protected void processAxis(final ValueAxis axis) {
        super.processAxis(axis);
        if (axis instanceof DateAxis) {
            final DateAxis dateAxis = (DateAxis) axis;
            dateAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
        }
    }

}
