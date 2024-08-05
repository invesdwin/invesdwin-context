package de.invesdwin.context.jfreechart.plot.combined;

import java.awt.geom.Point2D;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.util.ShadowGenerator;
import org.jfree.data.Range;

import de.invesdwin.context.jfreechart.axis.AxisType;
import de.invesdwin.context.jfreechart.plot.WrappedCategoryPlot;

@NotThreadSafe
public class WrappedCombinedDomainCategoryPlot extends WrappedCategoryPlot implements ICombinedPlot {

    public WrappedCombinedDomainCategoryPlot(final CombinedDomainCategoryPlot plot) {
        super(plot);
    }

    @Override
    public CombinedDomainCategoryPlot getPlot() {
        return (CombinedDomainCategoryPlot) super.getPlot();
    }

    @Override
    public AxisType getCombinedAxisType() {
        return AxisType.DOMAIN_AXIS;
    }

    @Override
    public double getGap() {
        return getPlot().getGap();
    }

    @Override
    public void setGap(final double gap) {
        getPlot().setGap(gap);
    }

    @Override
    public void add(final Plot subplot) {
        getPlot().add((CategoryPlot) subplot);
    }

    @Override
    public void add(final Plot subplot, final int weight) {
        getPlot().add((CategoryPlot) subplot, weight);
    }

    @Override
    public void remove(final Plot subplot) {
        getPlot().remove((CategoryPlot) subplot);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Plot> getSubplots() {
        return getPlot().getSubplots();
    }

    @Override
    public void zoomDomainAxes(final double factor, final PlotRenderingInfo info, final Point2D source) {
        getPlot().zoomDomainAxes(factor, info, source);
    }

    @Override
    public void zoomDomainAxes(final double factor, final PlotRenderingInfo info, final Point2D source,
            final boolean useAnchor) {
        getPlot().zoomDomainAxes(factor, info, source, useAnchor);
    }

    @Override
    public void zoomDomainAxes(final double lowerPercent, final double upperPercent, final PlotRenderingInfo info,
            final Point2D source) {
        getPlot().zoomDomainAxes(lowerPercent, upperPercent, info, source);
    }

    @Override
    public void panDomainAxes(final double panRange, final PlotRenderingInfo info, final Point2D source) {
        getPlot().panDomainAxes(panRange, info, source);
    }

    @Override
    public Plot findSubplot(final PlotRenderingInfo info, final Point2D source) {
        return getPlot().findSubplot(info, source);
    }

    @Override
    public void setOrientation(final PlotOrientation orientation) {
        getPlot().setOrientation(orientation);
    }

    @Override
    public void setShadowGenerator(final ShadowGenerator generator) {
        getPlot().setShadowGenerator(generator);
    }

    @Override
    public Range getDataRange(final ValueAxis axis) {
        return getPlot().getDataRange(axis);
    }

    @Override
    public void plotChanged(final PlotChangeEvent event) {
        getPlot().plotChanged(event);
    }

}
