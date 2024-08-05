package de.invesdwin.context.jfreechart.plot.combined;

import java.awt.geom.Point2D;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.util.ShadowGenerator;
import org.jfree.data.Range;

import de.invesdwin.context.jfreechart.axis.AxisType;
import de.invesdwin.context.jfreechart.plot.WrappedXYPlot;

@NotThreadSafe
public class WrappedCombinedRangeXYPlot extends WrappedXYPlot implements ICombinedPlot {

    public WrappedCombinedRangeXYPlot(final CombinedRangeXYPlot plot) {
        super(plot);
    }

    @Override
    public CombinedRangeXYPlot getPlot() {
        return (CombinedRangeXYPlot) super.getPlot();
    }

    @Override
    public AxisType getCombinedAxisType() {
        return AxisType.RANGE_AXIS;
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
        getPlot().add((XYPlot) subplot);
    }

    @Override
    public void add(final Plot subplot, final int weight) {
        getPlot().add((XYPlot) subplot, weight);
    }

    @Override
    public void remove(final Plot subplot) {
        getPlot().remove((XYPlot) subplot);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<? extends Plot> getSubplots() {
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
