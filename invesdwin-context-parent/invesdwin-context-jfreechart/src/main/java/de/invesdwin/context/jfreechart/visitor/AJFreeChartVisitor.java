package de.invesdwin.context.jfreechart.visitor;

import java.awt.Font;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemSource;
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

import de.invesdwin.context.jfreechart.axis.AxisType;
import de.invesdwin.context.jfreechart.axis.attached.AttachedDomainCategoryAxis;
import de.invesdwin.context.jfreechart.axis.attached.AttachedDomainValueAxis;
import de.invesdwin.context.jfreechart.axis.attached.AttachedRangeValueAxis;
import de.invesdwin.context.jfreechart.axis.attached.IAttachedAxis;
import de.invesdwin.context.jfreechart.plot.IAxisPlot;
import de.invesdwin.context.jfreechart.plot.WrappedCategoryPlot;
import de.invesdwin.context.jfreechart.plot.WrappedXYPlot;
import de.invesdwin.context.jfreechart.plot.combined.ICombinedPlot;
import de.invesdwin.context.jfreechart.plot.combined.WrappedCombinedDomainCategoryPlot;
import de.invesdwin.context.jfreechart.plot.combined.WrappedCombinedDomainXYPlot;
import de.invesdwin.context.jfreechart.plot.combined.WrappedCombinedRangeCategoryPlot;
import de.invesdwin.context.jfreechart.plot.combined.WrappedCombinedRangeXYPlot;
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
            processXYPlot(duplicateAxisFilter, new WrappedXYPlot(cPlot));
        } else if (plot instanceof CategoryPlot) {
            final CategoryPlot cPlot = (CategoryPlot) plot;
            processCategoryPlot(duplicateAxisFilter, new WrappedCategoryPlot(cPlot));
        }

        //recurse
        if (plot instanceof CombinedDomainCategoryPlot) {
            final CombinedDomainCategoryPlot cPlot = (CombinedDomainCategoryPlot) plot;
            processCombinedPlot(duplicateAxisFilter, new WrappedCombinedDomainCategoryPlot(cPlot));
        } else if (plot instanceof CombinedDomainXYPlot) {
            final CombinedDomainXYPlot cPlot = (CombinedDomainXYPlot) plot;
            processCombinedPlot(duplicateAxisFilter, new WrappedCombinedDomainXYPlot(cPlot));
        } else if (plot instanceof CombinedRangeCategoryPlot) {
            final CombinedRangeCategoryPlot cPlot = (CombinedRangeCategoryPlot) plot;
            processCombinedPlot(duplicateAxisFilter, new WrappedCombinedRangeCategoryPlot(cPlot));
        } else if (plot instanceof CombinedRangeXYPlot) {
            final CombinedRangeXYPlot cPlot = (CombinedRangeXYPlot) plot;
            processCombinedPlot(duplicateAxisFilter, new WrappedCombinedRangeXYPlot(cPlot));
        }
    }

    protected void processCombinedPlot(final Set<Integer> duplicateAxisFilter, final ICombinedPlot plot) {
        final List<?> plots = plot.getSubplots();
        for (int i = 0; i < plots.size(); i++) {
            final Object subPlotObj = plots.get(i);
            final Plot subPlot = (Plot) subPlotObj;
            processPlotRecursive(subPlot, duplicateAxisFilter);
        }
    }

    protected void processCategoryPlot(final Set<Integer> duplicateAxisFilter, final WrappedCategoryPlot plot) {
        processAxisPlot(plot);
        for (int i = 0; i < plot.getRangeAxisCount(); i++) {
            final ValueAxis axis = plot.getPlot().getRangeAxis(i);
            if (axis != null && duplicateAxisFilter.add(System.identityHashCode(axis))) {
                processAttachedAxis(new AttachedRangeValueAxis(plot, i, axis));
            }
        }
        for (int i = 0; i < plot.getDomainAxisCount(); i++) {
            final CategoryAxis axis = plot.getPlot().getDomainAxis(i);
            if (axis != null && duplicateAxisFilter.add(System.identityHashCode(axis))) {
                processAttachedAxis(new AttachedDomainCategoryAxis(plot, i, axis));
            }
        }
        for (int i = 0; i < plot.getRendererCount(); i++) {
            final CategoryItemRenderer renderer = plot.getPlot().getRenderer(i);
            processCategoryItemRenderer(renderer);
        }
    }

    public void processCategoryItemRenderer(final CategoryItemRenderer renderer) {}

    protected void processXYPlot(final Set<Integer> duplicateAxisFilter, final WrappedXYPlot plot) {
        processAxisPlot(plot);
        for (int i = 0; i < plot.getRangeAxisCount(); i++) {
            final ValueAxis axis = plot.getPlot().getRangeAxis(i);
            if (axis != null && duplicateAxisFilter.add(System.identityHashCode(axis))) {
                processAttachedAxis(new AttachedRangeValueAxis(plot, i, axis));
            }
        }
        for (int i = 0; i < plot.getDomainAxisCount(); i++) {
            final ValueAxis axis = plot.getPlot().getDomainAxis(i);
            if (axis != null && duplicateAxisFilter.add(System.identityHashCode(axis))) {
                processAttachedAxis(new AttachedDomainValueAxis(plot, i, axis));
            }
        }
        for (int i = 0; i < plot.getRendererCount(); i++) {
            final XYItemRenderer renderer = plot.getPlot().getRenderer(i);
            processXYItemRenderer(renderer);
        }
    }

    protected void processAxisPlot(final IAxisPlot plot) {
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

    public void processXYItemRenderer(final LegendItemSource renderer) {}

    public void processAttachedAxis(final IAttachedAxis axis) {
        processAxis(axis.getAxis(), axis.getAxisType());
    }

    public void processAxis(final Axis axis, final AxisType axisType) {
        axis.setLabelFont(processFont(axis.getLabelFont()));
        axis.setTickLabelFont(processFont(axis.getTickLabelFont()));
    }

}
