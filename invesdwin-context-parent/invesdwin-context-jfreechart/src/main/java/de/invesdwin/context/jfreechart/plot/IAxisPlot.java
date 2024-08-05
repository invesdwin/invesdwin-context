package de.invesdwin.context.jfreechart.plot;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.annotations.Annotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Pannable;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.Zoomable;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.util.ShadowGenerator;
import org.jfree.data.Range;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;

public interface IAxisPlot extends IPlot, Pannable, Zoomable, RendererChangeListener {

    void setOrientation(PlotOrientation orientation);

    RectangleInsets getAxisOffset();

    void setAxisOffset(RectangleInsets offset);

    Axis getDomainAxis();

    Axis getDomainAxis(int index);

    Map<Integer, ? extends Axis> getDomainAxes();

    void setDomainAxis(Axis axis);

    void setDomainAxis(int index, Axis axis);

    void setDomainAxis(int index, Axis axis, boolean notify);

    void setDomainAxes(Axis[] axes);

    AxisLocation getDomainAxisLocation();

    void setDomainAxisLocation(AxisLocation location);

    void setDomainAxisLocation(AxisLocation location, boolean notify);

    RectangleEdge getDomainAxisEdge();

    int getDomainAxisCount();

    void clearDomainAxes();

    void configureDomainAxes();

    AxisLocation getDomainAxisLocation(int index);

    void setDomainAxisLocation(int index, AxisLocation location);

    void setDomainAxisLocation(int index, AxisLocation location, boolean notify);

    RectangleEdge getDomainAxisEdge(int index);

    Axis getRangeAxis();

    void setRangeAxis(Axis axis);

    AxisLocation getRangeAxisLocation();

    void setRangeAxisLocation(AxisLocation location);

    void setRangeAxisLocation(AxisLocation location, boolean notify);

    RectangleEdge getRangeAxisEdge();

    Axis getRangeAxis(int index);

    Map<Integer, ? extends Axis> getRangeAxes();

    void setRangeAxis(int index, Axis axis);

    void setRangeAxis(int index, Axis axis, boolean notify);

    void setRangeAxes(Axis[] axes);

    int getRangeAxisCount();

    void clearRangeAxes();

    void configureRangeAxes();

    AxisLocation getRangeAxisLocation(int index);

    void setRangeAxisLocation(int index, AxisLocation location);

    void setRangeAxisLocation(int index, AxisLocation location, boolean notify);

    RectangleEdge getRangeAxisEdge(int index);

    Dataset getDataset();

    Dataset getDataset(int index);

    Map<Integer, ? extends Dataset> getDatasets();

    void setDataset(Dataset dataset);

    void setDataset(int index, XYDataset dataset);

    int getDatasetCount();

    int indexOf(Dataset dataset);

    void mapDatasetToDomainAxis(int index, int axisIndex);

    void mapDatasetToDomainAxes(int index, List<Integer> axisIndices);

    void mapDatasetToRangeAxis(int index, int axisIndex);

    void mapDatasetToRangeAxes(int index, List<Integer> axisIndices);

    int getRendererCount();

    LegendItemSource getRenderer();

    LegendItemSource getRenderer(int index);

    Map<Integer, ? extends LegendItemSource> getRenderers();

    void setRenderer(LegendItemSource renderer);

    void setRenderer(int index, LegendItemSource renderer);

    void setRenderer(int index, LegendItemSource renderer, boolean notify);

    void setRenderers(LegendItemSource[] renderers);

    DatasetRenderingOrder getDatasetRenderingOrder();

    void setDatasetRenderingOrder(DatasetRenderingOrder order);

    SeriesRenderingOrder getSeriesRenderingOrder();

    void setSeriesRenderingOrder(SeriesRenderingOrder order);

    int getIndexOf(LegendItemSource renderer);

    LegendItemSource getRendererForDataset(Dataset dataset);

    int getWeight();

    void setWeight(int weight);

    boolean isDomainGridlinesVisible();

    void setDomainGridlinesVisible(boolean visible);

    boolean isDomainMinorGridlinesVisible();

    void setDomainMinorGridlinesVisible(boolean visible);

    Stroke getDomainGridlineStroke();

    void setDomainGridlineStroke(Stroke stroke);

    Stroke getDomainMinorGridlineStroke();

    void setDomainMinorGridlineStroke(Stroke stroke);

    Paint getDomainGridlinePaint();

    void setDomainGridlinePaint(Paint paint);

    Paint getDomainMinorGridlinePaint();

    void setDomainMinorGridlinePaint(Paint paint);

    boolean isRangeGridlinesVisible();

    void setRangeGridlinesVisible(boolean visible);

    Stroke getRangeGridlineStroke();

    void setRangeGridlineStroke(Stroke stroke);

    Paint getRangeGridlinePaint();

    void setRangeGridlinePaint(Paint paint);

    boolean isRangeMinorGridlinesVisible();

    void setRangeMinorGridlinesVisible(boolean visible);

    Stroke getRangeMinorGridlineStroke();

    void setRangeMinorGridlineStroke(Stroke stroke);

    Paint getRangeMinorGridlinePaint();

    void setRangeMinorGridlinePaint(Paint paint);

    boolean isDomainZeroBaselineVisible();

    void setDomainZeroBaselineVisible(boolean visible);

    Stroke getDomainZeroBaselineStroke();

    void setDomainZeroBaselineStroke(Stroke stroke);

    Paint getDomainZeroBaselinePaint();

    void setDomainZeroBaselinePaint(Paint paint);

    boolean isRangeZeroBaselineVisible();

    void setRangeZeroBaselineVisible(boolean visible);

    Stroke getRangeZeroBaselineStroke();

    void setRangeZeroBaselineStroke(Stroke stroke);

    Paint getRangeZeroBaselinePaint();

    void setRangeZeroBaselinePaint(Paint paint);

    Paint getDomainTickBandPaint();

    void setDomainTickBandPaint(Paint paint);

    Paint getRangeTickBandPaint();

    void setRangeTickBandPaint(Paint paint);

    Point2D getQuadrantOrigin();

    void setQuadrantOrigin(Point2D origin);

    Paint getQuadrantPaint(int index);

    void setQuadrantPaint(int index, Paint paint);

    void addDomainMarker(Marker marker);

    void addDomainMarker(Marker marker, Layer layer);

    void clearDomainMarkers();

    void clearDomainMarkers(int index);

    void addDomainMarker(int index, Marker marker, Layer layer);

    void addDomainMarker(int index, Marker marker, Layer layer, boolean notify);

    boolean removeDomainMarker(Marker marker);

    boolean removeDomainMarker(Marker marker, Layer layer);

    boolean removeDomainMarker(int index, Marker marker, Layer layer);

    boolean removeDomainMarker(int index, Marker marker, Layer layer, boolean notify);

    void addRangeMarker(Marker marker);

    void addRangeMarker(Marker marker, Layer layer);

    void clearRangeMarkers();

    void addRangeMarker(int index, Marker marker, Layer layer);

    void addRangeMarker(int index, Marker marker, Layer layer, boolean notify);

    void clearRangeMarkers(int index);

    boolean removeRangeMarker(Marker marker);

    boolean removeRangeMarker(Marker marker, Layer layer);

    boolean removeRangeMarker(int index, Marker marker, Layer layer);

    boolean removeRangeMarker(int index, Marker marker, Layer layer, boolean notify);

    void addAnnotation(Annotation annotation);

    void addAnnotation(Annotation annotation, boolean notify);

    boolean removeAnnotation(Annotation annotation);

    boolean removeAnnotation(Annotation annotation, boolean notify);

    List<? extends Annotation> getAnnotations();

    void clearAnnotations();

    ShadowGenerator getShadowGenerator();

    void setShadowGenerator(ShadowGenerator generator);

    void drawDomainTickBands(Graphics2D g2, Rectangle2D dataArea, List<ValueTick> ticks);

    void drawRangeTickBands(Graphics2D g2, Rectangle2D dataArea, List<ValueTick> ticks);

    boolean render(Graphics2D g2, Rectangle2D dataArea, int index, PlotRenderingInfo info,
            CrosshairState crosshairState);

    Axis getDomainAxisForDataset(int index);

    Axis getRangeAxisForDataset(int index);

    Collection<Marker> getDomainMarkers(Layer layer);

    Collection<Marker> getRangeMarkers(Layer layer);

    Collection<? extends Marker> getDomainMarkers(int index, Layer layer);

    Collection<Marker> getRangeMarkers(int index, Layer layer);

    int getDomainAxisIndex(Axis axis);

    int getRangeAxisIndex(Axis axis);

    Range getDataRange(Axis axis);

    boolean isDomainCrosshairVisible();

    void setDomainCrosshairVisible(boolean flag);

    boolean isDomainCrosshairLockedOnData();

    void setDomainCrosshairLockedOnData(boolean flag);

    double getDomainCrosshairValue();

    void setDomainCrosshairValue(double value);

    void setDomainCrosshairValue(double value, boolean notify);

    Stroke getDomainCrosshairStroke();

    void setDomainCrosshairStroke(Stroke stroke);

    Paint getDomainCrosshairPaint();

    void setDomainCrosshairPaint(Paint paint);

    boolean isRangeCrosshairVisible();

    void setRangeCrosshairVisible(boolean flag);

    boolean isRangeCrosshairLockedOnData();

    void setRangeCrosshairLockedOnData(boolean flag);

    double getRangeCrosshairValue();

    void setRangeCrosshairValue(double value);

    void setRangeCrosshairValue(double value, boolean notify);

    Stroke getRangeCrosshairStroke();

    void setRangeCrosshairStroke(Stroke stroke);

    Paint getRangeCrosshairPaint();

    void setRangeCrosshairPaint(Paint paint);

    AxisSpace getFixedDomainAxisSpace();

    void setFixedDomainAxisSpace(AxisSpace space);

    void setFixedDomainAxisSpace(AxisSpace space, boolean notify);

    AxisSpace getFixedRangeAxisSpace();

    void setFixedRangeAxisSpace(AxisSpace space);

    void setFixedRangeAxisSpace(AxisSpace space, boolean notify);

    void setDomainPannable(boolean pannable);

    void setRangePannable(boolean pannable);

    int getSeriesCount();

    LegendItemCollection getFixedLegendItems();

    void setFixedLegendItems(LegendItemCollection items);

}
