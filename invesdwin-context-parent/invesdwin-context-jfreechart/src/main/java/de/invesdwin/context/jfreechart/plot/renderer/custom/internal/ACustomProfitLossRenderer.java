package de.invesdwin.context.jfreechart.plot.renderer.custom.internal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

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
import org.jfree.chart.renderer.RendererState;
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
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.XYDataset;

import de.invesdwin.context.jfreechart.panel.helper.config.PriceInitialSettings;
import de.invesdwin.context.jfreechart.plot.dataset.PlotSourceXYSeriesCollection;
import de.invesdwin.context.jfreechart.plot.dataset.basis.ListXYSeriesOHLC;
import de.invesdwin.context.jfreechart.plot.dataset.basis.XYDataItemOHLC;
import de.invesdwin.context.jfreechart.plot.renderer.DisabledXYItemRenderer;
import de.invesdwin.context.jfreechart.plot.renderer.IUpDownColorRenderer;
import de.invesdwin.util.math.Doubles;

/**
 * Low is Loss, Close is Profit.
 */
@NotThreadSafe
public abstract class ACustomProfitLossRenderer extends AbstractXYItemRenderer
        implements XYItemRenderer, PublicCloneable, IUpDownColorRenderer {

    private static final Color INVISIBLE_COLOR = DisabledXYItemRenderer.INVISIBLE_COLOR;

    private static final class XYAreaRendererState extends XYItemRendererState {

        private final XYAreaRendererStateData profit;
        private final XYAreaRendererStateData loss;

        private XYAreaRendererState(final PlotRenderingInfo info) {
            super(info);
            this.profit = new XYAreaRendererStateData();
            this.loss = new XYAreaRendererStateData();
        }

    }

    private static final class XYAreaRendererStateData {
        private GeneralPath area;
        private final Line2D line;

        private XYAreaRendererStateData() {
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
    public ACustomProfitLossRenderer() {
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
    public ACustomProfitLossRenderer(final XYToolTipGenerator toolTipGenerator, final XYURLGenerator urlGenerator) {

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

        setSeriesStroke(0, PriceInitialSettings.DEFAULT_SERIES_STROKE);
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
            final XYDataset dataset, final int series, final int item1, final CrosshairState crosshairState,
            final int pass) {
        //CHECKSTYLE:ON

        if (!getItemVisible(series, item1)) {
            return;
        }
        final XYAreaRendererState areaState = (XYAreaRendererState) state;

        final Color upColor;
        final Color downColor;
        // get the data point...
        final PlotSourceXYSeriesCollection cDataset = (PlotSourceXYSeriesCollection) dataset;
        final ListXYSeriesOHLC cSeries = cDataset.getSeries(series);
        final List<XYDataItemOHLC> data = cSeries.getData();
        final int item0 = Math.max(item1 - 1, 0);
        final OHLCDataItem cItem0 = data.get(item0).asOHLC();
        final OHLCDataItem cItem1 = data.get(item1).asOHLC();

        final double x1 = dataset.getXValue(series, item1);
        if (Double.isNaN(cItem1.getClose().doubleValue())) {
            upColor = INVISIBLE_COLOR;
            downColor = INVISIBLE_COLOR;
        } else {
            upColor = getUpColor();
            downColor = getDownColor();
        }
        final double x0 = dataset.getXValue(series, item0);

        //profit to profit
        drawProfitLoss(g2, dataArea, plot, domainAxis, rangeAxis, series, item1, areaState.profit, x0,
                convert(cItem0.getClose()), x1, convert(cItem1.getClose()), dataset, state, upColor);
        drawProfitLoss(g2, dataArea, plot, domainAxis, rangeAxis, series, item1, areaState.loss, x0,
                convert(cItem0.getLow()), x1, convert(cItem1.getLow()), dataset, state, upColor);

        // Check if the item is the last item for the series.
        // and number of items > 0.  We can't draw an area for a single point.
        if (getPlotArea() && item1 > 0 && item1 == data.size() - 1) {
            closeArea(g2, dataArea, plot, domainAxis, rangeAxis, upColor, x1, areaState.profit);
            closeArea(g2, dataArea, plot, domainAxis, rangeAxis, downColor, x1, areaState.loss);
        }
    }

    private double convert(final Number value) {
        return Doubles.nanToZero(value.doubleValue());
    }

    //CHECKSTYLE:OFF
    private void closeArea(final Graphics2D g2, final Rectangle2D dataArea, final XYPlot plot,
            final ValueAxis domainAxis, final ValueAxis rangeAxis, final Color upColor, final double x1,
            final XYAreaRendererStateData areaStateData) {
        //CHECKSTYLE:ON
        final double transX1 = domainAxis.valueToJava2D(x1, dataArea, plot.getDomainAxisEdge());
        final double transZero = rangeAxis.valueToJava2D(0.0, dataArea, plot.getRangeAxisEdge());

        final PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.VERTICAL) {
            // Add the last point (x,0)
            lineTo(areaStateData.area, transX1, transZero);
            areaStateData.area.closePath();
        } else if (orientation == PlotOrientation.HORIZONTAL) {
            // Add the last point (x,0)
            lineTo(areaStateData.area, transZero, transX1);
            areaStateData.area.closePath();
        }

        g2.setPaint(upColor);
        g2.fill(areaStateData.area);
    }

    //CHECKSTYLE:OFF
    private void drawProfitLoss(final Graphics2D g2, final Rectangle2D dataArea, final XYPlot plot,
            final ValueAxis domainAxis, final ValueAxis rangeAxis, final int series, final int item,
            final XYAreaRendererStateData areaStateData, final double x0, final double y0, final double x1,
            final double y1, final XYDataset dataset, final RendererState state, final Paint paint) {
        //CHECKSTYLE:ON

        final double transX1 = domainAxis.valueToJava2D(x1, dataArea, plot.getDomainAxisEdge());
        final double transY1 = rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge());

        // get the previous point and the next point so we can calculate a
        // "hot spot" for the area (used by the chart entity)...
        final double transX0 = domainAxis.valueToJava2D(x0, dataArea, plot.getDomainAxisEdge());
        final double transY0 = rangeAxis.valueToJava2D(y0, dataArea, plot.getRangeAxisEdge());

        if (item == 0) { // create a new area polygon for the series
            areaStateData.area = new GeneralPath();
            // the first point is (x, 0)
            final double zero = rangeAxis.valueToJava2D(0.0, dataArea, plot.getRangeAxisEdge());
            if (plot.getOrientation().isVertical()) {
                moveTo(areaStateData.area, transX1, zero);
            } else if (plot.getOrientation().isHorizontal()) {
                moveTo(areaStateData.area, zero, transX1);
            }
        }

        // Add each point to Area (x, y)
        if (plot.getOrientation().isVertical()) {
            lineTo(areaStateData.area, transX1, transY1);
        } else if (plot.getOrientation().isHorizontal()) {
            lineTo(areaStateData.area, transY1, transX1);
        }

        final Stroke stroke = getItemStroke(series, item);
        g2.setPaint(paint);
        g2.setStroke(stroke);

        if (getPlotLines()) {
            if (item > 0) {
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    areaStateData.line.setLine(transX0, transY0, transX1, transY1);
                } else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                    areaStateData.line.setLine(transY0, transX0, transY1, transX1);
                }
                g2.draw(areaStateData.line);
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
        final ACustomProfitLossRenderer clone = (ACustomProfitLossRenderer) super.clone();
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
        if (!(obj instanceof ACustomProfitLossRenderer)) {
            return false;
        }
        final ACustomProfitLossRenderer that = (ACustomProfitLossRenderer) obj;
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

    @Override
    protected void updateCrosshairValues(final CrosshairState crosshairState, final double x, final double y,
            final int datasetIndex, final double transX, final double transY, final PlotOrientation orientation) {
        //noop
    }

    @Override
    protected void addEntity(final EntityCollection entities, final Shape hotspot, final XYDataset dataset,
            final int series, final int item, final double entityX, final double entityY) {
        //noop
    }

}
