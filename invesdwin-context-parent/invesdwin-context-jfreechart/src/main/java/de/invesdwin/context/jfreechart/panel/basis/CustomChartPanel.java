package de.invesdwin.context.jfreechart.panel.basis;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ResourceBundle;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.Pannable;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.Zoomable;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.data.Range;

/**
 * A Swing GUI component for displaying a {@link JFreeChart} object.
 * <P>
 * The panel registers with the chart to receive notification of changes to any component of the chart. The chart is
 * redrawn automatically whenever this notification is received.
 */
//CHECKSTYLE:OFF
@NotThreadSafe
public class CustomChartPanel extends JPanel
        implements ChartChangeListener, ChartProgressListener, MouseListener, MouseMotionListener {

    public static final Cursor DEFAULT_CURSOR = Cursor.getDefaultCursor();
    public static final Cursor MOVE_CURSOR = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);

    /**
     * Default setting for buffer usage. The default has been changed to <code>true</code> from version 1.0.13 onwards,
     * because of a severe performance problem with drawing the zoom rectangle using XOR (which now happens only when
     * the buffer is NOT used).
     */
    public static final boolean DEFAULT_BUFFER_USED = true;

    /** The default panel width. */
    public static final int DEFAULT_WIDTH = 1920;

    /** The default panel height. */
    public static final int DEFAULT_HEIGHT = 1028;

    /** The default limit below which chart scaling kicks in. */
    public static final int DEFAULT_MINIMUM_DRAW_WIDTH = 1;

    /** The default limit below which chart scaling kicks in. */
    public static final int DEFAULT_MINIMUM_DRAW_HEIGHT = 1;

    /** The default limit above which chart scaling kicks in. */
    public static final int DEFAULT_MAXIMUM_DRAW_WIDTH = 1920;

    /** The default limit above which chart scaling kicks in. */
    public static final int DEFAULT_MAXIMUM_DRAW_HEIGHT = 1080;

    /** The minimum size required to perform a zoom on a rectangle */
    public static final int DEFAULT_ZOOM_TRIGGER_DISTANCE = 10;

    /** The chart that is displayed in the panel. */
    private JFreeChart chart;

    /** A flag that controls whether or not the off-screen buffer is used. */
    private final boolean useBuffer;

    /** A flag that indicates that the buffer should be refreshed. */
    private boolean refreshBuffer;

    /** A buffer for the rendered chart. */
    private transient Image chartBuffer;

    /** The height of the chart buffer. */
    private int chartBufferHeight;

    /** The width of the chart buffer. */
    private int chartBufferWidth;

    /**
     * The minimum width for drawing a chart (uses scaling for smaller widths).
     */
    private int minimumDrawWidth;

    /**
     * The minimum height for drawing a chart (uses scaling for smaller heights).
     */
    private int minimumDrawHeight;

    /**
     * The maximum width for drawing a chart (uses scaling for bigger widths).
     */
    private int maximumDrawWidth;

    /**
     * The maximum height for drawing a chart (uses scaling for bigger heights).
     */
    private int maximumDrawHeight;

    /** The drawing info collected the last time the chart was drawn. */
    private final ChartRenderingInfo info;

    /** The chart anchor point. */
    private Point2D anchor;

    /** The scale factor used to draw the chart. */
    private double scaleX;

    /** The scale factor used to draw the chart. */
    private double scaleY;

    /** A flag that controls whether or not horizontal tracing is enabled. */
    private boolean horizontalAxisTrace = false;

    /** A flag that controls whether or not vertical tracing is enabled. */
    private boolean verticalAxisTrace = false;

    /** A vertical trace line. */
    private transient Line2D verticalTraceLine;

    /** A horizontal trace line. */
    private transient Line2D horizontalTraceLine;

    /** A flag that indicates if original tooltip delays are changed. */
    private boolean ownToolTipDelaysActive;

    /** Original initial tooltip delay of ToolTipManager.sharedInstance(). */
    private int originalToolTipInitialDelay;

    /** Original reshow tooltip delay of ToolTipManager.sharedInstance(). */
    private int originalToolTipReshowDelay;

    /** Original dismiss tooltip delay of ToolTipManager.sharedInstance(). */
    private int originalToolTipDismissDelay;

    /** Own initial tooltip delay to be used in this chart panel. */
    private int ownToolTipInitialDelay;

    /** Own reshow tooltip delay to be used in this chart panel. */
    private int ownToolTipReshowDelay;

    /** Own dismiss tooltip delay to be used in this chart panel. */
    private int ownToolTipDismissDelay;

    /** The resourceBundle for the localization. */
    protected static ResourceBundle localizationResources = ResourceBundleWrapper
            .getBundle("org.jfree.chart.LocalizationBundle");

    /**
     * Temporary storage for the width and height of the chart drawing area during panning.
     */
    private double panW, panH;

    /** The last mouse position during panning. */
    private Point panLast;

    /**
     * Constructs a panel that displays the specified chart.
     *
     * @param chart
     *            the chart.
     */
    public CustomChartPanel(final JFreeChart chart) {
        this(chart, DEFAULT_BUFFER_USED);
    }

    /**
     * Constructs a panel containing a chart. The <code>useBuffer</code> flag controls whether or not an offscreen
     * <code>BufferedImage</code> is maintained for the chart. If the buffer is used, more memory is consumed, but panel
     * repaints will be a lot quicker in cases where the chart itself hasn't changed (for example, when another frame is
     * moved to reveal the panel). WARNING: If you set the <code>useBuffer</code> flag to false, note that the mouse
     * zooming rectangle will (in that case) be drawn using XOR, and there is a SEVERE performance problem with that on
     * JRE6 on Windows.
     *
     * @param chart
     *            the chart.
     * @param useBuffer
     *            a flag controlling whether or not an off-screen buffer is used (read the warning above before setting
     *            this to <code>false</code>).
     */
    public CustomChartPanel(final JFreeChart chart, final boolean useBuffer) {
        this(chart, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_MINIMUM_DRAW_WIDTH, DEFAULT_MINIMUM_DRAW_HEIGHT,
                DEFAULT_MAXIMUM_DRAW_WIDTH, DEFAULT_MAXIMUM_DRAW_HEIGHT, useBuffer);

    }

    /**
     * Constructs a JFreeChart panel.
     *
     * @param chart
     *            the chart.
     * @param width
     *            the preferred width of the panel.
     * @param height
     *            the preferred height of the panel.
     * @param minimumDrawWidth
     *            the minimum drawing width.
     * @param minimumDrawHeight
     *            the minimum drawing height.
     * @param maximumDrawWidth
     *            the maximum drawing width.
     * @param maximumDrawHeight
     *            the maximum drawing height.
     * @param useBuffer
     *            a flag that indicates whether to use the off-screen buffer to improve performance (at the expense of
     *            memory).
     * @param properties
     *            a flag indicating whether or not the chart property editor should be available via the popup menu.
     * @param copy
     *            a flag indicating whether or not a copy option should be available via the popup menu.
     * @param save
     *            a flag indicating whether or not save options should be available via the popup menu.
     * @param print
     *            a flag indicating whether or not the print option should be available via the popup menu.
     * @param zoom
     *            a flag indicating whether or not zoom options should be added to the popup menu.
     * @param tooltips
     *            a flag indicating whether or not tooltips should be enabled for the chart.
     *
     * @since 1.0.13
     */
    public CustomChartPanel(final JFreeChart chart, final int width, final int height, final int minimumDrawWidth,
            final int minimumDrawHeight, final int maximumDrawWidth, final int maximumDrawHeight,
            final boolean useBuffer) {

        setChart(chart);
        this.info = new ChartRenderingInfo();
        setPreferredSize(new Dimension(width, height));
        this.useBuffer = useBuffer;
        this.refreshBuffer = false;
        this.minimumDrawWidth = minimumDrawWidth;
        this.minimumDrawHeight = minimumDrawHeight;
        this.maximumDrawWidth = maximumDrawWidth;
        this.maximumDrawHeight = maximumDrawHeight;

        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setDisplayToolTips(false);
        addMouseListener(this);
        addMouseMotionListener(this);

        // initialize ChartPanel-specific tool tip delays with
        // values the from ToolTipManager.sharedInstance()
        final ToolTipManager ttm = ToolTipManager.sharedInstance();
        this.ownToolTipInitialDelay = ttm.getInitialDelay();
        this.ownToolTipDismissDelay = ttm.getDismissDelay();
        this.ownToolTipReshowDelay = ttm.getReshowDelay();
    }

    /**
     * Returns the chart contained in the panel.
     *
     * @return The chart (possibly <code>null</code>).
     */
    public JFreeChart getChart() {
        return this.chart;
    }

    /**
     * Sets the chart that is displayed in the panel.
     *
     * @param chart
     *            the chart (<code>null</code> permitted).
     */
    public void setChart(final JFreeChart chart) {
        // stop listening for changes to the existing chart
        if (this.chart != null) {
            this.chart.removeChangeListener(this);
            this.chart.removeProgressListener(this);
        }

        // add the new chart
        this.chart = chart;
        if (chart != null) {
            this.chart.addChangeListener(this);
            this.chart.addProgressListener(this);
        }
        if (this.useBuffer) {
            this.refreshBuffer = true;
        }
        repaint();

    }

    /**
     * Returns the minimum drawing width for charts.
     * <P>
     * If the width available on the panel is less than this, then the chart is drawn at the minimum width then scaled
     * down to fit.
     *
     * @return The minimum drawing width.
     */
    public int getMinimumDrawWidth() {
        return this.minimumDrawWidth;
    }

    /**
     * Sets the minimum drawing width for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available width is less than this amount, the chart will be
     * drawn using the minimum width then scaled down to fit the available space.
     *
     * @param width
     *            The width.
     */
    public void setMinimumDrawWidth(final int width) {
        this.minimumDrawWidth = width;
    }

    /**
     * Returns the maximum drawing width for charts.
     * <P>
     * If the width available on the panel is greater than this, then the chart is drawn at the maximum width then
     * scaled up to fit.
     *
     * @return The maximum drawing width.
     */
    public int getMaximumDrawWidth() {
        return this.maximumDrawWidth;
    }

    /**
     * Sets the maximum drawing width for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available width is greater than this amount, the chart will
     * be drawn using the maximum width then scaled up to fit the available space.
     *
     * @param width
     *            The width.
     */
    public void setMaximumDrawWidth(final int width) {
        this.maximumDrawWidth = width;
    }

    /**
     * Returns the minimum drawing height for charts.
     * <P>
     * If the height available on the panel is less than this, then the chart is drawn at the minimum height then scaled
     * down to fit.
     *
     * @return The minimum drawing height.
     */
    public int getMinimumDrawHeight() {
        return this.minimumDrawHeight;
    }

    /**
     * Sets the minimum drawing height for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available height is less than this amount, the chart will be
     * drawn using the minimum height then scaled down to fit the available space.
     *
     * @param height
     *            The height.
     */
    public void setMinimumDrawHeight(final int height) {
        this.minimumDrawHeight = height;
    }

    /**
     * Returns the maximum drawing height for charts.
     * <P>
     * If the height available on the panel is greater than this, then the chart is drawn at the maximum height then
     * scaled up to fit.
     *
     * @return The maximum drawing height.
     */
    public int getMaximumDrawHeight() {
        return this.maximumDrawHeight;
    }

    /**
     * Sets the maximum drawing height for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available height is greater than this amount, the chart will
     * be drawn using the maximum height then scaled up to fit the available space.
     *
     * @param height
     *            The height.
     */
    public void setMaximumDrawHeight(final int height) {
        this.maximumDrawHeight = height;
    }

    /**
     * Returns the X scale factor for the chart. This will be 1.0 if no scaling has been used.
     *
     * @return The scale factor.
     */
    public double getScaleX() {
        return this.scaleX;
    }

    /**
     * Returns the Y scale factory for the chart. This will be 1.0 if no scaling has been used.
     *
     * @return The scale factor.
     */
    public double getScaleY() {
        return this.scaleY;
    }

    /**
     * Returns the anchor point.
     *
     * @return The anchor point (possibly <code>null</code>).
     */
    public Point2D getAnchor() {
        return this.anchor;
    }

    /**
     * Sets the anchor point. This method is provided for the use of subclasses, not end users.
     *
     * @param anchor
     *            the anchor point (<code>null</code> permitted).
     */
    protected void setAnchor(final Point2D anchor) {
        this.anchor = anchor;
    }

    /**
     * Returns the chart rendering info from the most recent chart redraw.
     *
     * @return The chart rendering info.
     */
    public ChartRenderingInfo getChartRenderingInfo() {
        return this.info;
    }

    /**
     * Returns the flag that controls whether or not a horizontal axis trace line is drawn over the plot area at the
     * current mouse location.
     *
     * @return A boolean.
     */
    public boolean getHorizontalAxisTrace() {
        return this.horizontalAxisTrace;
    }

    /**
     * A flag that controls trace lines on the horizontal axis.
     *
     * @param flag
     *            <code>true</code> enables trace lines for the mouse pointer on the horizontal axis.
     */
    public void setHorizontalAxisTrace(final boolean flag) {
        this.horizontalAxisTrace = flag;
    }

    /**
     * Returns the horizontal trace line.
     *
     * @return The horizontal trace line (possibly <code>null</code>).
     */
    protected Line2D getHorizontalTraceLine() {
        return this.horizontalTraceLine;
    }

    /**
     * Sets the horizontal trace line.
     *
     * @param line
     *            the line (<code>null</code> permitted).
     */
    protected void setHorizontalTraceLine(final Line2D line) {
        this.horizontalTraceLine = line;
    }

    /**
     * Returns the flag that controls whether or not a vertical axis trace line is drawn over the plot area at the
     * current mouse location.
     *
     * @return A boolean.
     */
    public boolean getVerticalAxisTrace() {
        return this.verticalAxisTrace;
    }

    /**
     * A flag that controls trace lines on the vertical axis.
     *
     * @param flag
     *            <code>true</code> enables trace lines for the mouse pointer on the vertical axis.
     */
    public void setVerticalAxisTrace(final boolean flag) {
        this.verticalAxisTrace = flag;
    }

    /**
     * Returns the vertical trace line.
     *
     * @return The vertical trace line (possibly <code>null</code>).
     */
    protected Line2D getVerticalTraceLine() {
        return this.verticalTraceLine;
    }

    /**
     * Sets the vertical trace line.
     *
     * @param line
     *            the line (<code>null</code> permitted).
     */
    protected void setVerticalTraceLine(final Line2D line) {
        this.verticalTraceLine = line;
    }

    private int allowedRangeGap = 5;

    public int getAllowedRangeGap() {
        return allowedRangeGap;
    }

    public void setAllowedRangeGap(final int allowedRangeGap) {
        this.allowedRangeGap = allowedRangeGap;
    }

    /**
     * Switches the display of tooltips for the panel on or off. Note that tooltips can only be displayed if the chart
     * has been configured to generate tooltip items.
     *
     * @param flag
     *            <code>true</code> to enable tooltips, <code>false</code> to disable tooltips.
     */
    public void setDisplayToolTips(final boolean flag) {
        if (flag) {
            ToolTipManager.sharedInstance().registerComponent(this);
        } else {
            ToolTipManager.sharedInstance().unregisterComponent(this);
        }
    }

    /**
     * Returns a string for the tooltip.
     *
     * @param e
     *            the mouse event.
     *
     * @return A tool tip or <code>null</code> if no tooltip is available.
     */
    @Override
    public String getToolTipText(final MouseEvent e) {
        String result = null;
        if (this.info != null) {
            final EntityCollection entities = this.info.getEntityCollection();
            if (entities != null) {
                final Insets insets = getInsets();
                final ChartEntity entity = entities.getEntity((int) ((e.getX() - insets.left) / this.scaleX),
                        (int) ((e.getY() - insets.top) / this.scaleY));
                if (entity != null) {
                    result = entity.getToolTipText();
                }
            }
        }
        return result;
    }

    /**
     * Translates a Java2D point on the chart to a screen location.
     *
     * @param java2DPoint
     *            the Java2D point.
     *
     * @return The screen location.
     */
    public Point translateJava2DToScreen(final Point2D java2DPoint) {
        final Insets insets = getInsets();
        final int x = (int) (java2DPoint.getX() * this.scaleX + insets.left);
        final int y = (int) (java2DPoint.getY() * this.scaleY + insets.top);
        return new Point(x, y);
    }

    /**
     * Translates a panel (component) location to a Java2D point.
     *
     * @param screenPoint
     *            the screen location (<code>null</code> not permitted).
     *
     * @return The Java2D coordinates.
     */
    public Point2D translateScreenToJava2D(final Point screenPoint) {
        final Insets insets = getInsets();
        final double x = (screenPoint.getX() - insets.left) / this.scaleX;
        final double y = (screenPoint.getY() - insets.top) / this.scaleY;
        return new Point2D.Double(x, y);
    }

    /**
     * Applies any scaling that is in effect for the chart drawing to the given rectangle.
     *
     * @param rect
     *            the rectangle (<code>null</code> not permitted).
     *
     * @return A new scaled rectangle.
     */
    public Rectangle2D scale(final Rectangle2D rect) {
        final Insets insets = getInsets();
        final double x = rect.getX() * getScaleX() + insets.left;
        final double y = rect.getY() * getScaleY() + insets.top;
        final double w = rect.getWidth() * getScaleX();
        final double h = rect.getHeight() * getScaleY();
        return new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Returns the chart entity at a given point.
     * <P>
     * This method will return null if there is (a) no entity at the given point, or (b) no entity collection has been
     * generated.
     *
     * @param viewX
     *            the x-coordinate.
     * @param viewY
     *            the y-coordinate.
     *
     * @return The chart entity (possibly <code>null</code>).
     */
    public ChartEntity getEntityForPoint(final int viewX, final int viewY) {

        ChartEntity result = null;
        if (this.info != null) {
            final Insets insets = getInsets();
            final double x = (viewX - insets.left) / this.scaleX;
            final double y = (viewY - insets.top) / this.scaleY;
            final EntityCollection entities = this.info.getEntityCollection();
            result = entities != null ? entities.getEntity(x, y) : null;
        }
        return result;

    }

    /**
     * Returns the flag that controls whether or not the offscreen buffer needs to be refreshed.
     *
     * @return A boolean.
     */
    public boolean getRefreshBuffer() {
        return this.refreshBuffer;
    }

    /**
     * Sets the refresh buffer flag. This flag is used to avoid unnecessary redrawing of the chart when the offscreen
     * image buffer is used.
     *
     * @param flag
     *            <code>true</code> indicates that the buffer should be refreshed.
     */
    public void setRefreshBuffer(final boolean flag) {
        this.refreshBuffer = flag;
    }

    /**
     * Paints the component by drawing the chart to fill the entire component, but allowing for the insets (which will
     * be non-zero if a border has been set for this component). To increase performance (at the expense of memory), an
     * off-screen buffer image can be used.
     *
     * @param g
     *            the graphics device for drawing on.
     */
    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        if (this.chart == null) {
            return;
        }
        final Graphics2D g2 = (Graphics2D) g.create();

        // first determine the size of the chart rendering area...
        final Dimension size = getSize();
        final Insets insets = getInsets();
        final Rectangle2D available = new Rectangle2D.Double(insets.left, insets.top,
                size.getWidth() - insets.left - insets.right, size.getHeight() - insets.top - insets.bottom);

        // work out if scaling is required...
        boolean scale = false;
        double drawWidth = available.getWidth();
        double drawHeight = available.getHeight();
        this.scaleX = 1.0;
        this.scaleY = 1.0;

        if (drawWidth < this.minimumDrawWidth) {
            this.scaleX = drawWidth / this.minimumDrawWidth;
            drawWidth = this.minimumDrawWidth;
            scale = true;
        } else if (drawWidth > this.maximumDrawWidth) {
            this.scaleX = drawWidth / this.maximumDrawWidth;
            drawWidth = this.maximumDrawWidth;
            scale = true;
        }

        if (drawHeight < this.minimumDrawHeight) {
            this.scaleY = drawHeight / this.minimumDrawHeight;
            drawHeight = this.minimumDrawHeight;
            scale = true;
        } else if (drawHeight > this.maximumDrawHeight) {
            this.scaleY = drawHeight / this.maximumDrawHeight;
            drawHeight = this.maximumDrawHeight;
            scale = true;
        }

        final Rectangle2D chartArea = new Rectangle2D.Double(0.0, 0.0, drawWidth, drawHeight);

        // are we using the chart buffer?
        if (this.useBuffer) {

            // do we need to resize the buffer?
            if ((this.chartBuffer == null) || (this.chartBufferWidth != available.getWidth())
                    || (this.chartBufferHeight != available.getHeight())) {
                this.chartBufferWidth = (int) available.getWidth();
                this.chartBufferHeight = (int) available.getHeight();
                final GraphicsConfiguration gc = g2.getDeviceConfiguration();
                this.chartBuffer = gc.createCompatibleImage(this.chartBufferWidth, this.chartBufferHeight,
                        Transparency.TRANSLUCENT);
                this.refreshBuffer = true;
            }

            // do we need to redraw the buffer?
            if (this.refreshBuffer) {

                this.refreshBuffer = false; // clear the flag

                final Rectangle2D bufferArea = new Rectangle2D.Double(0, 0, this.chartBufferWidth,
                        this.chartBufferHeight);

                // make the background of the buffer clear and transparent
                final Graphics2D bufferG2 = (Graphics2D) this.chartBuffer.getGraphics();
                final Composite savedComposite = bufferG2.getComposite();
                bufferG2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
                final Rectangle r = new Rectangle(0, 0, this.chartBufferWidth, this.chartBufferHeight);
                bufferG2.fill(r);
                bufferG2.setComposite(savedComposite);

                if (scale) {
                    final AffineTransform saved = bufferG2.getTransform();
                    final AffineTransform st = AffineTransform.getScaleInstance(this.scaleX, this.scaleY);
                    bufferG2.transform(st);
                    this.chart.draw(bufferG2, chartArea, this.anchor, this.info);
                    bufferG2.setTransform(saved);
                } else {
                    this.chart.draw(bufferG2, bufferArea, this.anchor, this.info);
                }

            }

            // zap the buffer onto the panel...
            g2.drawImage(this.chartBuffer, insets.left, insets.top, this);

        } else { // redrawing the chart every time...
            final AffineTransform saved = g2.getTransform();
            g2.translate(insets.left, insets.top);
            if (scale) {
                final AffineTransform st = AffineTransform.getScaleInstance(this.scaleX, this.scaleY);
                g2.transform(st);
            }
            this.chart.draw(g2, chartArea, this.anchor, this.info);
            g2.setTransform(saved);

        }

        g2.dispose();

        this.anchor = null;
        this.verticalTraceLine = null;
        this.horizontalTraceLine = null;
    }

    /**
     * Receives notification of changes to the chart, and redraws the chart.
     *
     * @param event
     *            details of the chart change event.
     */
    @Override
    public void chartChanged(final ChartChangeEvent event) {
        this.refreshBuffer = true;
        repaint();
    }

    /**
     * Receives notification of a chart progress event.
     *
     * @param event
     *            the event.
     */
    @Override
    public void chartProgress(final ChartProgressEvent event) {
        // does nothing - override if necessary
    }

    /**
     * Handles a 'mouse entered' event. This method changes the tooltip delays of ToolTipManager.sharedInstance() to the
     * possibly different values set for this chart panel.
     *
     * @param e
     *            the mouse event.
     */
    @Override
    public void mouseEntered(final MouseEvent e) {
        if (!this.ownToolTipDelaysActive) {
            final ToolTipManager ttm = ToolTipManager.sharedInstance();

            this.originalToolTipInitialDelay = ttm.getInitialDelay();
            ttm.setInitialDelay(this.ownToolTipInitialDelay);

            this.originalToolTipReshowDelay = ttm.getReshowDelay();
            ttm.setReshowDelay(this.ownToolTipReshowDelay);

            this.originalToolTipDismissDelay = ttm.getDismissDelay();
            ttm.setDismissDelay(this.ownToolTipDismissDelay);

            this.ownToolTipDelaysActive = true;
        }
    }

    /**
     * Handles a 'mouse exited' event. This method resets the tooltip delays of ToolTipManager.sharedInstance() to their
     * original values in effect before mouseEntered()
     *
     * @param e
     *            the mouse event.
     */
    @Override
    public void mouseExited(final MouseEvent e) {
        if (this.ownToolTipDelaysActive) {
            // restore original tooltip dealys
            final ToolTipManager ttm = ToolTipManager.sharedInstance();
            ttm.setInitialDelay(this.originalToolTipInitialDelay);
            ttm.setReshowDelay(this.originalToolTipReshowDelay);
            ttm.setDismissDelay(this.originalToolTipDismissDelay);
            this.ownToolTipDelaysActive = false;
        }
    }

    /**
     * Handles a 'mouse pressed' event.
     * <P>
     * This event is the popup trigger on Unix/Linux. For Windows, the popup trigger is the 'mouse released' event.
     *
     * @param e
     *            The mouse event.
     */
    @Override
    public void mousePressed(final MouseEvent e) {
        if (this.chart == null) {
            return;
        }
        final Plot plot = this.chart.getPlot();
        // can we pan this plot?
        if (plot instanceof Pannable && isPanAllowed() && e.getButton() == MouseEvent.BUTTON1) {
            final Pannable pannable = (Pannable) plot;
            if (pannable.isDomainPannable() || pannable.isRangePannable()) {
                final Rectangle2D screenDataArea = getScreenDataArea(e.getX(), e.getY());
                if (screenDataArea != null && screenDataArea.contains(e.getPoint())) {
                    this.panW = screenDataArea.getWidth();
                    this.panH = screenDataArea.getHeight();
                    this.panLast = e.getPoint();
                    setCursor(MOVE_CURSOR);
                }
            }
            // the actual panning occurs later in the mouseDragged()
            // method
        }
    }

    protected boolean isPanAllowed() {
        return true;
    }

    /**
     * Handles a 'mouse dragged' event.
     *
     * @param e
     *            the mouse event.
     */
    @Override
    public void mouseDragged(final MouseEvent e) {
        // handle panning if we have a start point
        if (this.panLast != null) {
            final double dx = e.getX() - this.panLast.getX();
            final double dy = e.getY() - this.panLast.getY();
            if (dx == 0.0 && dy == 0.0) {
                return;
            }
            final double wPercent = -dx / this.panW;
            final double hPercent = dy / this.panH;
            final XYPlot plot = chart.getXYPlot();
            final ValueAxis domainAxis = plot.getDomainAxis();
            final Range range = domainAxis.getRange();
            if (wPercent > 0 && range.getUpperBound() >= plot.getDataset().getItemCount(0) + allowedRangeGap) {
                return;
            }
            if (wPercent < 0 && range.getLowerBound() <= 0 - allowedRangeGap) {
                return;
            }
            final boolean old = this.chart.getPlot().isNotify();
            this.chart.getPlot().setNotify(false);
            final Pannable p = (Pannable) this.chart.getPlot();
            if (p.getOrientation() == PlotOrientation.VERTICAL) {
                p.panDomainAxes(wPercent, this.info.getPlotInfo(), this.panLast);
                p.panRangeAxes(hPercent, this.info.getPlotInfo(), this.panLast);
            } else {
                p.panDomainAxes(hPercent, this.info.getPlotInfo(), this.panLast);
                p.panRangeAxes(wPercent, this.info.getPlotInfo(), this.panLast);
            }
            this.panLast = e.getPoint();
            this.chart.getPlot().setNotify(old);
            return;
        }
    }

    public boolean isPanning() {
        return panLast != null;
    }

    /**
     * Handles a 'mouse released' event. On Windows, we need to check if this is a popup trigger, but only if we haven't
     * already been tracking a zoom rectangle.
     *
     * @param e
     *            information about the event.
     */
    @Override
    public void mouseReleased(final MouseEvent e) {

        // if we've been panning, we need to reset now that the mouse is
        // released...
        if (this.panLast != null) {
            this.panLast = null;
            setCursor(DEFAULT_CURSOR);
        }

    }

    /**
     * Receives notification of mouse clicks on the panel. These are translated and passed on to any registered
     * {@link ChartMouseListener}s.
     *
     * @param event
     *            Information about the mouse event.
     */
    @Override
    public void mouseClicked(final MouseEvent event) {

        final Insets insets = getInsets();
        final int x = (int) ((event.getX() - insets.left) / this.scaleX);
        final int y = (int) ((event.getY() - insets.top) / this.scaleY);

        this.anchor = new Point2D.Double(x, y);
        if (this.chart == null) {
            return;
        }
        this.chart.setNotify(true); // force a redraw
    }

    /**
     * Implementation of the MouseMotionListener's method.
     *
     * @param e
     *            the event.
     */
    @Override
    public void mouseMoved(final MouseEvent e) {
        final Graphics2D g2 = (Graphics2D) getGraphics();
        if (this.horizontalAxisTrace) {
            drawHorizontalAxisTrace(g2, e.getX());
        }
        if (this.verticalAxisTrace) {
            drawVerticalAxisTrace(g2, e.getY());
        }
        g2.dispose();
    }

    /**
     * Restores the auto-range calculation on both axes.
     */
    public void restoreAutoBounds() {
        final Plot plot = this.chart.getPlot();
        if (plot == null) {
            return;
        }
        // here we tweak the notify flag on the plot so that only
        // one notification happens even though we update multiple
        // axes...
        final boolean savedNotify = plot.isNotify();
        plot.setNotify(false);
        restoreAutoDomainBounds();
        restoreAutoRangeBounds();
        plot.setNotify(savedNotify);
    }

    /**
     * Restores the auto-range calculation on the domain axis.
     */
    public void restoreAutoDomainBounds() {
        final Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            final Zoomable z = (Zoomable) plot;
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            final boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            // we need to guard against this.zoomPoint being null
            final Point2D zp = new Point();
            z.zoomDomainAxes(0.0, this.info.getPlotInfo(), zp);
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Restores the auto-range calculation on the range axis.
     */
    public void restoreAutoRangeBounds() {
        final Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            final Zoomable z = (Zoomable) plot;
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            final boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            // we need to guard against this.zoomPoint being null
            final Point2D zp = new Point();
            z.zoomRangeAxes(0.0, this.info.getPlotInfo(), zp);
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Returns the data area for the chart (the area inside the axes) with the current scaling applied (that is, the
     * area as it appears on screen).
     *
     * @return The scaled data area.
     */
    public Rectangle2D getScreenDataArea() {
        final Rectangle2D dataArea = this.info.getPlotInfo().getDataArea();
        final Insets insets = getInsets();
        final double x = dataArea.getX() * this.scaleX + insets.left;
        final double y = dataArea.getY() * this.scaleY + insets.top;
        final double w = dataArea.getWidth() * this.scaleX;
        final double h = dataArea.getHeight() * this.scaleY;
        return new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Returns the data area (the area inside the axes) for the plot or subplot, with the current scaling applied.
     *
     * @param x
     *            the x-coordinate (for subplot selection).
     * @param y
     *            the y-coordinate (for subplot selection).
     *
     * @return The scaled data area.
     */
    public Rectangle2D getScreenDataArea(final int x, final int y) {
        final PlotRenderingInfo plotInfo = this.info.getPlotInfo();
        Rectangle2D result;
        if (plotInfo.getSubplotCount() == 0) {
            result = getScreenDataArea();
        } else {
            // get the origin of the zoom selection in the Java2D space used for
            // drawing the chart (that is, before any scaling to fit the panel)
            final Point2D selectOrigin = translateScreenToJava2D(new Point(x, y));
            final int subplotIndex = plotInfo.getSubplotIndex(selectOrigin);
            if (subplotIndex == -1) {
                return null;
            }
            result = scale(plotInfo.getSubplotInfo(subplotIndex).getDataArea());
        }
        return result;
    }

    /**
     * Returns the initial tooltip delay value used inside this chart panel.
     *
     * @return An integer representing the initial delay value, in milliseconds.
     *
     * @see javax.swing.ToolTipManager#getInitialDelay()
     */
    public int getInitialDelay() {
        return this.ownToolTipInitialDelay;
    }

    /**
     * Returns the reshow tooltip delay value used inside this chart panel.
     *
     * @return An integer representing the reshow delay value, in milliseconds.
     *
     * @see javax.swing.ToolTipManager#getReshowDelay()
     */
    public int getReshowDelay() {
        return this.ownToolTipReshowDelay;
    }

    /**
     * Returns the dismissal tooltip delay value used inside this chart panel.
     *
     * @return An integer representing the dismissal delay value, in milliseconds.
     *
     * @see javax.swing.ToolTipManager#getDismissDelay()
     */
    public int getDismissDelay() {
        return this.ownToolTipDismissDelay;
    }

    /**
     * Specifies the initial delay value for this chart panel.
     *
     * @param delay
     *            the number of milliseconds to delay (after the cursor has paused) before displaying.
     *
     * @see javax.swing.ToolTipManager#setInitialDelay(int)
     */
    public void setInitialDelay(final int delay) {
        this.ownToolTipInitialDelay = delay;
    }

    /**
     * Specifies the amount of time before the user has to wait initialDelay milliseconds before a tooltip will be
     * shown.
     *
     * @param delay
     *            time in milliseconds
     *
     * @see javax.swing.ToolTipManager#setReshowDelay(int)
     */
    public void setReshowDelay(final int delay) {
        this.ownToolTipReshowDelay = delay;
    }

    /**
     * Specifies the dismissal delay value for this chart panel.
     *
     * @param delay
     *            the number of milliseconds to delay before taking away the tooltip
     *
     * @see javax.swing.ToolTipManager#setDismissDelay(int)
     */
    public void setDismissDelay(final int delay) {
        this.ownToolTipDismissDelay = delay;
    }

    /**
     * Draws a vertical line used to trace the mouse position to the horizontal axis.
     *
     * @param g2
     *            the graphics device.
     * @param x
     *            the x-coordinate of the trace line.
     */
    private void drawHorizontalAxisTrace(final Graphics2D g2, final int x) {

        final Rectangle2D dataArea = getScreenDataArea();

        g2.setXORMode(Color.orange);
        if (((int) dataArea.getMinX() < x) && (x < (int) dataArea.getMaxX())) {

            if (this.verticalTraceLine != null) {
                g2.draw(this.verticalTraceLine);
                this.verticalTraceLine.setLine(x, (int) dataArea.getMinY(), x, (int) dataArea.getMaxY());
            } else {
                this.verticalTraceLine = new Line2D.Float(x, (int) dataArea.getMinY(), x, (int) dataArea.getMaxY());
            }
            g2.draw(this.verticalTraceLine);
        }

        // Reset to the default 'overwrite' mode
        g2.setPaintMode();
    }

    /**
     * Draws a horizontal line used to trace the mouse position to the vertical axis.
     *
     * @param g2
     *            the graphics device.
     * @param y
     *            the y-coordinate of the trace line.
     */
    private void drawVerticalAxisTrace(final Graphics2D g2, final int y) {

        final Rectangle2D dataArea = getScreenDataArea();

        g2.setXORMode(Color.orange);
        if (((int) dataArea.getMinY() < y) && (y < (int) dataArea.getMaxY())) {

            if (this.horizontalTraceLine != null) {
                g2.draw(this.horizontalTraceLine);
                this.horizontalTraceLine.setLine((int) dataArea.getMinX(), y, (int) dataArea.getMaxX(), y);
            } else {
                this.horizontalTraceLine = new Line2D.Float((int) dataArea.getMinX(), y, (int) dataArea.getMaxX(), y);
            }
            g2.draw(this.horizontalTraceLine);
        }

        // Reset to the default 'overwrite' mode
        g2.setPaintMode();
    }

}
