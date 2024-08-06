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
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.util.ShadowGenerator;
import org.jfree.data.Range;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;

@NotThreadSafe
public class WrappedXYPlot extends APlot implements IAxisPlot {

    public WrappedXYPlot(final XYPlot plot) {
        super(plot);
    }

    @Override
    public XYPlot getPlot() {
        return (XYPlot) super.getPlot();
    }

    @Override
    public PlotOrientation getOrientation() {
        return getPlot().getOrientation();
    }

    @Override
    public void setOrientation(final PlotOrientation orientation) {
        getPlot().setOrientation(orientation);
    }

    @Override
    public RectangleInsets getAxisOffset() {
        return getPlot().getAxisOffset();
    }

    @Override
    public void setAxisOffset(final RectangleInsets offset) {
        getPlot().setAxisOffset(offset);
    }

    @Override
    public Axis getDomainAxis() {
        return getPlot().getDomainAxis();
    }

    @Override
    public Axis getDomainAxis(final int index) {
        return getPlot().getDomainAxis(index);
    }

    @Override
    public Map<Integer, ? extends Axis> getDomainAxes() {
        return getPlot().getDomainAxes();
    }

    @Override
    public void setDomainAxis(final Axis axis) {
        getPlot().setDomainAxis((ValueAxis) axis);
    }

    @Override
    public void setDomainAxis(final int index, final Axis axis) {
        getPlot().setDomainAxis(index, (ValueAxis) axis);
    }

    @Override
    public void setDomainAxis(final int index, final Axis axis, final boolean notify) {
        getPlot().setDomainAxis(index, (ValueAxis) axis, notify);
    }

    @Override
    public void setDomainAxes(final Axis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setDomainAxis(i, axes[i], i == axes.length - 1);
        }
    }

    @Override
    public AxisLocation getDomainAxisLocation() {
        return getPlot().getDomainAxisLocation();
    }

    @Override
    public void setDomainAxisLocation(final AxisLocation location) {
        getPlot().setDomainAxisLocation(location);
    }

    @Override
    public void setDomainAxisLocation(final AxisLocation location, final boolean notify) {
        getPlot().setDomainAxisLocation(location, notify);
    }

    @Override
    public RectangleEdge getDomainAxisEdge() {
        return getPlot().getDomainAxisEdge();
    }

    @Override
    public int getDomainAxisCount() {
        return getPlot().getDomainAxisCount();
    }

    @Override
    public void clearDomainAxes() {
        getPlot().clearDomainAxes();
    }

    @Override
    public void configureDomainAxes() {
        getPlot().configureDomainAxes();
    }

    @Override
    public AxisLocation getDomainAxisLocation(final int index) {
        return getPlot().getDomainAxisLocation(index);
    }

    @Override
    public void setDomainAxisLocation(final int index, final AxisLocation location) {
        getPlot().setDomainAxisLocation(index, location);
    }

    @Override
    public void setDomainAxisLocation(final int index, final AxisLocation location, final boolean notify) {
        getPlot().setDomainAxisLocation(index, location, notify);
    }

    @Override
    public RectangleEdge getDomainAxisEdge(final int index) {
        return getPlot().getDomainAxisEdge(index);
    }

    @Override
    public Axis getRangeAxis() {
        return getPlot().getRangeAxis();
    }

    @Override
    public void setRangeAxis(final Axis axis) {
        getPlot().setRangeAxis((ValueAxis) axis);
    }

    @Override
    public AxisLocation getRangeAxisLocation() {
        return getPlot().getRangeAxisLocation();
    }

    @Override
    public void setRangeAxisLocation(final AxisLocation location) {
        getPlot().setRangeAxisLocation(location);
    }

    @Override
    public void setRangeAxisLocation(final AxisLocation location, final boolean notify) {
        getPlot().setRangeAxisLocation(location, notify);
    }

    @Override
    public RectangleEdge getRangeAxisEdge() {
        return getPlot().getRangeAxisEdge();
    }

    @Override
    public Axis getRangeAxis(final int index) {
        return getPlot().getRangeAxis(index);
    }

    @Override
    public Map<Integer, ? extends Axis> getRangeAxes() {
        return getPlot().getRangeAxes();
    }

    @Override
    public void setRangeAxis(final int index, final Axis axis) {
        getPlot().setRangeAxis(index, (ValueAxis) axis);
    }

    @Override
    public void setRangeAxis(final int index, final Axis axis, final boolean notify) {
        getPlot().setRangeAxis(index, (ValueAxis) axis, notify);
    }

    @Override
    public void setRangeAxes(final Axis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setRangeAxis(i, axes[i], i == axes.length - 1);
        }
    }

    @Override
    public int getRangeAxisCount() {
        return getPlot().getRangeAxisCount();
    }

    @Override
    public void clearRangeAxes() {
        getPlot().clearRangeAxes();
    }

    @Override
    public void configureRangeAxes() {
        getPlot().configureRangeAxes();
    }

    @Override
    public AxisLocation getRangeAxisLocation(final int index) {
        return getPlot().getRangeAxisLocation(index);
    }

    @Override
    public void setRangeAxisLocation(final int index, final AxisLocation location) {
        getPlot().setRangeAxisLocation(index, location);
    }

    @Override
    public void setRangeAxisLocation(final int index, final AxisLocation location, final boolean notify) {
        getPlot().setRangeAxisLocation(index, location);
    }

    @Override
    public RectangleEdge getRangeAxisEdge(final int index) {
        return getPlot().getRangeAxisEdge(index);
    }

    @Override
    public Dataset getDataset() {
        return getPlot().getDataset();
    }

    @Override
    public Dataset getDataset(final int index) {
        return getPlot().getDataset(index);
    }

    @Override
    public Map<Integer, ? extends Dataset> getDatasets() {
        return getPlot().getDatasets();
    }

    @Override
    public void setDataset(final Dataset dataset) {
        getPlot().setDataset((XYDataset) dataset);
    }

    @Override
    public void setDataset(final int index, final XYDataset dataset) {
        getPlot().setDataset(index, dataset);
    }

    @Override
    public int getDatasetCount() {
        return getPlot().getDatasetCount();
    }

    @Override
    public int indexOf(final Dataset dataset) {
        return getPlot().indexOf((XYDataset) dataset);
    }

    @Override
    public void mapDatasetToDomainAxis(final int index, final int axisIndex) {
        getPlot().mapDatasetToDomainAxis(index, axisIndex);
    }

    @Override
    public void mapDatasetToDomainAxes(final int index, final List<Integer> axisIndices) {
        getPlot().mapDatasetToDomainAxes(index, axisIndices);
    }

    @Override
    public void mapDatasetToRangeAxis(final int index, final int axisIndex) {
        getPlot().mapDatasetToRangeAxis(index, axisIndex);
    }

    @Override
    public void mapDatasetToRangeAxes(final int index, final List<Integer> axisIndices) {
        getPlot().mapDatasetToRangeAxes(index, axisIndices);
    }

    @Override
    public int getRendererCount() {
        return getPlot().getRendererCount();
    }

    @Override
    public LegendItemSource getRenderer() {
        return getPlot().getRenderer();
    }

    @Override
    public LegendItemSource getRenderer(final int index) {
        return getPlot().getRenderer(index);
    }

    @Override
    public Map<Integer, ? extends LegendItemSource> getRenderers() {
        return getPlot().getRenderers();
    }

    @Override
    public void setRenderer(final LegendItemSource renderer) {
        getPlot().setRenderer((XYItemRenderer) renderer);
    }

    @Override
    public void setRenderer(final int index, final LegendItemSource renderer) {
        getPlot().setRenderer(index, (XYItemRenderer) renderer);
    }

    @Override
    public void setRenderer(final int index, final LegendItemSource renderer, final boolean notify) {
        getPlot().setRenderer(index, (XYItemRenderer) renderer, notify);
    }

    @Override
    public void setRenderers(final LegendItemSource[] renderers) {
        for (int i = 0; i < renderers.length; i++) {
            setRenderer(i, renderers[i], i == renderers.length - 1);
        }
    }

    @Override
    public DatasetRenderingOrder getDatasetRenderingOrder() {
        return getPlot().getDatasetRenderingOrder();
    }

    @Override
    public void setDatasetRenderingOrder(final DatasetRenderingOrder order) {
        getPlot().setDatasetRenderingOrder(order);
    }

    @Override
    public SeriesRenderingOrder getSeriesRenderingOrder() {
        return getPlot().getSeriesRenderingOrder();
    }

    @Override
    public void setSeriesRenderingOrder(final SeriesRenderingOrder order) {
        getPlot().setSeriesRenderingOrder(order);
    }

    @Override
    public int getIndexOf(final LegendItemSource renderer) {
        return getPlot().getIndexOf((XYItemRenderer) renderer);
    }

    @Override
    public LegendItemSource getRendererForDataset(final Dataset dataset) {
        return getPlot().getRendererForDataset((XYDataset) dataset);
    }

    @Override
    public int getWeight() {
        return getPlot().getWeight();
    }

    @Override
    public void setWeight(final int weight) {
        getPlot().setWeight(weight);
    }

    @Override
    public boolean isDomainGridlinesVisible() {
        return getPlot().isDomainGridlinesVisible();
    }

    @Override
    public void setDomainGridlinesVisible(final boolean visible) {
        getPlot().setDomainGridlinesVisible(visible);
    }

    @Override
    public boolean isDomainMinorGridlinesVisible() {
        return getPlot().isDomainMinorGridlinesVisible();
    }

    @Override
    public void setDomainMinorGridlinesVisible(final boolean visible) {
        getPlot().setDomainMinorGridlinesVisible(visible);
    }

    @Override
    public Stroke getDomainGridlineStroke() {
        return getPlot().getDomainGridlineStroke();
    }

    @Override
    public void setDomainGridlineStroke(final Stroke stroke) {
        getPlot().setDomainGridlineStroke(stroke);
    }

    @Override
    public Stroke getDomainMinorGridlineStroke() {
        return getPlot().getDomainMinorGridlineStroke();
    }

    @Override
    public void setDomainMinorGridlineStroke(final Stroke stroke) {
        getPlot().setDomainMinorGridlineStroke(stroke);
    }

    @Override
    public Paint getDomainGridlinePaint() {
        return getPlot().getDomainGridlinePaint();
    }

    @Override
    public void setDomainGridlinePaint(final Paint paint) {
        getPlot().setDomainGridlinePaint(paint);
    }

    @Override
    public Paint getDomainMinorGridlinePaint() {
        return getPlot().getDomainMinorGridlinePaint();
    }

    @Override
    public void setDomainMinorGridlinePaint(final Paint paint) {
        getPlot().setDomainMinorGridlinePaint(paint);
    }

    @Override
    public boolean isRangeGridlinesVisible() {
        return getPlot().isRangeGridlinesVisible();
    }

    @Override
    public void setRangeGridlinesVisible(final boolean visible) {
        getPlot().setRangeGridlinesVisible(visible);
    }

    @Override
    public Stroke getRangeGridlineStroke() {
        return getPlot().getRangeGridlineStroke();
    }

    @Override
    public void setRangeGridlineStroke(final Stroke stroke) {
        getPlot().setRangeGridlineStroke(stroke);
    }

    @Override
    public Paint getRangeGridlinePaint() {
        return getPlot().getRangeGridlinePaint();
    }

    @Override
    public void setRangeGridlinePaint(final Paint paint) {
        getPlot().setRangeGridlinePaint(paint);
    }

    @Override
    public boolean isRangeMinorGridlinesVisible() {
        return getPlot().isRangeMinorGridlinesVisible();
    }

    @Override
    public void setRangeMinorGridlinesVisible(final boolean visible) {
        getPlot().setRangeMinorGridlinesVisible(visible);
    }

    @Override
    public Stroke getRangeMinorGridlineStroke() {
        return getPlot().getRangeMinorGridlineStroke();
    }

    @Override
    public void setRangeMinorGridlineStroke(final Stroke stroke) {
        getPlot().setRangeMinorGridlineStroke(stroke);
    }

    @Override
    public Paint getRangeMinorGridlinePaint() {
        return getPlot().getRangeMinorGridlinePaint();
    }

    @Override
    public void setRangeMinorGridlinePaint(final Paint paint) {
        getPlot().setRangeMinorGridlinePaint(paint);
    }

    @Override
    public boolean isDomainZeroBaselineVisible() {
        return getPlot().isDomainZeroBaselineVisible();
    }

    @Override
    public void setDomainZeroBaselineVisible(final boolean visible) {
        getPlot().setDomainZeroBaselineVisible(visible);
    }

    @Override
    public Stroke getDomainZeroBaselineStroke() {
        return getPlot().getDomainZeroBaselineStroke();
    }

    @Override
    public void setDomainZeroBaselineStroke(final Stroke stroke) {
        getPlot().setDomainZeroBaselineStroke(stroke);
    }

    @Override
    public Paint getDomainZeroBaselinePaint() {
        return getPlot().getDomainZeroBaselinePaint();
    }

    @Override
    public void setDomainZeroBaselinePaint(final Paint paint) {
        getPlot().setDomainZeroBaselinePaint(paint);
    }

    @Override
    public boolean isRangeZeroBaselineVisible() {
        return getPlot().isRangeZeroBaselineVisible();
    }

    @Override
    public void setRangeZeroBaselineVisible(final boolean visible) {
        getPlot().setRangeZeroBaselineVisible(visible);
    }

    @Override
    public Stroke getRangeZeroBaselineStroke() {
        return getPlot().getRangeZeroBaselineStroke();
    }

    @Override
    public void setRangeZeroBaselineStroke(final Stroke stroke) {
        getPlot().setRangeZeroBaselineStroke(stroke);
    }

    @Override
    public Paint getRangeZeroBaselinePaint() {
        return getPlot().getRangeZeroBaselinePaint();
    }

    @Override
    public void setRangeZeroBaselinePaint(final Paint paint) {
        getPlot().setRangeZeroBaselinePaint(paint);
    }

    @Override
    public Paint getDomainTickBandPaint() {
        return getPlot().getDomainTickBandPaint();
    }

    @Override
    public void setDomainTickBandPaint(final Paint paint) {
        getPlot().setDomainTickBandPaint(paint);
    }

    @Override
    public Paint getRangeTickBandPaint() {
        return getPlot().getRangeTickBandPaint();
    }

    @Override
    public void setRangeTickBandPaint(final Paint paint) {
        getPlot().setRangeTickBandPaint(paint);
    }

    @Override
    public Point2D getQuadrantOrigin() {
        return getPlot().getQuadrantOrigin();
    }

    @Override
    public void setQuadrantOrigin(final Point2D origin) {
        getPlot().setQuadrantOrigin(origin);
    }

    @Override
    public Paint getQuadrantPaint(final int index) {
        return getPlot().getQuadrantPaint(index);
    }

    @Override
    public void setQuadrantPaint(final int index, final Paint paint) {
        getPlot().setQuadrantPaint(index, paint);
    }

    @Override
    public void addDomainMarker(final Marker marker) {
        getPlot().addDomainMarker(marker);
    }

    @Override
    public void addDomainMarker(final Marker marker, final Layer layer) {
        getPlot().addDomainMarker(marker, layer);
    }

    @Override
    public void clearDomainMarkers() {
        getPlot().clearDomainMarkers();
    }

    @Override
    public void clearDomainMarkers(final int index) {
        getPlot().clearDomainMarkers(index);
    }

    @Override
    public void addDomainMarker(final int index, final Marker marker, final Layer layer) {
        getPlot().addDomainMarker(index, marker, layer);
    }

    @Override
    public void addDomainMarker(final int index, final Marker marker, final Layer layer, final boolean notify) {
        getPlot().addDomainMarker(index, marker, layer, notify);
    }

    @Override
    public boolean removeDomainMarker(final Marker marker) {
        return getPlot().removeDomainMarker(marker);
    }

    @Override
    public boolean removeDomainMarker(final Marker marker, final Layer layer) {
        return getPlot().removeDomainMarker(marker, layer);
    }

    @Override
    public boolean removeDomainMarker(final int index, final Marker marker, final Layer layer) {
        return getPlot().removeDomainMarker(index, marker, layer);
    }

    @Override
    public boolean removeDomainMarker(final int index, final Marker marker, final Layer layer, final boolean notify) {
        return getPlot().removeDomainMarker(index, marker, layer, notify);
    }

    @Override
    public void addRangeMarker(final Marker marker) {
        getPlot().addRangeMarker(marker);
    }

    @Override
    public void addRangeMarker(final Marker marker, final Layer layer) {
        getPlot().addRangeMarker(marker, layer);
    }

    @Override
    public void clearRangeMarkers() {
        getPlot().clearRangeMarkers();
    }

    @Override
    public void addRangeMarker(final int index, final Marker marker, final Layer layer) {
        getPlot().addRangeMarker(index, marker, layer);
    }

    @Override
    public void addRangeMarker(final int index, final Marker marker, final Layer layer, final boolean notify) {
        getPlot().addRangeMarker(index, marker, layer, notify);
    }

    @Override
    public void clearRangeMarkers(final int index) {
        getPlot().clearRangeMarkers(index);
    }

    @Override
    public boolean removeRangeMarker(final Marker marker) {
        return getPlot().removeRangeMarker(marker);
    }

    @Override
    public boolean removeRangeMarker(final Marker marker, final Layer layer) {
        return getPlot().removeRangeMarker(marker, layer);
    }

    @Override
    public boolean removeRangeMarker(final int index, final Marker marker, final Layer layer) {
        return getPlot().removeRangeMarker(index, marker, layer);
    }

    @Override
    public boolean removeRangeMarker(final int index, final Marker marker, final Layer layer, final boolean notify) {
        return getPlot().removeRangeMarker(index, marker, layer, notify);
    }

    @Override
    public void addAnnotation(final Annotation annotation) {
        getPlot().addAnnotation((XYAnnotation) annotation);
    }

    @Override
    public void addAnnotation(final Annotation annotation, final boolean notify) {
        getPlot().addAnnotation((XYAnnotation) annotation, notify);
    }

    @Override
    public boolean removeAnnotation(final Annotation annotation) {
        return getPlot().removeAnnotation((XYAnnotation) annotation);
    }

    @Override
    public boolean removeAnnotation(final Annotation annotation, final boolean notify) {
        return getPlot().removeAnnotation((XYAnnotation) annotation, notify);
    }

    @Override
    public List<? extends Annotation> getAnnotations() {
        return getPlot().getAnnotations();
    }

    @Override
    public void clearAnnotations() {
        getPlot().clearAnnotations();
    }

    @Override
    public ShadowGenerator getShadowGenerator() {
        return getPlot().getShadowGenerator();
    }

    @Override
    public void setShadowGenerator(final ShadowGenerator generator) {
        getPlot().setShadowGenerator(generator);
    }

    @Override
    public void drawDomainTickBands(final Graphics2D g2, final Rectangle2D dataArea, final List<ValueTick> ticks) {
        getPlot().drawDomainTickBands(g2, dataArea, ticks);
    }

    @Override
    public void drawRangeTickBands(final Graphics2D g2, final Rectangle2D dataArea, final List<ValueTick> ticks) {
        getPlot().drawRangeTickBands(g2, dataArea, ticks);
    }

    @Override
    public boolean render(final Graphics2D g2, final Rectangle2D dataArea, final int index,
            final PlotRenderingInfo info, final CrosshairState crosshairState) {
        return getPlot().render(g2, dataArea, index, info, crosshairState);
    }

    @Override
    public Axis getDomainAxisForDataset(final int index) {
        return getPlot().getDomainAxisForDataset(index);
    }

    @Override
    public Axis getRangeAxisForDataset(final int index) {
        return getPlot().getRangeAxisForDataset(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Marker> getDomainMarkers(final Layer layer) {
        return getPlot().getDomainMarkers(layer);
    }

    @Override
    public Collection<Marker> getRangeMarkers(final Layer layer) {
        return getPlot().getRangeMarkers(layer);
    }

    @Override
    public Collection<? extends Marker> getDomainMarkers(final int index, final Layer layer) {
        return getPlot().getDomainMarkers(index, layer);
    }

    @Override
    public Collection<Marker> getRangeMarkers(final int index, final Layer layer) {
        return getPlot().getRangeMarkers(index, layer);
    }

    @Override
    public int getDomainAxisIndex(final Axis axis) {
        return getPlot().getDomainAxisIndex((ValueAxis) axis);
    }

    @Override
    public int getRangeAxisIndex(final Axis axis) {
        return getPlot().getRangeAxisIndex((ValueAxis) axis);
    }

    @Override
    public Range getDataRange(final Axis axis) {
        return getPlot().getDataRange((ValueAxis) axis);
    }

    @Override
    public void rendererChanged(final RendererChangeEvent event) {
        getPlot().rendererChanged(event);
    }

    @Override
    public boolean isDomainCrosshairVisible() {
        return getPlot().isDomainCrosshairVisible();
    }

    @Override
    public void setDomainCrosshairVisible(final boolean flag) {
        getPlot().setDomainCrosshairVisible(flag);
    }

    @Override
    public boolean isDomainCrosshairLockedOnData() {
        return getPlot().isDomainCrosshairLockedOnData();
    }

    @Override
    public void setDomainCrosshairLockedOnData(final boolean flag) {
        getPlot().setDomainCrosshairLockedOnData(flag);
    }

    @Override
    public double getDomainCrosshairValue() {
        return getPlot().getDomainCrosshairValue();
    }

    @Override
    public void setDomainCrosshairValue(final double value) {
        getPlot().setDomainCrosshairValue(value);
    }

    @Override
    public void setDomainCrosshairValue(final double value, final boolean notify) {
        getPlot().setDomainCrosshairValue(value, notify);
    }

    @Override
    public Stroke getDomainCrosshairStroke() {
        return getPlot().getDomainCrosshairStroke();
    }

    @Override
    public void setDomainCrosshairStroke(final Stroke stroke) {
        getPlot().setDomainCrosshairStroke(stroke);
    }

    @Override
    public Paint getDomainCrosshairPaint() {
        return getPlot().getDomainCrosshairPaint();
    }

    @Override
    public void setDomainCrosshairPaint(final Paint paint) {
        getPlot().setDomainCrosshairPaint(paint);
    }

    @Override
    public boolean isRangeCrosshairVisible() {
        return getPlot().isRangeCrosshairVisible();
    }

    @Override
    public void setRangeCrosshairVisible(final boolean flag) {
        getPlot().setRangeCrosshairVisible(flag);
    }

    @Override
    public boolean isRangeCrosshairLockedOnData() {
        return getPlot().isRangeCrosshairLockedOnData();
    }

    @Override
    public void setRangeCrosshairLockedOnData(final boolean flag) {
        getPlot().setRangeCrosshairLockedOnData(flag);
    }

    @Override
    public double getRangeCrosshairValue() {
        return getPlot().getRangeCrosshairValue();
    }

    @Override
    public void setRangeCrosshairValue(final double value) {
        getPlot().setRangeCrosshairValue(value);
    }

    @Override
    public void setRangeCrosshairValue(final double value, final boolean notify) {
        getPlot().setRangeCrosshairValue(value, notify);
    }

    @Override
    public Stroke getRangeCrosshairStroke() {
        return getPlot().getRangeCrosshairStroke();
    }

    @Override
    public void setRangeCrosshairStroke(final Stroke stroke) {
        getPlot().setRangeCrosshairStroke(stroke);
    }

    @Override
    public Paint getRangeCrosshairPaint() {
        return getPlot().getRangeCrosshairPaint();
    }

    @Override
    public void setRangeCrosshairPaint(final Paint paint) {
        getPlot().setRangeCrosshairPaint(paint);
    }

    @Override
    public AxisSpace getFixedDomainAxisSpace() {
        return getPlot().getFixedDomainAxisSpace();
    }

    @Override
    public void setFixedDomainAxisSpace(final AxisSpace space) {
        getPlot().setFixedDomainAxisSpace(space);
    }

    @Override
    public void setFixedDomainAxisSpace(final AxisSpace space, final boolean notify) {
        getPlot().setFixedDomainAxisSpace(space, notify);
    }

    @Override
    public AxisSpace getFixedRangeAxisSpace() {
        return getPlot().getFixedRangeAxisSpace();
    }

    @Override
    public void setFixedRangeAxisSpace(final AxisSpace space) {
        getPlot().setFixedRangeAxisSpace(space);
    }

    @Override
    public void setFixedRangeAxisSpace(final AxisSpace space, final boolean notify) {
        getPlot().setFixedRangeAxisSpace(space, notify);
    }

    @Override
    public boolean isDomainPannable() {
        return getPlot().isDomainPannable();
    }

    @Override
    public void setDomainPannable(final boolean pannable) {
        getPlot().setDomainPannable(pannable);
    }

    @Override
    public boolean isRangePannable() {
        return getPlot().isRangePannable();
    }

    @Override
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

    @Override
    public int getSeriesCount() {
        return getPlot().getSeriesCount();
    }

    @Override
    public LegendItemCollection getFixedLegendItems() {
        return getPlot().getFixedLegendItems();
    }

    @Override
    public void setFixedLegendItems(final LegendItemCollection items) {
        getPlot().setFixedLegendItems(items);
    }

}
