package de.invesdwin.context.jfreechart.visitor;

import java.awt.Font;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;
import org.jfree.chart.ui.Layer;

import de.invesdwin.util.lang.string.Strings;

@NotThreadSafe
public abstract class AJFreeChartVisitor {

    public final void process(final JFreeChart chart) {
        processChart(chart);
    }

    protected void processChart(final JFreeChart chart) {
        final TextTitle title = chart.getTitle();
        if (title != null) {
            processTitle(title);
        }
        for (int i = 0; i < chart.getSubtitleCount(); i++) {
            final Title subtitle = chart.getSubtitle(i);
            processTitle(subtitle);
        }

        processPlotRecursive(chart.getPlot(), new HashSet<Integer>());
    }

    public Font processFont(final Font font) {
        return font;
    }

    public void processTitle(final Title title) {
        if (title instanceof TextTitle) {
            final TextTitle cTitle = (TextTitle) title;
            processTextTitle(cTitle);
            cTitle.setFont(processFont(cTitle.getFont()));
        } else if (title instanceof LegendTitle) {
            final LegendTitle cTitle = (LegendTitle) title;
            processLegendTitle(cTitle);
            cTitle.setItemFont(processFont(cTitle.getItemFont()));
        } else if (title != null) {
            throw new IllegalArgumentException("Unknown " + Title.class + " type: " + title.getClass());
        }
    }

    public void processLegendTitle(final LegendTitle title) {}

    public void processTextTitle(final TextTitle title) {
        if (Strings.isBlank(title.getText())) {
            title.setVisible(false);
        }
    }

    public final void processPlot(final Plot plot) {
        processPlotRecursive(plot, new HashSet<Integer>());
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
            final List<?> plots = cPlot.getSubplots();
            for (int i = 0; i < plots.size(); i++) {
                final Object subPlotObj = plots.get(i);
                final Plot subPlot = (Plot) subPlotObj;
                processPlotRecursive(subPlot, duplicateAxisFilter);
            }
        } else if (plot instanceof CombinedDomainXYPlot) {
            final CombinedDomainXYPlot cPlot = (CombinedDomainXYPlot) plot;
            final List<?> plots = cPlot.getSubplots();
            for (int i = 0; i < plots.size(); i++) {
                final Object subPlotObj = plots.get(i);
                final Plot subPlot = (Plot) subPlotObj;
                processPlotRecursive(subPlot, duplicateAxisFilter);
            }
        } else if (plot instanceof CombinedRangeCategoryPlot) {
            final CombinedRangeCategoryPlot cPlot = (CombinedRangeCategoryPlot) plot;
            final List<?> plots = cPlot.getSubplots();
            for (int i = 0; i < plots.size(); i++) {
                final Object subPlotObj = plots.get(i);
                final Plot subPlot = (Plot) subPlotObj;
                processPlotRecursive(subPlot, duplicateAxisFilter);
            }
        } else if (plot instanceof CombinedRangeXYPlot) {
            final CombinedRangeXYPlot cPlot = (CombinedRangeXYPlot) plot;
            final List<?> plots = cPlot.getSubplots();
            for (int i = 0; i < plots.size(); i++) {
                final Object subPlotObj = plots.get(i);
                final Plot subPlot = (Plot) subPlotObj;
                processPlotRecursive(subPlot, duplicateAxisFilter);
            }
        }
    }

    protected void processCategoryPlot(final Set<Integer> duplicateAxisFilter, final CategoryPlot plot) {
        for (int i = 0; i < plot.getRangeAxisCount(); i++) {
            final ValueAxis axis = plot.getRangeAxis(i);
            if (axis != null && duplicateAxisFilter.add(System.identityHashCode(axis))) {
                processRangeAxis(axis);
            }
        }
        for (int i = 0; i < plot.getDomainAxisCount(); i++) {
            final CategoryAxis axis = plot.getDomainAxis(i);
            if (axis != null && duplicateAxisFilter.add(System.identityHashCode(axis))) {
                processDomainAxis(axis);
            }
        }
        for (int i = 0; i < plot.getRendererCount(); i++) {
            final CategoryItemRenderer renderer = plot.getRenderer(i);
            processCategoryItemRenderer(renderer);
        }
    }

    public void processCategoryItemRenderer(final CategoryItemRenderer renderer) {}

    @SuppressWarnings("unchecked")
    protected void processXYPlot(final Set<Integer> duplicateAxisFilter, final XYPlot plot) {
        for (int i = 0; i < plot.getRangeAxisCount(); i++) {
            final ValueAxis axis = plot.getRangeAxis(i);
            if (axis != null && duplicateAxisFilter.add(System.identityHashCode(axis))) {
                processRangeAxis(axis);
            }
        }
        for (int i = 0; i < plot.getDomainAxisCount(); i++) {
            final ValueAxis axis = plot.getDomainAxis(i);
            if (axis != null && duplicateAxisFilter.add(System.identityHashCode(axis))) {
                processDomainAxis(axis);
            }
        }
        for (int i = 0; i < plot.getRendererCount(); i++) {
            final XYItemRenderer renderer = plot.getRenderer(i);
            processXYItemRenderer(renderer);
        }
        final Collection<Marker> foregroundDomainMarkers = plot.getDomainMarkers(Layer.FOREGROUND);
        processMarkers(foregroundDomainMarkers);
        final Collection<Marker> backgroundDomainMarkers = plot.getDomainMarkers(Layer.BACKGROUND);
        processMarkers(backgroundDomainMarkers);
        final Collection<Marker> foregroundRangeMarkers = plot.getRangeMarkers(Layer.FOREGROUND);
        processMarkers(foregroundRangeMarkers);
        final Collection<Marker> backgroundRangeMarkers = plot.getRangeMarkers(Layer.BACKGROUND);
        processMarkers(backgroundRangeMarkers);
    }

    public void processMarkers(final Collection<Marker> markers) {
        if (markers != null && !markers.isEmpty()) {
            for (final Marker marker : markers) {
                processMarker(marker);
            }
        }
    }

    public void processMarker(final Marker marker) {
        marker.setLabelFont(processFont(marker.getLabelFont()));
    }

    public void processXYItemRenderer(final XYItemRenderer renderer) {}

    public void processDomainAxis(final Axis axis) {
        processAxis(axis);
    }

    public void processRangeAxis(final Axis axis) {
        processAxis(axis);
    }

    public void processAxis(final Axis axis) {
        axis.setLabelFont(processFont(axis.getLabelFont()));
        axis.setTickLabelFont(processFont(axis.getTickLabelFont()));
    }

}
