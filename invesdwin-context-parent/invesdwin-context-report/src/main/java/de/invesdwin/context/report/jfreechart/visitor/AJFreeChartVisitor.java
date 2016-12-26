package de.invesdwin.context.report.jfreechart.visitor;

import java.awt.Font;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeCategoryPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;

@NotThreadSafe
public abstract class AJFreeChartVisitor {

    public final void process(final JFreeChart chart) {
        processChart(chart);
    }

    protected void processChart(final JFreeChart chart) {
        processTitle(chart.getTitle());
        for (int i = 0; i < chart.getSubtitleCount(); i++) {
            final Title subtitle = chart.getSubtitle(i);
            processTitle(subtitle);
        }
        processTitle(chart.getLegend());

        processPlotRecursive(chart.getPlot(), new HashSet<Integer>());
    }

    protected Font processFont(final Font font) {
        return font;
    }

    protected void processTitle(final Title title) {
        if (title instanceof TextTitle) {
            final TextTitle cTitle = (TextTitle) title;
            cTitle.setFont(processFont(cTitle.getFont()));
        } else if (title instanceof LegendTitle) {
            final LegendTitle cTitle = (LegendTitle) title;
            cTitle.setItemFont(processFont(cTitle.getItemFont()));
        } else {
            throw new IllegalArgumentException("Unknown " + Title.class + " type: " + title.getClass());
        }
    }

    protected void processPlotRecursive(final Plot plot, final Set<Integer> duplicateAxisFilter) {
        if (plot instanceof XYPlot) {
            final XYPlot cPlot = (XYPlot) plot;
            processXYPlot(duplicateAxisFilter, cPlot);
        }
        if (plot instanceof CategoryPlot) {
            final CategoryPlot cPlot = (CategoryPlot) plot;
            processCategoryPlot(duplicateAxisFilter, cPlot);
        }

        //recurse
        if (plot instanceof CombinedDomainCategoryPlot) {
            final CombinedDomainCategoryPlot cPlot = (CombinedDomainCategoryPlot) plot;
            for (final Object subPlotObj : cPlot.getSubplots()) {
                final Plot subPlot = (Plot) subPlotObj;
                processPlotRecursive(subPlot, duplicateAxisFilter);
            }
        } else if (plot instanceof CombinedDomainXYPlot) {
            final CombinedDomainXYPlot cPlot = (CombinedDomainXYPlot) plot;
            for (final Object subPlotObj : cPlot.getSubplots()) {
                final Plot subPlot = (Plot) subPlotObj;
                processPlotRecursive(subPlot, duplicateAxisFilter);
            }
        } else if (plot instanceof CombinedRangeCategoryPlot) {
            final CombinedRangeCategoryPlot cPlot = (CombinedRangeCategoryPlot) plot;
            for (final Object subPlotObj : cPlot.getSubplots()) {
                final Plot subPlot = (Plot) subPlotObj;
                processPlotRecursive(subPlot, duplicateAxisFilter);
            }
        } else if (plot instanceof CombinedRangeXYPlot) {
            final CombinedRangeXYPlot cPlot = (CombinedRangeXYPlot) plot;
            for (final Object subPlotObj : cPlot.getSubplots()) {
                final Plot subPlot = (Plot) subPlotObj;
                processPlotRecursive(subPlot, duplicateAxisFilter);
            }
        }
    }

    protected void processCategoryPlot(final Set<Integer> duplicateAxisFilter, final CategoryPlot cPlot) {
        for (int i = 0; i < cPlot.getRangeAxisCount(); i++) {
            final ValueAxis axis = cPlot.getRangeAxis(i);
            if (axis != null && duplicateAxisFilter.add(System.identityHashCode(axis))) {
                processAxis(axis);
            }
        }
        for (int i = 0; i < cPlot.getDomainAxisCount(); i++) {
            final CategoryAxis axis = cPlot.getDomainAxis(i);
            if (axis != null && duplicateAxisFilter.add(System.identityHashCode(axis))) {
                processAxis(axis);
            }
        }
    }

    protected void processXYPlot(final Set<Integer> duplicateAxisFilter, final XYPlot cPlot) {
        for (int i = 0; i < cPlot.getRangeAxisCount(); i++) {
            final ValueAxis axis = cPlot.getRangeAxis(i);
            if (axis != null && duplicateAxisFilter.add(System.identityHashCode(axis))) {
                processAxis(axis);
            }
        }
        for (int i = 0; i < cPlot.getDomainAxisCount(); i++) {
            final ValueAxis axis = cPlot.getDomainAxis(i);
            if (axis != null && duplicateAxisFilter.add(System.identityHashCode(axis))) {
                processAxis(axis);
            }
        }
    }

    protected void processAxis(final Axis axis) {
        axis.setLabelFont(processFont(axis.getLabelFont()));
        axis.setTickLabelFont(processFont(axis.getTickLabelFont()));
    }

}
