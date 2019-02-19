package de.invesdwin.context.jfreechart.renderer;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.HashUtils;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.ui.GradientPaintTransformer;
import org.jfree.chart.ui.StandardGradientPaintTransformer;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.SerialUtils;
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.panel.helper.config.StrokeType;

@NotThreadSafe
public class CustomXYAreaRenderer extends AbstractXYItemRenderer implements XYItemRenderer, PublicCloneable {

    /**
     * A state object used by this renderer.
     */
    private static final class XYAreaRendererState extends XYItemRendererState {

        /** Working storage for the area under one series. */
        private GeneralPath area;

        /** Working line that can be recycled. */
        private final Line2D line;

        /**
         * Creates a new state.
         *
         * @param info
         *            the plot rendering info.
         */
        private XYAreaRendererState(final PlotRenderingInfo info) {
            super(info);
            this.area = new GeneralPath();
            this.line = new Line2D.Double();
        }

    }

    /** A flag indicating whether or not lines are drawn between XY points. */
    private final boolean plotLines;

    /** A flag indicating whether or not Area are drawn at each XY point. */
    private final boolean plotArea;

    /**
     * The shape used to represent an area in each legend item (this should never be {@code null}).
     */
    private transient Shape legendArea;

    /**
     * A transformer that is applied to the paint used to fill under the area *if* it is an instance of GradientPaint.
     *
     * @since 1.0.14
     */
    private GradientPaintTransformer gradientTransformer;

    /**
     * Constructs a new renderer.
     *
     * @param type
     *            the type of the renderer.
     */
    public CustomXYAreaRenderer() {
        this(null, null);
    }

    /**
     * Constructs a new renderer. To specify the type of renderer, use one of the constants: {@code SHAPES},
     * {@code LINES}, {@code SHAPES_AND_LINES}, {@code AREA} or {@code AREA_AND_SHAPES}.
     *
     * @param type
     *            the type of renderer.
     * @param toolTipGenerator
     *            the tool tip generator ({@code null} permitted).
     * @param urlGenerator
     *            the URL generator ({@code null} permitted).
     */
    public CustomXYAreaRenderer(final XYToolTipGenerator toolTipGenerator, final XYURLGenerator urlGenerator) {

        super();
        setDefaultToolTipGenerator(toolTipGenerator);
        setURLGenerator(urlGenerator);

        this.plotArea = true;
        this.plotLines = true;

        final GeneralPath area = new GeneralPath();
        area.moveTo(0.0f, -4.0f);
        area.lineTo(3.0f, -2.0f);
        area.lineTo(4.0f, 4.0f);
        area.lineTo(-4.0f, 4.0f);
        area.lineTo(-3.0f, -2.0f);
        area.closePath();
        this.legendArea = area;
        this.gradientTransformer = new StandardGradientPaintTransformer();

        setSeriesStroke(0, StrokeType.Solid.getStroke());
    }

    /**
     * Returns true if lines are being plotted by the renderer.
     *
     * @return {@code true} if lines are being plotted by the renderer.
     */
    public boolean getPlotLines() {
        return this.plotLines;
    }

    /**
     * Returns true if Area is being plotted by the renderer.
     *
     * @return {@code true} if Area is being plotted by the renderer.
     */
    public boolean getPlotArea() {
        return this.plotArea;
    }

    /**
     * Returns the shape used to represent an area in the legend.
     *
     * @return The legend area (never {@code null}).
     */
    public Shape getLegendArea() {
        return this.legendArea;
    }

    /**
     * Sets the shape used as an area in each legend item and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param area
     *            the area ({@code null} not permitted).
     */
    public void setLegendArea(final Shape area) {
        Args.nullNotPermitted(area, "area");
        this.legendArea = area;
        fireChangeEvent();
    }

    /**
     * Returns the gradient paint transformer.
     *
     * @return The gradient paint transformer (never {@code null}).
     *
     * @since 1.0.14
     */
    public GradientPaintTransformer getGradientTransformer() {
        return this.gradientTransformer;
    }

    /**
     * Sets the gradient paint transformer and sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param transformer
     *            the transformer ({@code null} not permitted).
     *
     * @since 1.0.14
     */
    public void setGradientTransformer(final GradientPaintTransformer transformer) {
        Args.nullNotPermitted(transformer, "transformer");
        this.gradientTransformer = transformer;
        fireChangeEvent();
    }

    /**
     * Initialises the renderer and returns a state object that should be passed to all subsequent calls to the
     * drawItem() method.
     *
     * @param g2
     *            the graphics device.
     * @param dataArea
     *            the area inside the axes.
     * @param plot
     *            the plot.
     * @param data
     *            the data.
     * @param info
     *            an optional info collection object to return data back to the caller.
     *
     * @return A state object for use by the renderer.
     */
    @Override
    public XYItemRendererState initialise(final Graphics2D g2, final Rectangle2D dataArea, final XYPlot plot,
            final XYDataset data, final PlotRenderingInfo info) {
        final XYAreaRendererState state = new XYAreaRendererState(info);

        // in the rendering process, there is special handling for item
        // zero, so we can't support processing of visible data items only
        state.setProcessVisibleItemsOnly(false);
        return state;
    }

    /**
     * Returns a default legend item for the specified series. Subclasses should override this method to generate
     * customised items.
     *
     * @param datasetIndex
     *            the dataset index (zero-based).
     * @param series
     *            the series index (zero-based).
     *
     * @return A legend item for the series.
     */
    @Override
    public LegendItem getLegendItem(final int datasetIndex, final int series) {
        LegendItem result = null;
        final XYPlot xyplot = getPlot();
        if (xyplot != null) {
            final XYDataset dataset = xyplot.getDataset(datasetIndex);
            if (dataset != null) {
                final XYSeriesLabelGenerator lg = getLegendItemLabelGenerator();
                final String label = lg.generateLabel(dataset, series);
                final String description = label;
                String toolTipText = null;
                if (getLegendItemToolTipGenerator() != null) {
                    toolTipText = getLegendItemToolTipGenerator().generateLabel(dataset, series);
                }
                String urlText = null;
                if (getLegendItemURLGenerator() != null) {
                    urlText = getLegendItemURLGenerator().generateLabel(dataset, series);
                }
                final Paint paint = lookupSeriesPaint(series);
                result = new LegendItem(label, description, toolTipText, urlText, this.legendArea, paint);
                result.setLabelFont(lookupLegendTextFont(series));
                final Paint labelPaint = lookupLegendTextPaint(series);
                if (labelPaint != null) {
                    result.setLabelPaint(labelPaint);
                }
                result.setDataset(dataset);
                result.setDatasetIndex(datasetIndex);
                result.setSeriesKey(dataset.getSeriesKey(series));
                result.setSeriesIndex(series);
            }
        }
        return result;
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2
     *            the graphics device.
     * @param state
     *            the renderer state.
     * @param dataArea
     *            the area within which the data is being drawn.
     * @param info
     *            collects information about the drawing.
     * @param plot
     *            the plot (can be used to obtain standard color information etc).
     * @param domainAxis
     *            the domain axis.
     * @param rangeAxis
     *            the range axis.
     * @param dataset
     *            the dataset.
     * @param series
     *            the series index (zero-based).
     * @param item
     *            the item index (zero-based).
     * @param crosshairState
     *            crosshair information for the plot ({@code null} permitted).
     * @param pass
     *            the pass index.
     */
    //CHECKSTYLE:OFF
    @Override
    public void drawItem(final Graphics2D g2, final XYItemRendererState state, final Rectangle2D dataArea,
            final PlotRenderingInfo info, final XYPlot plot, final ValueAxis domainAxis, final ValueAxis rangeAxis,
            final XYDataset dataset, final int series, final int item, final CrosshairState crosshairState,
            final int pass) {
        //CHECKSTYLE:ON

        if (!getItemVisible(series, item)) {
            return;
        }
        final XYAreaRendererState areaState = (XYAreaRendererState) state;

        // get the data point...
        final double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1)) {
            y1 = 0.0;
        }
        final double transX1 = domainAxis.valueToJava2D(x1, dataArea, plot.getDomainAxisEdge());
        final double transY1 = rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge());

        // get the previous point and the next point so we can calculate a
        // "hot spot" for the area (used by the chart entity)...
        final int itemCount = dataset.getItemCount(series);
        final double x0 = dataset.getXValue(series, Math.max(item - 1, 0));
        double y0 = dataset.getYValue(series, Math.max(item - 1, 0));
        if (Double.isNaN(y0)) {
            y0 = 0.0;
        }
        final double transX0 = domainAxis.valueToJava2D(x0, dataArea, plot.getDomainAxisEdge());
        final double transY0 = rangeAxis.valueToJava2D(y0, dataArea, plot.getRangeAxisEdge());

        final double x2 = dataset.getXValue(series, Math.min(item + 1, itemCount - 1));
        double y2 = dataset.getYValue(series, Math.min(item + 1, itemCount - 1));
        if (Double.isNaN(y2)) {
            y2 = 0.0;
        }
        final double transX2 = domainAxis.valueToJava2D(x2, dataArea, plot.getDomainAxisEdge());
        final double transY2 = rangeAxis.valueToJava2D(y2, dataArea, plot.getRangeAxisEdge());

        final double transZero = rangeAxis.valueToJava2D(0.0, dataArea, plot.getRangeAxisEdge());

        if (item == 0) { // create a new area polygon for the series
            areaState.area = new GeneralPath();
            // the first point is (x, 0)
            final double zero = rangeAxis.valueToJava2D(0.0, dataArea, plot.getRangeAxisEdge());
            if (plot.getOrientation().isVertical()) {
                moveTo(areaState.area, transX1, zero);
            } else if (plot.getOrientation().isHorizontal()) {
                moveTo(areaState.area, zero, transX1);
            }
        }

        // Add each point to Area (x, y)
        if (plot.getOrientation().isVertical()) {
            lineTo(areaState.area, transX1, transY1);
        } else if (plot.getOrientation().isHorizontal()) {
            lineTo(areaState.area, transY1, transX1);
        }

        final PlotOrientation orientation = plot.getOrientation();
        Paint paint = getItemPaint(series, item);
        final Stroke stroke = getItemStroke(series, item);
        g2.setPaint(paint);
        g2.setStroke(stroke);

        if (getPlotLines()) {
            if (item > 0) {
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    areaState.line.setLine(transX0, transY0, transX1, transY1);
                } else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                    areaState.line.setLine(transY0, transX0, transY1, transX1);
                }
                g2.draw(areaState.line);
            }
        }

        // Check if the item is the last item for the series.
        // and number of items > 0.  We can't draw an area for a single point.
        if (getPlotArea() && item > 0 && item == (itemCount - 1)) {

            if (orientation == PlotOrientation.VERTICAL) {
                // Add the last point (x,0)
                lineTo(areaState.area, transX1, transZero);
                areaState.area.closePath();
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                // Add the last point (x,0)
                lineTo(areaState.area, transZero, transX1);
                areaState.area.closePath();
            }

            paint = lookupSeriesFillPaint(series);
            if (paint instanceof GradientPaint) {
                final GradientPaint gp = (GradientPaint) paint;
                final GradientPaint adjGP = this.gradientTransformer.transform(gp, dataArea);
                g2.setPaint(adjGP);
            } else {
                g2.setPaint(paint);
            }
            g2.fill(areaState.area);
        }

        final int datasetIndex = plot.indexOf(dataset);
        updateCrosshairValues(crosshairState, x1, y1, datasetIndex, transX1, transY1, orientation);

        // collect entity and tool tip information...
        final EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            final GeneralPath hotspot = new GeneralPath();
            if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                moveTo(hotspot, transZero, ((transX0 + transX1) / 2.0));
                lineTo(hotspot, ((transY0 + transY1) / 2.0), ((transX0 + transX1) / 2.0));
                lineTo(hotspot, transY1, transX1);
                lineTo(hotspot, ((transY1 + transY2) / 2.0), ((transX1 + transX2) / 2.0));
                lineTo(hotspot, transZero, ((transX1 + transX2) / 2.0));
            } else { // vertical orientation
                moveTo(hotspot, ((transX0 + transX1) / 2.0), transZero);
                lineTo(hotspot, ((transX0 + transX1) / 2.0), ((transY0 + transY1) / 2.0));
                lineTo(hotspot, transX1, transY1);
                lineTo(hotspot, ((transX1 + transX2) / 2.0), ((transY1 + transY2) / 2.0));
                lineTo(hotspot, ((transX1 + transX2) / 2.0), transZero);
            }
            hotspot.closePath();

            // limit the entity hotspot area to the data area
            final Area dataAreaHotspot = new Area(hotspot);
            dataAreaHotspot.intersect(new Area(dataArea));

            if (!dataAreaHotspot.isEmpty()) {
                addEntity(entities, dataAreaHotspot, dataset, series, item, 0.0, 0.0);
            }
        }

    }

    /**
     * Returns a clone of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException
     *             if the renderer cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        final CustomXYAreaRenderer clone = (CustomXYAreaRenderer) super.clone();
        clone.legendArea = ShapeUtils.clone(this.legendArea);
        return clone;
    }

    /**
     * Tests this renderer for equality with an arbitrary object.
     *
     * @param obj
     *            the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    //CHECKSTYLE:OFF
    @Override
    public boolean equals(final Object obj) {
        //CHECKSTYLE:ON
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CustomXYAreaRenderer)) {
            return false;
        }
        final CustomXYAreaRenderer that = (CustomXYAreaRenderer) obj;
        if (this.plotArea != that.plotArea) {
            return false;
        }
        if (this.plotLines != that.plotLines) {
            return false;
        }
        if (!this.gradientTransformer.equals(that.gradientTransformer)) {
            return false;
        }
        if (!ShapeUtils.equal(this.legendArea, that.legendArea)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code for this instance.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = HashUtils.hashCode(result, this.plotArea);
        result = HashUtils.hashCode(result, this.plotLines);
        return result;
    }

    /**
     * Provides serialization support.
     *
     * @param stream
     *            the input stream.
     *
     * @throws IOException
     *             if there is an I/O error.
     * @throws ClassNotFoundException
     *             if there is a classpath problem.
     */
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendArea = SerialUtils.readShape(stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream
     *            the output stream.
     *
     * @throws IOException
     *             if there is an I/O error.
     */
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtils.writeShape(this.legendArea, stream);
    }
}