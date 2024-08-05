package de.invesdwin.context.jfreechart.plot;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.annotations.Annotation;
import org.jfree.chart.annotations.CategoryAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CategoryCrosshairState;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.util.ShadowGenerator;
import org.jfree.chart.util.SortOrder;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.util.error.UnknownArgumentException;

@NotThreadSafe
public class WrappedCategoryPlot extends APlot implements IAxisPlot {

    public WrappedCategoryPlot(final CategoryPlot plot) {
        super(plot);
    }

    @Override
    public CategoryPlot getPlot() {
        return (CategoryPlot) super.getPlot();
    }

    @Override
    public PlotOrientation getOrientation() {
        return getPlot().getOrientation();
    }

    public void setOrientation(final PlotOrientation orientation) {
        getPlot().setOrientation(orientation);
    }

    public RectangleInsets getAxisOffset() {
        return getPlot().getAxisOffset();
    }

    public void setAxisOffset(final RectangleInsets offset) {
        getPlot().setAxisOffset(offset);
    }

    public Axis getDomainAxis() {
        return getPlot().getDomainAxis();
    }

    public Axis getDomainAxis(final int index) {
        return getPlot().getDomainAxis(index);
    }

    public Map<Integer, ? extends Axis> getDomainAxes() {
        return getPlot().getDomainAxes();
    }

    public void setDomainAxis(final Axis axis) {
        getPlot().setDomainAxis((CategoryAxis) axis);
    }

    public void setDomainAxis(final int index, final Axis axis) {
        getPlot().setDomainAxis(index, (CategoryAxis) axis);
    }

    public void setDomainAxis(final int index, final Axis axis, final boolean notify) {
        getPlot().setDomainAxis(index, (CategoryAxis) axis, notify);
    }

    public void setDomainAxes(final Axis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setDomainAxis(i, axes[i], i == axes.length - 1);
        }
    }

    public AxisLocation getDomainAxisLocation() {
        return getPlot().getDomainAxisLocation();
    }

    public void setDomainAxisLocation(final AxisLocation location) {
        getPlot().setDomainAxisLocation(location);
    }

    public void setDomainAxisLocation(final AxisLocation location, final boolean notify) {
        getPlot().setDomainAxisLocation(location, notify);
    }

    public RectangleEdge getDomainAxisEdge() {
        return getPlot().getDomainAxisEdge();
    }

    public int getDomainAxisCount() {
        return getPlot().getDomainAxisCount();
    }

    public void clearDomainAxes() {
        getPlot().clearDomainAxes();
    }

    public void configureDomainAxes() {
        getPlot().configureDomainAxes();
    }

    public AxisLocation getDomainAxisLocation(final int index) {
        return getPlot().getDomainAxisLocation(index);
    }

    public void setDomainAxisLocation(final int index, final AxisLocation location) {
        getPlot().setDomainAxisLocation(index, location);
    }

    public void setDomainAxisLocation(final int index, final AxisLocation location, final boolean notify) {
        getPlot().setDomainAxisLocation(index, location, notify);
    }

    public RectangleEdge getDomainAxisEdge(final int index) {
        return getPlot().getDomainAxisEdge();
    }

    public Axis getRangeAxis() {
        return getPlot().getRangeAxis();
    }

    public void setRangeAxis(final Axis axis) {
        getPlot().setRangeAxis((ValueAxis) axis);
    }

    public AxisLocation getRangeAxisLocation() {
        return getPlot().getRangeAxisLocation();
    }

    public void setRangeAxisLocation(final AxisLocation location) {
        getPlot().setRangeAxisLocation(location);
    }

    public void setRangeAxisLocation(final AxisLocation location, final boolean notify) {
        getPlot().setRangeAxisLocation(location, notify);
    }

    public RectangleEdge getRangeAxisEdge() {
        return getPlot().getRangeAxisEdge();
    }

    public Axis getRangeAxis(final int index) {
        return getPlot().getRangeAxis(index);
    }

    public Map<Integer, ? extends Axis> getRangeAxes() {
        return getPlot().getRangeAxes();
    }

    public void setRangeAxis(final int index, final Axis axis) {
        getPlot().setRangeAxis(index, (ValueAxis) axis);
    }

    public void setRangeAxis(final int index, final Axis axis, final boolean notify) {
        getPlot().setRangeAxis(index, (ValueAxis) axis, notify);
    }

    public void setRangeAxes(final Axis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setRangeAxis(i, axes[i], i == axes.length - 1);
        }
    }

    public int getRangeAxisCount() {
        return getPlot().getRangeAxisCount();
    }

    public void clearRangeAxes() {
        getPlot().clearRangeAxes();
    }

    public void configureRangeAxes() {
        getPlot().configureRangeAxes();
    }

    public AxisLocation getRangeAxisLocation(final int index) {
        return getPlot().getRangeAxisLocation(index);
    }

    public void setRangeAxisLocation(final int index, final AxisLocation location) {
        getPlot().setRangeAxisLocation(index, location);
    }

    public void setRangeAxisLocation(final int index, final AxisLocation location, final boolean notify) {
        getPlot().setRangeAxisLocation(location);
    }

    public RectangleEdge getRangeAxisEdge(final int index) {
        return getPlot().getRangeAxisEdge();
    }

    public Dataset getDataset() {
        return getPlot().getDataset();
    }

    public Dataset getDataset(final int index) {
        return getPlot().getDataset(index);
    }

    public Map<Integer, ? extends Dataset> getDatasets() {
        return getPlot().getDatasets();
    }

    public void setDataset(final Dataset dataset) {
        getPlot().setDataset((CategoryDataset) dataset);
    }

    public void setDataset(final int index, final XYDataset dataset) {
        getPlot().setDataset(index, (CategoryDataset) dataset);
    }

    public int getDatasetCount() {
        return getPlot().getDatasetCount();
    }

    public int indexOf(final Dataset dataset) {
        return getPlot().indexOf((CategoryDataset) dataset);
    }

    public void mapDatasetToDomainAxis(final int index, final int axisIndex) {
        getPlot().mapDatasetToDomainAxis(index, axisIndex);
    }

    public void mapDatasetToDomainAxes(final int index, final List<Integer> axisIndices) {
        getPlot().mapDatasetToDomainAxes(index, axisIndices);
    }

    public void mapDatasetToRangeAxis(final int index, final int axisIndex) {
        getPlot().mapDatasetToRangeAxis(index, axisIndex);
    }

    public void mapDatasetToRangeAxes(final int index, final List<Integer> axisIndices) {
        getPlot().mapDatasetToRangeAxes(index, axisIndices);
    }

    public int getRendererCount() {
        return getPlot().getRendererCount();
    }

    public LegendItemSource getRenderer() {
        return getPlot().getRenderer();
    }

    public LegendItemSource getRenderer(final int index) {
        return getPlot().getRenderer(index);
    }

    public Map<Integer, ? extends LegendItemSource> getRenderers() {
        return getPlot().getRenderers();
    }

    public void setRenderer(final LegendItemSource renderer) {
        getPlot().setRenderer((CategoryItemRenderer) renderer);
    }

    public void setRenderer(final int index, final LegendItemSource renderer) {
        getPlot().setRenderer(index, (CategoryItemRenderer) renderer);
    }

    public void setRenderer(final int index, final LegendItemSource renderer, final boolean notify) {
        getPlot().setRenderer(index, (CategoryItemRenderer) renderer, notify);
    }

    public void setRenderers(final LegendItemSource[] renderers) {
        for (int i = 0; i < renderers.length; i++) {
            setRenderer(i, renderers[i], i == renderers.length - 1);
        }
    }

    public DatasetRenderingOrder getDatasetRenderingOrder() {
        return getPlot().getDatasetRenderingOrder();
    }

    public void setDatasetRenderingOrder(final DatasetRenderingOrder order) {
        getPlot().setDatasetRenderingOrder(order);
    }

    public SeriesRenderingOrder getSeriesRenderingOrder() {
        final SortOrder sortOrder = getPlot().getColumnRenderingOrder();
        switch (sortOrder) {
        case ASCENDING:
            return SeriesRenderingOrder.REVERSE;
        case DESCENDING:
            return SeriesRenderingOrder.FORWARD;
        default:
            throw UnknownArgumentException.newInstance(SortOrder.class, sortOrder);
        }
    }

    public void setSeriesRenderingOrder(final SeriesRenderingOrder order) {
        if (order == SeriesRenderingOrder.REVERSE) {
            getPlot().setColumnRenderingOrder(SortOrder.ASCENDING);
        } else if (order == SeriesRenderingOrder.FORWARD) {
            getPlot().setColumnRenderingOrder(SortOrder.DESCENDING);
        } else {
            throw UnknownArgumentException.newInstance(Class.class, order.getClass());
        }
    }

    public int getIndexOf(final LegendItemSource renderer) {
        return getPlot().getIndexOf((CategoryItemRenderer) renderer);
    }

    public LegendItemSource getRendererForDataset(final Dataset dataset) {
        return getPlot().getRendererForDataset((CategoryDataset) dataset);
    }

    public int getWeight() {
        return getPlot().getWeight();
    }

    public void setWeight(final int weight) {
        getPlot().setWeight(weight);
    }

    public boolean isDomainGridlinesVisible() {
        return getPlot().isDomainGridlinesVisible();
    }

    public void setDomainGridlinesVisible(final boolean visible) {
        getPlot().setDomainGridlinesVisible(visible);
    }

    public boolean isDomainMinorGridlinesVisible() {
        return false;
    }

    public void setDomainMinorGridlinesVisible(final boolean visible) {
        //noop
    }

    public Stroke getDomainGridlineStroke() {
        return getPlot().getDomainGridlineStroke();
    }

    public void setDomainGridlineStroke(final Stroke stroke) {
        getPlot().setDomainGridlineStroke(stroke);
    }

    public Stroke getDomainMinorGridlineStroke() {
        return null;
    }

    public void setDomainMinorGridlineStroke(final Stroke stroke) {
        //noop
    }

    public Paint getDomainGridlinePaint() {
        return getPlot().getDomainGridlinePaint();
    }

    public void setDomainGridlinePaint(final Paint paint) {
        getPlot().setDomainGridlinePaint(paint);
    }

    public Paint getDomainMinorGridlinePaint() {
        return null;
    }

    public void setDomainMinorGridlinePaint(final Paint paint) {
        //noop
    }

    public boolean isRangeGridlinesVisible() {
        return getPlot().isRangeGridlinesVisible();
    }

    public void setRangeGridlinesVisible(final boolean visible) {
        getPlot().setRangeGridlinesVisible(visible);
    }

    public Stroke getRangeGridlineStroke() {
        return getPlot().getRangeGridlineStroke();
    }

    public void setRangeGridlineStroke(final Stroke stroke) {
        getPlot().setRangeGridlineStroke(stroke);
    }

    public Paint getRangeGridlinePaint() {
        return getPlot().getRangeGridlinePaint();
    }

    public void setRangeGridlinePaint(final Paint paint) {
        getPlot().setRangeGridlinePaint(paint);
    }

    public boolean isRangeMinorGridlinesVisible() {
        return getPlot().isRangeMinorGridlinesVisible();
    }

    public void setRangeMinorGridlinesVisible(final boolean visible) {
        getPlot().setRangeMinorGridlinesVisible(visible);
    }

    public Stroke getRangeMinorGridlineStroke() {
        return getPlot().getRangeMinorGridlineStroke();
    }

    public void setRangeMinorGridlineStroke(final Stroke stroke) {
        getPlot().setRangeMinorGridlineStroke(stroke);
    }

    public Paint getRangeMinorGridlinePaint() {
        return getPlot().getRangeMinorGridlinePaint();
    }

    public void setRangeMinorGridlinePaint(final Paint paint) {
        getPlot().setRangeMinorGridlinePaint(paint);
    }

    public boolean isDomainZeroBaselineVisible() {
        return false;
    }

    public void setDomainZeroBaselineVisible(final boolean visible) {
        //noop
    }

    public Stroke getDomainZeroBaselineStroke() {
        return null;
    }

    public void setDomainZeroBaselineStroke(final Stroke stroke) {
        //noop
    }

    public Paint getDomainZeroBaselinePaint() {
        return null;
    }

    public void setDomainZeroBaselinePaint(final Paint paint) {
        //noop
    }

    public boolean isRangeZeroBaselineVisible() {
        return getPlot().isRangeZeroBaselineVisible();
    }

    public void setRangeZeroBaselineVisible(final boolean visible) {
        getPlot().setRangeZeroBaselineVisible(visible);
    }

    public Stroke getRangeZeroBaselineStroke() {
        return getPlot().getRangeZeroBaselineStroke();
    }

    public void setRangeZeroBaselineStroke(final Stroke stroke) {
        getPlot().setRangeZeroBaselineStroke(stroke);
    }

    public Paint getRangeZeroBaselinePaint() {
        return getPlot().getRangeZeroBaselinePaint();
    }

    public void setRangeZeroBaselinePaint(final Paint paint) {
        getPlot().setRangeZeroBaselinePaint(paint);
    }

    public Paint getDomainTickBandPaint() {
        return null;
    }

    public void setDomainTickBandPaint(final Paint paint) {
        //noop
    }

    public Paint getRangeTickBandPaint() {
        return null;
    }

    public void setRangeTickBandPaint(final Paint paint) {
        //noop
    }

    public Point2D getQuadrantOrigin() {
        return null;
    }

    public void setQuadrantOrigin(final Point2D origin) {
        //noop
    }

    public Paint getQuadrantPaint(final int index) {
        return null;
    }

    public void setQuadrantPaint(final int index, final Paint paint) {
        //noop
    }

    public void addDomainMarker(final Marker marker) {
        getPlot().addDomainMarker((CategoryMarker) marker);
    }

    public void addDomainMarker(final Marker marker, final Layer layer) {
        getPlot().addDomainMarker((CategoryMarker) marker, layer);
    }

    public void clearDomainMarkers() {
        getPlot().clearDomainMarkers();
    }

    public void clearDomainMarkers(final int index) {
        getPlot().clearDomainMarkers(index);
    }

    public void addDomainMarker(final int index, final Marker marker, final Layer layer) {
        getPlot().addDomainMarker(index, (CategoryMarker) marker, layer);
    }

    public void addDomainMarker(final int index, final Marker marker, final Layer layer, final boolean notify) {
        getPlot().addDomainMarker(index, (CategoryMarker) marker, layer, notify);
    }

    public boolean removeDomainMarker(final Marker marker) {
        return getPlot().removeDomainMarker(marker);
    }

    public boolean removeDomainMarker(final Marker marker, final Layer layer) {
        return getPlot().removeDomainMarker(marker, layer);
    }

    public boolean removeDomainMarker(final int index, final Marker marker, final Layer layer) {
        return getPlot().removeDomainMarker(index, marker, layer);
    }

    public boolean removeDomainMarker(final int index, final Marker marker, final Layer layer, final boolean notify) {
        return getPlot().removeDomainMarker(index, marker, layer, notify);
    }

    public void addRangeMarker(final Marker marker) {
        getPlot().addRangeMarker(marker);
    }

    public void addRangeMarker(final Marker marker, final Layer layer) {
        getPlot().addRangeMarker(marker, layer);
    }

    public void clearRangeMarkers() {
        getPlot().clearRangeMarkers();
    }

    public void addRangeMarker(final int index, final Marker marker, final Layer layer) {
        getPlot().addRangeMarker(index, marker, layer);
    }

    public void addRangeMarker(final int index, final Marker marker, final Layer layer, final boolean notify) {
        getPlot().addRangeMarker(index, marker, layer, notify);
    }

    public void clearRangeMarkers(final int index) {
        getPlot().clearRangeMarkers(index);
    }

    public boolean removeRangeMarker(final Marker marker) {
        return getPlot().removeRangeMarker(marker);
    }

    public boolean removeRangeMarker(final Marker marker, final Layer layer) {
        return getPlot().removeRangeMarker(marker, layer);
    }

    public boolean removeRangeMarker(final int index, final Marker marker, final Layer layer) {
        return getPlot().removeRangeMarker(index, marker, layer);
    }

    public boolean removeRangeMarker(final int index, final Marker marker, final Layer layer, final boolean notify) {
        return getPlot().removeRangeMarker(index, marker, layer, notify);
    }

    public void addAnnotation(final Annotation annotation) {
        getPlot().addAnnotation((CategoryAnnotation) annotation);
    }

    public void addAnnotation(final Annotation annotation, final boolean notify) {
        getPlot().addAnnotation((CategoryAnnotation) annotation, notify);
    }

    public boolean removeAnnotation(final Annotation annotation) {
        return getPlot().removeAnnotation((CategoryAnnotation) annotation);
    }

    public boolean removeAnnotation(final Annotation annotation, final boolean notify) {
        return getPlot().removeAnnotation((CategoryAnnotation) annotation, notify);
    }

    @SuppressWarnings("unchecked")
    public List<Annotation> getAnnotations() {
        return getPlot().getAnnotations();
    }

    public void clearAnnotations() {
        getPlot().clearAnnotations();
    }

    public ShadowGenerator getShadowGenerator() {
        return getPlot().getShadowGenerator();
    }

    public void setShadowGenerator(final ShadowGenerator generator) {
        getPlot().setShadowGenerator(generator);
    }

    public void drawDomainTickBands(final Graphics2D g2, final Rectangle2D dataArea, final List<ValueTick> ticks) {
        //noop
    }

    public void drawRangeTickBands(final Graphics2D g2, final Rectangle2D dataArea, final List<ValueTick> ticks) {
        //noop
    }

    public boolean render(final Graphics2D g2, final Rectangle2D dataArea, final int index,
            final PlotRenderingInfo info, final CrosshairState crosshairState) {
        return getPlot().render(g2, dataArea, index, info, (CategoryCrosshairState) crosshairState);
    }

    public Axis getDomainAxisForDataset(final int index) {
        return getPlot().getDomainAxisForDataset(index);
    }

    public Axis getRangeAxisForDataset(final int index) {
        return getPlot().getRangeAxisForDataset(index);
    }

    @SuppressWarnings("unchecked")
    public Collection<Marker> getDomainMarkers(final Layer layer) {
        return getPlot().getDomainMarkers(layer);
    }

    @SuppressWarnings("unchecked")
    public Collection<Marker> getRangeMarkers(final Layer layer) {
        return getPlot().getRangeMarkers(layer);
    }

    public Collection<? extends Marker> getDomainMarkers(final int index, final Layer layer) {
        return getPlot().getDomainMarkers(index, layer);
    }

    public Collection<Marker> getRangeMarkers(final int index, final Layer layer) {
        return getPlot().getRangeMarkers(index, layer);
    }

    public int getDomainAxisIndex(final Axis axis) {
        return getPlot().getDomainAxisIndex((CategoryAxis) axis);
    }

    public int getRangeAxisIndex(final Axis axis) {
        return getPlot().getRangeAxisIndex((ValueAxis) axis);
    }

    public Range getDataRange(final Axis axis) {
        return getPlot().getDataRange((ValueAxis) axis);
    }

    @Override
    public void rendererChanged(final RendererChangeEvent event) {
        getPlot().rendererChanged(event);
    }

    public boolean isDomainCrosshairVisible() {
        return getPlot().isDomainCrosshairVisible();
    }

    public void setDomainCrosshairVisible(final boolean flag) {
        getPlot().setDomainCrosshairVisible(flag);
    }

    public boolean isDomainCrosshairLockedOnData() {
        return false;
    }

    public void setDomainCrosshairLockedOnData(final boolean flag) {
        //noop
    }

    public double getDomainCrosshairValue() {
        return Double.NaN;
    }

    public void setDomainCrosshairValue(final double value) {
        //noop
    }

    public void setDomainCrosshairValue(final double value, final boolean notify) {
        //noop
    }

    public Stroke getDomainCrosshairStroke() {
        return getPlot().getDomainCrosshairStroke();
    }

    public void setDomainCrosshairStroke(final Stroke stroke) {
        getPlot().setDomainCrosshairStroke(stroke);
    }

    public Paint getDomainCrosshairPaint() {
        return getPlot().getDomainCrosshairPaint();
    }

    public void setDomainCrosshairPaint(final Paint paint) {
        getPlot().setDomainCrosshairPaint(paint);
    }

    public boolean isRangeCrosshairVisible() {
        return getPlot().isRangeCrosshairVisible();
    }

    public void setRangeCrosshairVisible(final boolean flag) {
        getPlot().setRangeCrosshairVisible(flag);
    }

    public boolean isRangeCrosshairLockedOnData() {
        return getPlot().isRangeCrosshairLockedOnData();
    }

    public void setRangeCrosshairLockedOnData(final boolean flag) {
        getPlot().setRangeCrosshairLockedOnData(flag);
    }

    public double getRangeCrosshairValue() {
        return getPlot().getRangeCrosshairValue();
    }

    public void setRangeCrosshairValue(final double value) {
        getPlot().setRangeCrosshairValue(value);
    }

    public void setRangeCrosshairValue(final double value, final boolean notify) {
        getPlot().setRangeCrosshairValue(value, notify);
    }

    public Stroke getRangeCrosshairStroke() {
        return getPlot().getRangeCrosshairStroke();
    }

    public void setRangeCrosshairStroke(final Stroke stroke) {
        getPlot().setRangeCrosshairStroke(stroke);
    }

    public Paint getRangeCrosshairPaint() {
        return getPlot().getRangeCrosshairPaint();
    }

    public void setRangeCrosshairPaint(final Paint paint) {
        getPlot().setRangeCrosshairPaint(paint);
    }

    public AxisSpace getFixedDomainAxisSpace() {
        return getPlot().getFixedDomainAxisSpace();
    }

    public void setFixedDomainAxisSpace(final AxisSpace space) {
        getPlot().setFixedDomainAxisSpace(space);
    }

    public void setFixedDomainAxisSpace(final AxisSpace space, final boolean notify) {
        getPlot().setFixedDomainAxisSpace(space, notify);
    }

    public AxisSpace getFixedRangeAxisSpace() {
        return getPlot().getFixedRangeAxisSpace();
    }

    public void setFixedRangeAxisSpace(final AxisSpace space) {
        getPlot().setFixedRangeAxisSpace(space);
    }

    public void setFixedRangeAxisSpace(final AxisSpace space, final boolean notify) {
        getPlot().setFixedRangeAxisSpace(space, notify);
    }

    @Override
    public boolean isDomainPannable() {
        return getPlot().isDomainPannable();
    }

    public void setDomainPannable(final boolean pannable) {
        //noop
    }

    @Override
    public boolean isRangePannable() {
        return getPlot().isRangePannable();
    }

    public void setRangePannable(final boolean pannable) {
        getPlot().setRangePannable(pannable);
    }

    @Override
    public void panDomainAxes(final double percent, final PlotRenderingInfo info, final Point2D source) {
        getPlot().panDomainAxes(percent, info, source);
    }

    @Override
    public void panRangeAxes(final double percent, final PlotRenderingInfo info, final Point2D source) {
        getPlot().panRangeAxes(percent, info, source);
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
    public void zoomRangeAxes(final double factor, final PlotRenderingInfo info, final Point2D source) {
        getPlot().zoomRangeAxes(factor, info, source);
    }

    @Override
    public void zoomRangeAxes(final double factor, final PlotRenderingInfo info, final Point2D source,
            final boolean useAnchor) {
        getPlot().zoomRangeAxes(factor, info, source, useAnchor);
    }

    @Override
    public void zoomRangeAxes(final double lowerPercent, final double upperPercent, final PlotRenderingInfo info,
            final Point2D source) {
        getPlot().zoomRangeAxes(lowerPercent, upperPercent, info, source);
    }

    @Override
    public boolean isDomainZoomable() {
        return getPlot().isDomainZoomable();
    }

    @Override
    public boolean isRangeZoomable() {
        return getPlot().isRangeZoomable();
    }

    public int getSeriesCount() {
        return 1;
    }

    public LegendItemCollection getFixedLegendItems() {
        return getPlot().getFixedLegendItems();
    }

    public void setFixedLegendItems(final LegendItemCollection items) {
        getPlot().setFixedLegendItems(items);
    }

}
