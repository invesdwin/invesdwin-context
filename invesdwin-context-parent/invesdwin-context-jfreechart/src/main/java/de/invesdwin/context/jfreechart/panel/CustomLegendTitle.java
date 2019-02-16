package de.invesdwin.context.jfreechart.panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.annotation.concurrent.NotThreadSafe;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.block.Arrangement;
import org.jfree.chart.block.Block;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BlockFrame;
import org.jfree.chart.block.BlockResult;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.CenterArrangement;
import org.jfree.chart.block.ColumnArrangement;
import org.jfree.chart.block.EntityBlockParams;
import org.jfree.chart.block.LabelBlock;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.entity.TitleEntity;
import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.title.LegendGraphic;
import org.jfree.chart.title.LegendItemBlockContainer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.Title;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.Size2D;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.PaintUtils;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.SerialUtils;
import org.jfree.chart.util.SortOrder;

@NotThreadSafe
public class CustomLegendTitle extends Title implements Cloneable, PublicCloneable, Serializable {

    /** The default item font. */
    public static final Font DEFAULT_ITEM_FONT = new Font("SansSerif", Font.PLAIN, 12);

    /** The default item paint. */
    public static final Paint DEFAULT_ITEM_PAINT = Color.black;

    /** The sources for legend items. */
    private LegendItemSource[] sources;

    /** The background paint (possibly <code>null</code>). */
    private transient Paint backgroundPaint;

    /** The edge for the legend item graphic relative to the text. */
    private RectangleEdge legendItemGraphicEdge;

    /** The anchor point for the legend item graphic. */
    private RectangleAnchor legendItemGraphicAnchor;

    /** The legend item graphic location. */
    private RectangleAnchor legendItemGraphicLocation;

    /** The padding for the legend item graphic. */
    private RectangleInsets legendItemGraphicPadding;

    /** The item font. */
    private Font itemFont;

    /** The item paint. */
    private transient Paint itemPaint;

    /** The padding for the item labels. */
    private RectangleInsets itemLabelPadding;

    /**
     * A container that holds and displays the legend items.
     */
    private final BlockContainer items;

    /**
     * The layout for the legend when it is positioned at the top or bottom of the chart.
     */
    private final Arrangement hLayout;

    /**
     * The layout for the legend when it is positioned at the left or right of the chart.
     */
    private final Arrangement vLayout;

    /**
     * An optional container for wrapping the legend items (allows for adding a title or other text to the legend).
     */
    private BlockContainer wrapper;

    /**
     * Whether to render legend items in ascending or descending order.
     * 
     * @since 1.0.15
     */
    private SortOrder sortOrder;

    /**
     * Constructs a new (empty) legend for the specified source.
     *
     * @param source
     *            the source.
     */
    public CustomLegendTitle(final LegendItemSource source) {
        this(source, new ColumnArrangement(), new ColumnArrangement());
    }

    /**
     * Creates a new legend title with the specified arrangement.
     *
     * @param source
     *            the source.
     * @param hLayout
     *            the horizontal item arrangement (<code>null</code> not permitted).
     * @param vLayout
     *            the vertical item arrangement (<code>null</code> not permitted).
     */
    public CustomLegendTitle(final LegendItemSource source, final Arrangement hLayout, final Arrangement vLayout) {
        this.sources = new LegendItemSource[] { source };
        this.items = new BlockContainer(hLayout);
        this.hLayout = hLayout;
        this.vLayout = vLayout;
        this.backgroundPaint = null;
        this.legendItemGraphicEdge = RectangleEdge.LEFT;
        this.legendItemGraphicAnchor = RectangleAnchor.CENTER;
        this.legendItemGraphicLocation = RectangleAnchor.CENTER;
        this.legendItemGraphicPadding = new RectangleInsets(2.0, 2.0, 2.0, 2.0);
        this.itemFont = DEFAULT_ITEM_FONT;
        this.itemPaint = DEFAULT_ITEM_PAINT;
        this.itemLabelPadding = new RectangleInsets(2.0, 2.0, 2.0, 2.0);
        this.sortOrder = SortOrder.ASCENDING;
    }

    /**
     * Returns the legend item sources.
     *
     * @return The sources.
     */
    public LegendItemSource[] getSources() {
        return this.sources;
    }

    /**
     * Sets the legend item sources and sends a {@link TitleChangeEvent} to all registered listeners.
     *
     * @param sources
     *            the sources (<code>null</code> not permitted).
     */
    public void setSources(final LegendItemSource[] sources) {
        Args.nullNotPermitted(sources, "sources");
        this.sources = sources;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the background paint.
     *
     * @return The background paint (possibly <code>null</code>).
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    /**
     * Sets the background paint for the legend and sends a {@link TitleChangeEvent} to all registered listeners.
     *
     * @param paint
     *            the paint (<code>null</code> permitted).
     */
    public void setBackgroundPaint(final Paint paint) {
        this.backgroundPaint = paint;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the location of the shape within each legend item.
     *
     * @return The location (never <code>null</code>).
     */
    public RectangleEdge getLegendItemGraphicEdge() {
        return this.legendItemGraphicEdge;
    }

    /**
     * Sets the location of the shape within each legend item.
     *
     * @param edge
     *            the edge (<code>null</code> not permitted).
     */
    public void setLegendItemGraphicEdge(final RectangleEdge edge) {
        Args.nullNotPermitted(edge, "edge");
        this.legendItemGraphicEdge = edge;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the legend item graphic anchor.
     *
     * @return The graphic anchor (never <code>null</code>).
     */
    public RectangleAnchor getLegendItemGraphicAnchor() {
        return this.legendItemGraphicAnchor;
    }

    /**
     * Sets the anchor point used for the graphic in each legend item.
     *
     * @param anchor
     *            the anchor point (<code>null</code> not permitted).
     */
    public void setLegendItemGraphicAnchor(final RectangleAnchor anchor) {
        Args.nullNotPermitted(anchor, "anchor");
        this.legendItemGraphicAnchor = anchor;
    }

    /**
     * Returns the legend item graphic location.
     *
     * @return The location (never <code>null</code>).
     */
    public RectangleAnchor getLegendItemGraphicLocation() {
        return this.legendItemGraphicLocation;
    }

    /**
     * Sets the legend item graphic location.
     *
     * @param anchor
     *            the anchor (<code>null</code> not permitted).
     */
    public void setLegendItemGraphicLocation(final RectangleAnchor anchor) {
        this.legendItemGraphicLocation = anchor;
    }

    /**
     * Returns the padding that will be applied to each item graphic.
     *
     * @return The padding (never <code>null</code>).
     */
    public RectangleInsets getLegendItemGraphicPadding() {
        return this.legendItemGraphicPadding;
    }

    /**
     * Sets the padding that will be applied to each item graphic in the legend and sends a {@link TitleChangeEvent} to
     * all registered listeners.
     *
     * @param padding
     *            the padding (<code>null</code> not permitted).
     */
    public void setLegendItemGraphicPadding(final RectangleInsets padding) {
        Args.nullNotPermitted(padding, "padding");
        this.legendItemGraphicPadding = padding;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the item font.
     *
     * @return The font (never <code>null</code>).
     */
    public Font getItemFont() {
        return this.itemFont;
    }

    /**
     * Sets the item font and sends a {@link TitleChangeEvent} to all registered listeners.
     *
     * @param font
     *            the font (<code>null</code> not permitted).
     */
    public void setItemFont(final Font font) {
        Args.nullNotPermitted(font, "font");
        this.itemFont = font;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the item paint.
     *
     * @return The paint (never <code>null</code>).
     */
    public Paint getItemPaint() {
        return this.itemPaint;
    }

    /**
     * Sets the item paint.
     *
     * @param paint
     *            the paint (<code>null</code> not permitted).
     */
    public void setItemPaint(final Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.itemPaint = paint;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the padding used for the items labels.
     *
     * @return The padding (never <code>null</code>).
     */
    public RectangleInsets getItemLabelPadding() {
        return this.itemLabelPadding;
    }

    /**
     * Sets the padding used for the item labels in the legend.
     *
     * @param padding
     *            the padding (<code>null</code> not permitted).
     */
    public void setItemLabelPadding(final RectangleInsets padding) {
        Args.nullNotPermitted(padding, "padding");
        this.itemLabelPadding = padding;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Gets the order used to display legend items.
     * 
     * @return The order (never <code>null</code>).
     * @since 1.0.15
     */
    public SortOrder getSortOrder() {
        return this.sortOrder;
    }

    /**
     * Sets the order used to display legend items.
     * 
     * @param order
     *            Specifies ascending or descending order (<code>null</code> not permitted).
     * @since 1.0.15
     */
    public void setSortOrder(final SortOrder order) {
        Args.nullNotPermitted(order, "order");
        this.sortOrder = order;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Fetches the latest legend items.
     */
    protected void fetchLegendItems() {
        this.items.clear();
        final RectangleEdge p = getPosition();
        if (RectangleEdge.isTopOrBottom(p)) {
            this.items.setArrangement(this.hLayout);
        } else {
            this.items.setArrangement(this.vLayout);
        }

        if (this.sortOrder.equals(SortOrder.ASCENDING)) {
            for (int s = 0; s < this.sources.length; s++) {
                final LegendItemCollection legendItems = this.sources[s].getLegendItems();
                if (legendItems != null) {
                    for (int i = 0; i < legendItems.getItemCount(); i++) {
                        addItemBlock(legendItems.get(i));
                    }
                }
            }
        } else {
            for (int s = this.sources.length - 1; s >= 0; s--) {
                final LegendItemCollection legendItems = this.sources[s].getLegendItems();
                if (legendItems != null) {
                    for (int i = legendItems.getItemCount() - 1; i >= 0; i--) {
                        addItemBlock(legendItems.get(i));
                    }
                }
            }
        }
    }

    private void addItemBlock(final LegendItem item) {
        final Block block = createLegendItemBlock(item);
        this.items.add(block);
    }

    /**
     * Creates a legend item block.
     *
     * @param item
     *            the legend item.
     *
     * @return The block.
     */
    protected Block createLegendItemBlock(final LegendItem item) {
        final BlockContainer result;
        final LegendGraphic lg = new LegendGraphic(item.getShape(), newFillPaint(item));
        lg.setFillPaintTransformer(item.getFillPaintTransformer());
        lg.setShapeFilled(item.isShapeFilled());
        lg.setLine(item.getLine());
        lg.setLineStroke(item.getLineStroke());
        lg.setLinePaint(item.getLinePaint());
        lg.setLineVisible(item.isLineVisible());
        lg.setShapeVisible(item.isShapeVisible());
        lg.setShapeOutlineVisible(item.isShapeOutlineVisible());
        lg.setOutlinePaint(item.getOutlinePaint());
        lg.setOutlineStroke(item.getOutlineStroke());
        lg.setPadding(this.legendItemGraphicPadding);

        final LegendItemBlockContainer legendItem = new LegendItemBlockContainer(new BorderArrangement(),
                item.getDataset(), item.getSeriesKey());
        lg.setShapeAnchor(getLegendItemGraphicAnchor());
        lg.setShapeLocation(getLegendItemGraphicLocation());
        legendItem.add(lg, this.legendItemGraphicEdge);
        Font textFont = item.getLabelFont();
        if (textFont == null) {
            textFont = this.itemFont;
        }
        Paint textPaint = item.getLabelPaint();
        if (textPaint == null) {
            textPaint = this.itemPaint;
        }
        final LabelBlock labelBlock = new LabelBlock(newLabel(item), newTextFont(item, textFont), textPaint);
        labelBlock.setPadding(this.itemLabelPadding);
        legendItem.add(labelBlock);
        legendItem.setToolTipText(item.getToolTipText());
        legendItem.setURLText(item.getURLText());

        result = new BlockContainer(new CenterArrangement());
        result.add(legendItem);

        return result;
    }

    protected Paint newFillPaint(final LegendItem item) {
        return item.getFillPaint();
    }

    protected Font newTextFont(final LegendItem item, final Font textFont) {
        return textFont;
    }

    protected String newLabel(final LegendItem item) {
        return item.getLabel();
    }

    /**
     * Returns the container that holds the legend items.
     *
     * @return The container for the legend items.
     */
    public BlockContainer getItemContainer() {
        return this.items;
    }

    /**
     * Arranges the contents of the block, within the given constraints, and returns the block size.
     *
     * @param g2
     *            the graphics device.
     * @param constraint
     *            the constraint (<code>null</code> not permitted).
     *
     * @return The block size (in Java2D units, never <code>null</code>).
     */
    @Override
    public Size2D arrange(final Graphics2D g2, final RectangleConstraint constraint) {
        final Size2D result = new Size2D();
        fetchLegendItems();
        if (this.items.isEmpty()) {
            return result;
        }
        BlockContainer container = this.wrapper;
        if (container == null) {
            container = this.items;
        }
        final RectangleConstraint c = toContentConstraint(constraint);
        final Size2D size = container.arrange(g2, c);
        result.height = calculateTotalHeight(size.height);
        result.width = calculateTotalWidth(size.width);
        return result;
    }

    /**
     * Draws the title on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2
     *            the graphics device.
     * @param area
     *            the available area for the title.
     */
    @Override
    public void draw(final Graphics2D g2, final Rectangle2D area) {
        draw(g2, area, null);
    }

    /**
     * Draws the block within the specified area.
     *
     * @param g2
     *            the graphics device.
     * @param area
     *            the area.
     * @param params
     *            ignored (<code>null</code> permitted).
     *
     * @return An {@link org.jfree.chart.block.EntityBlockResult} or <code>null</code>.
     */
    @Override
    public Object draw(final Graphics2D g2, final Rectangle2D area, final Object params) {
        Rectangle2D target = (Rectangle2D) area.clone();
        final Rectangle2D hotspot = (Rectangle2D) area.clone();
        StandardEntityCollection sec = null;
        if (params instanceof EntityBlockParams && ((EntityBlockParams) params).getGenerateEntities()) {
            sec = new StandardEntityCollection();
            sec.add(new TitleEntity(hotspot, this));
        }
        target = trimMargin(target);
        if (this.backgroundPaint != null) {
            g2.setPaint(this.backgroundPaint);
            g2.fill(target);
        }
        final BlockFrame border = getFrame();
        border.draw(g2, target);
        border.getInsets().trim(target);
        BlockContainer container = this.wrapper;
        if (container == null) {
            container = this.items;
        }
        target = trimPadding(target);
        final Object val = container.draw(g2, target, params);
        if (val instanceof BlockResult) {
            final EntityCollection ec = ((BlockResult) val).getEntityCollection();
            if (ec != null && sec != null) {
                sec.addAll(ec);
                ((BlockResult) val).setEntityCollection(sec);
            }
        }
        return val;
    }

    /**
     * Returns the wrapper container, if any.
     *
     * @return The wrapper container (possibly <code>null</code>).
     *
     * @since 1.0.11
     */
    public BlockContainer getWrapper() {
        return this.wrapper;
    }

    /**
     * Sets the wrapper container for the legend.
     *
     * @param wrapper
     *            the wrapper container.
     */
    public void setWrapper(final BlockContainer wrapper) {
        this.wrapper = wrapper;
    }

    /**
     * Tests this title for equality with an arbitrary object.
     *
     * @param obj
     *            the object (<code>null</code> permitted).
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
        if (!(obj instanceof LegendTitle)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final CustomLegendTitle that = (CustomLegendTitle) obj;
        if (!PaintUtils.equal(this.backgroundPaint, that.backgroundPaint)) {
            return false;
        }
        if (this.legendItemGraphicEdge != that.legendItemGraphicEdge) {
            return false;
        }
        if (this.legendItemGraphicAnchor != that.legendItemGraphicAnchor) {
            return false;
        }
        if (this.legendItemGraphicLocation != that.legendItemGraphicLocation) {
            return false;
        }
        if (!this.itemFont.equals(that.itemFont)) {
            return false;
        }
        if (!this.itemPaint.equals(that.itemPaint)) {
            return false;
        }
        if (!this.hLayout.equals(that.hLayout)) {
            return false;
        }
        if (!this.vLayout.equals(that.vLayout)) {
            return false;
        }
        if (!this.sortOrder.equals(that.sortOrder)) {
            return false;
        }
        return true;
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
        SerialUtils.writePaint(this.backgroundPaint, stream);
        SerialUtils.writePaint(this.itemPaint, stream);
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
        this.backgroundPaint = SerialUtils.readPaint(stream);
        this.itemPaint = SerialUtils.readPaint(stream);
    }

}
