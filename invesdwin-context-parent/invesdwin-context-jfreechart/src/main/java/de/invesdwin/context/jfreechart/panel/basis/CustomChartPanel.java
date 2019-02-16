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
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.ResourceBundle;

import javax.annotation.concurrent.NotThreadSafe;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartTransferable;
import org.jfree.chart.ChartUtils;
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
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.chart.util.SerialUtils;
import org.jfree.data.Range;

/**
 * A Swing GUI component for displaying a {@link JFreeChart} object.
 * <P>
 * The panel registers with the chart to receive notification of changes to any component of the chart. The chart is
 * redrawn automatically whenever this notification is received.
 */
//CHECKSTYLE:OFF
@NotThreadSafe
public class CustomChartPanel extends JPanel implements ChartChangeListener, ChartProgressListener, ActionListener,
        MouseListener, MouseMotionListener, Printable, Serializable {

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

    /**
     * Copy action command.
     *
     * @since 1.0.13
     */
    public static final String COPY_COMMAND = "COPY";

    /** Save action command. */
    public static final String SAVE_COMMAND = "SAVE";

    /** Action command to save as PNG. */
    private static final String SAVE_AS_PNG_COMMAND = "SAVE_AS_PNG";

    /** Action command to save as SVG. */
    private static final String SAVE_AS_SVG_COMMAND = "SAVE_AS_SVG";

    /** Action command to save as PDF. */
    private static final String SAVE_AS_PDF_COMMAND = "SAVE_AS_PDF";

    /** The chart that is displayed in the panel. */
    private JFreeChart chart;

    /** Storage for registered (chart) mouse listeners. */
    private transient EventListenerList chartMouseListeners;

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

    /** The popup menu for the frame. */
    private JPopupMenu popup;

    /** The drawing info collected the last time the chart was drawn. */
    private final ChartRenderingInfo info;

    /** The chart anchor point. */
    private Point2D anchor;

    /** The scale factor used to draw the chart. */
    private double scaleX;

    /** The scale factor used to draw the chart. */
    private double scaleY;

    /** The plot orientation. */
    private PlotOrientation orientation = PlotOrientation.VERTICAL;

    /** A flag that controls whether or not domain zooming is enabled. */
    private boolean domainZoomable = false;

    /** A flag that controls whether or not range zooming is enabled. */
    private boolean rangeZoomable = false;

    /**
     * The zoom rectangle starting point (selected by the user with a mouse click). This is a point on the screen, not
     * the chart (which may have been scaled up or down to fit the panel).
     */
    private Point2D zoomPoint = null;

    /** The zoom rectangle (selected by the user with the mouse). */
    private transient Rectangle2D zoomRectangle = null;

    /** Controls if the zoom rectangle is drawn as an outline or filled. */
    private boolean fillZoomRectangle = true;

    /** The minimum distance required to drag the mouse to trigger a zoom. */
    private int zoomTriggerDistance;

    /** A flag that controls whether or not horizontal tracing is enabled. */
    private boolean horizontalAxisTrace = false;

    /** A flag that controls whether or not vertical tracing is enabled. */
    private boolean verticalAxisTrace = false;

    /** A vertical trace line. */
    private transient Line2D verticalTraceLine;

    /** A horizontal trace line. */
    private transient Line2D horizontalTraceLine;

    /** Menu item for zooming in on a chart (both axes). */
    private JMenuItem zoomInBothMenuItem;

    /** Menu item for zooming in on a chart (domain axis). */
    private JMenuItem zoomInDomainMenuItem;

    /** Menu item for zooming in on a chart (range axis). */
    private JMenuItem zoomInRangeMenuItem;

    /** Menu item for zooming out on a chart. */
    private JMenuItem zoomOutBothMenuItem;

    /** Menu item for zooming out on a chart (domain axis). */
    private JMenuItem zoomOutDomainMenuItem;

    /** Menu item for zooming out on a chart (range axis). */
    private JMenuItem zoomOutRangeMenuItem;

    /** Menu item for resetting the zoom (both axes). */
    private JMenuItem zoomResetBothMenuItem;

    /** Menu item for resetting the zoom (domain axis only). */
    private JMenuItem zoomResetDomainMenuItem;

    /** Menu item for resetting the zoom (range axis only). */
    private JMenuItem zoomResetRangeMenuItem;

    /**
     * The default directory for saving charts to file.
     *
     * @since 1.0.7
     */
    private File defaultDirectoryForSaveAs;

    /** A flag that controls whether or not file extensions are enforced. */
    private boolean enforceFileExtensions;

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

    /** The factor used to zoom in on an axis range. */
    private double zoomInFactor = 0.9;

    /** The factor used to zoom out on an axis range. */
    private double zoomOutFactor = 1.1;

    /**
     * A flag that controls whether zoom operations are centred on the current anchor point, or the centre point of the
     * relevant axis.
     *
     * @since 1.0.7
     */
    private boolean zoomAroundAnchor;

    /**
     * The paint used to draw the zoom rectangle outline.
     *
     * @since 1.0.13
     */
    private transient Paint zoomOutlinePaint;

    /**
     * The zoom fill paint (should use transparency).
     *
     * @since 1.0.13
     */
    private transient Paint zoomFillPaint;

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
        this.chartMouseListeners = new EventListenerList();
        this.info = new ChartRenderingInfo();
        setPreferredSize(new Dimension(width, height));
        this.useBuffer = useBuffer;
        this.refreshBuffer = false;
        this.minimumDrawWidth = minimumDrawWidth;
        this.minimumDrawHeight = minimumDrawHeight;
        this.maximumDrawWidth = maximumDrawWidth;
        this.maximumDrawHeight = maximumDrawHeight;
        this.zoomTriggerDistance = DEFAULT_ZOOM_TRIGGER_DISTANCE;

        // set up popup menu...
        this.popup = createPopupMenu();

        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setDisplayToolTips(false);
        addMouseListener(this);
        addMouseMotionListener(this);

        this.defaultDirectoryForSaveAs = null;
        this.enforceFileExtensions = true;

        // initialize ChartPanel-specific tool tip delays with
        // values the from ToolTipManager.sharedInstance()
        final ToolTipManager ttm = ToolTipManager.sharedInstance();
        this.ownToolTipInitialDelay = ttm.getInitialDelay();
        this.ownToolTipDismissDelay = ttm.getDismissDelay();
        this.ownToolTipReshowDelay = ttm.getReshowDelay();

        this.zoomAroundAnchor = false;
        this.zoomOutlinePaint = Color.blue;
        this.zoomFillPaint = new Color(0, 0, 255, 63);
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
            final Plot plot = chart.getPlot();
            this.domainZoomable = false;
            this.rangeZoomable = false;
            if (plot instanceof Zoomable) {
                final Zoomable z = (Zoomable) plot;
                this.domainZoomable = z.isDomainZoomable();
                this.rangeZoomable = z.isRangeZoomable();
                this.orientation = z.getOrientation();
            }
        } else {
            this.domainZoomable = false;
            this.rangeZoomable = false;
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
     * Returns the popup menu.
     *
     * @return The popup menu.
     */
    public JPopupMenu getPopupMenu() {
        return this.popup;
    }

    /**
     * Sets the popup menu for the panel.
     *
     * @param popup
     *            the popup menu (<code>null</code> permitted).
     */
    public void setPopupMenu(final JPopupMenu popup) {
        this.popup = popup;
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
     * A convenience method that switches on mouse-based zooming.
     *
     * @param flag
     *            <code>true</code> enables zooming and rectangle fill on zoom.
     */
    public void setMouseZoomable(final boolean flag) {
        setMouseZoomable(flag, true);
    }

    /**
     * A convenience method that switches on mouse-based zooming.
     *
     * @param flag
     *            <code>true</code> if zooming enabled
     * @param fillRectangle
     *            <code>true</code> if zoom rectangle is filled, false if rectangle is shown as outline only.
     */
    public void setMouseZoomable(final boolean flag, final boolean fillRectangle) {
        setDomainZoomable(flag);
        setRangeZoomable(flag);
        setFillZoomRectangle(fillRectangle);
    }

    /**
     * Returns the flag that determines whether or not zooming is enabled for the domain axis.
     *
     * @return A boolean.
     */
    public boolean isDomainZoomable() {
        return this.domainZoomable;
    }

    /**
     * Sets the flag that controls whether or not zooming is enabled for the domain axis. A check is made to ensure that
     * the current plot supports zooming for the domain values.
     *
     * @param flag
     *            <code>true</code> enables zooming if possible.
     */
    public void setDomainZoomable(final boolean flag) {
        if (flag) {
            final Plot plot = this.chart.getPlot();
            if (plot instanceof Zoomable) {
                final Zoomable z = (Zoomable) plot;
                this.domainZoomable = flag && (z.isDomainZoomable());
            }
        } else {
            this.domainZoomable = false;
        }
    }

    /**
     * Returns the flag that determines whether or not zooming is enabled for the range axis.
     *
     * @return A boolean.
     */
    public boolean isRangeZoomable() {
        return this.rangeZoomable;
    }

    /**
     * A flag that controls mouse-based zooming on the vertical axis.
     *
     * @param flag
     *            <code>true</code> enables zooming.
     */
    public void setRangeZoomable(final boolean flag) {
        if (flag) {
            final Plot plot = this.chart.getPlot();
            if (plot instanceof Zoomable) {
                final Zoomable z = (Zoomable) plot;
                this.rangeZoomable = flag && (z.isRangeZoomable());
            }
        } else {
            this.rangeZoomable = false;
        }
    }

    /**
     * Returns the flag that controls whether or not the zoom rectangle is filled when drawn.
     *
     * @return A boolean.
     */
    public boolean getFillZoomRectangle() {
        return this.fillZoomRectangle;
    }

    /**
     * A flag that controls how the zoom rectangle is drawn.
     *
     * @param flag
     *            <code>true</code> instructs to fill the rectangle on zoom, otherwise it will be outlined.
     */
    public void setFillZoomRectangle(final boolean flag) {
        this.fillZoomRectangle = flag;
    }

    /**
     * Returns the zoom trigger distance. This controls how far the mouse must move before a zoom action is triggered.
     *
     * @return The distance (in Java2D units).
     */
    public int getZoomTriggerDistance() {
        return this.zoomTriggerDistance;
    }

    /**
     * Sets the zoom trigger distance. This controls how far the mouse must move before a zoom action is triggered.
     *
     * @param distance
     *            the distance (in Java2D units).
     */
    public void setZoomTriggerDistance(final int distance) {
        this.zoomTriggerDistance = distance;
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

    /**
     * Returns the default directory for the "save as" option.
     *
     * @return The default directory (possibly <code>null</code>).
     *
     * @since 1.0.7
     */
    public File getDefaultDirectoryForSaveAs() {
        return this.defaultDirectoryForSaveAs;
    }

    /**
     * Sets the default directory for the "save as" option. If you set this to <code>null</code>, the user's default
     * directory will be used.
     *
     * @param directory
     *            the directory (<code>null</code> permitted).
     *
     * @since 1.0.7
     */
    public void setDefaultDirectoryForSaveAs(final File directory) {
        if (directory != null) {
            if (!directory.isDirectory()) {
                throw new IllegalArgumentException("The 'directory' argument is not a directory.");
            }
        }
        this.defaultDirectoryForSaveAs = directory;
    }

    /**
     * Returns <code>true</code> if file extensions should be enforced, and <code>false</code> otherwise.
     *
     * @return The flag.
     *
     * @see #setEnforceFileExtensions(boolean)
     */
    public boolean isEnforceFileExtensions() {
        return this.enforceFileExtensions;
    }

    /**
     * Sets a flag that controls whether or not file extensions are enforced.
     *
     * @param enforce
     *            the new flag value.
     *
     * @see #isEnforceFileExtensions()
     */
    public void setEnforceFileExtensions(final boolean enforce) {
        this.enforceFileExtensions = enforce;
    }

    /**
     * Returns the flag that controls whether or not zoom operations are centered around the current anchor point.
     *
     * @return A boolean.
     *
     * @since 1.0.7
     *
     * @see #setZoomAroundAnchor(boolean)
     */
    public boolean getZoomAroundAnchor() {
        return this.zoomAroundAnchor;
    }

    /**
     * Sets the flag that controls whether or not zoom operations are centered around the current anchor point.
     *
     * @param zoomAroundAnchor
     *            the new flag value.
     *
     * @since 1.0.7
     *
     * @see #getZoomAroundAnchor()
     */
    public void setZoomAroundAnchor(final boolean zoomAroundAnchor) {
        this.zoomAroundAnchor = zoomAroundAnchor;
    }

    /**
     * Returns the zoom rectangle fill paint.
     *
     * @return The zoom rectangle fill paint (never <code>null</code>).
     *
     * @see #setZoomFillPaint(java.awt.Paint)
     * @see #setFillZoomRectangle(boolean)
     *
     * @since 1.0.13
     */
    public Paint getZoomFillPaint() {
        return this.zoomFillPaint;
    }

    /**
     * Sets the zoom rectangle fill paint.
     *
     * @param paint
     *            the paint (<code>null</code> not permitted).
     *
     * @see #getZoomFillPaint()
     * @see #getFillZoomRectangle()
     *
     * @since 1.0.13
     */
    public void setZoomFillPaint(final Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.zoomFillPaint = paint;
    }

    /**
     * Returns the zoom rectangle outline paint.
     *
     * @return The zoom rectangle outline paint (never <code>null</code>).
     *
     * @see #setZoomOutlinePaint(java.awt.Paint)
     * @see #setFillZoomRectangle(boolean)
     *
     * @since 1.0.13
     */
    public Paint getZoomOutlinePaint() {
        return this.zoomOutlinePaint;
    }

    /**
     * Sets the zoom rectangle outline paint.
     *
     * @param paint
     *            the paint (<code>null</code> not permitted).
     *
     * @see #getZoomOutlinePaint()
     * @see #getFillZoomRectangle()
     *
     * @since 1.0.13
     */
    public void setZoomOutlinePaint(final Paint paint) {
        this.zoomOutlinePaint = paint;
    }

    /**
     * The mouse wheel handler.
     */
    private MouseWheelHandler mouseWheelHandler;

    private int allowedRangeGap = 5;

    public int getAllowedRangeGap() {
        return allowedRangeGap;
    }

    public void setAllowedRangeGap(final int allowedRangeGap) {
        this.allowedRangeGap = allowedRangeGap;
    }

    /**
     * Returns <code>true</code> if the mouse wheel handler is enabled, and <code>false</code> otherwise.
     *
     * @return A boolean.
     *
     * @since 1.0.13
     */
    public boolean isMouseWheelEnabled() {
        return this.mouseWheelHandler != null;
    }

    /**
     * Enables or disables mouse wheel support for the panel.
     *
     * @param flag
     *            a boolean.
     *
     * @since 1.0.13
     */
    public void setMouseWheelEnabled(final boolean flag) {
        if (flag && this.mouseWheelHandler == null) {
            this.mouseWheelHandler = new MouseWheelHandler(this);
        } else if (!flag && this.mouseWheelHandler != null) {
            this.removeMouseWheelListener(this.mouseWheelHandler);
            this.mouseWheelHandler = null;
        }
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

        // redraw the zoom rectangle (if present) - if useBuffer is false,
        // we use XOR so we can XOR the rectangle away again without redrawing
        // the chart
        drawZoomRectangle(g2, !this.useBuffer);

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
        final Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            final Zoomable z = (Zoomable) plot;
            this.orientation = z.getOrientation();
        }
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
     * Handles action events generated by the popup menu.
     *
     * @param event
     *            the event.
     */
    @Override
    public void actionPerformed(final ActionEvent event) {

        final String command = event.getActionCommand();

        if (command.equals(COPY_COMMAND)) {
            doCopy();
        } else if (command.equals(SAVE_AS_PNG_COMMAND)) {
            try {
                doSaveAs();
            } catch (final IOException e) {
                javax.swing.JOptionPane.showMessageDialog(this, "I/O error occurred.", "Save As PNG",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
            }
        } else if (command.equals(SAVE_AS_SVG_COMMAND)) {
            try {
                saveAsSVG(null);
            } catch (final IOException e) {
                javax.swing.JOptionPane.showMessageDialog(this, "I/O error occurred.", "Save As SVG",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
            }
        } else if (command.equals(SAVE_AS_PDF_COMMAND)) {
            saveAsPDF(null);
        }

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
        if (e.isPopupTrigger()) {
            if (this.popup != null) {
                displayPopupMenu(e.getX(), e.getY());
            }
        } else {
            if (plot instanceof Pannable && isPanAllowed()) {
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
    }

    protected boolean isPanAllowed() {
        return true;
    }

    /**
     * Returns a point based on (x, y) but constrained to be within the bounds of the given rectangle. This method could
     * be moved to JCommon.
     *
     * @param x
     *            the x-coordinate.
     * @param y
     *            the y-coordinate.
     * @param area
     *            the rectangle (<code>null</code> not permitted).
     *
     * @return A point within the rectangle.
     */
    private Point2D getPointInRectangle(final int x, final int y, final Rectangle2D area) {
        final double xx = Math.max(area.getMinX(), Math.min(x, area.getMaxX()));
        final double yy = Math.max(area.getMinY(), Math.min(y, area.getMaxY()));
        return new Point2D.Double(xx, yy);
    }

    /**
     * Handles a 'mouse dragged' event.
     *
     * @param e
     *            the mouse event.
     */
    @Override
    public void mouseDragged(final MouseEvent e) {

        // if the popup menu has already been triggered, then ignore dragging...
        if (this.popup != null && this.popup.isShowing()) {
            return;
        }

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

        // if no initial zoom point was set, ignore dragging...
        if (this.zoomPoint == null) {
            return;
        }
        final Graphics2D g2 = (Graphics2D) getGraphics();

        // erase the previous zoom rectangle (if any).  We only need to do
        // this is we are using XOR mode, which we do when we're not using
        // the buffer (if there is a buffer, then at the end of this method we
        // just trigger a repaint)
        if (!this.useBuffer) {
            drawZoomRectangle(g2, true);
        }

        boolean hZoom, vZoom;
        if (this.orientation == PlotOrientation.HORIZONTAL) {
            hZoom = this.rangeZoomable;
            vZoom = this.domainZoomable;
        } else {
            hZoom = this.domainZoomable;
            vZoom = this.rangeZoomable;
        }
        final Rectangle2D scaledDataArea = getScreenDataArea((int) this.zoomPoint.getX(), (int) this.zoomPoint.getY());
        if (hZoom && vZoom) {
            // selected rectangle shouldn't extend outside the data area...
            final double xmax = Math.min(e.getX(), scaledDataArea.getMaxX());
            final double ymax = Math.min(e.getY(), scaledDataArea.getMaxY());
            this.zoomRectangle = new Rectangle2D.Double(this.zoomPoint.getX(), this.zoomPoint.getY(),
                    xmax - this.zoomPoint.getX(), ymax - this.zoomPoint.getY());
        } else if (hZoom) {
            final double xmax = Math.min(e.getX(), scaledDataArea.getMaxX());
            this.zoomRectangle = new Rectangle2D.Double(this.zoomPoint.getX(), scaledDataArea.getMinY(),
                    xmax - this.zoomPoint.getX(), scaledDataArea.getHeight());
        } else if (vZoom) {
            final double ymax = Math.min(e.getY(), scaledDataArea.getMaxY());
            this.zoomRectangle = new Rectangle2D.Double(scaledDataArea.getMinX(), this.zoomPoint.getY(),
                    scaledDataArea.getWidth(), ymax - this.zoomPoint.getY());
        }

        // Draw the new zoom rectangle...
        if (this.useBuffer) {
            repaint();
        } else {
            // with no buffer, we use XOR to draw the rectangle "over" the
            // chart...
            drawZoomRectangle(g2, true);
        }
        g2.dispose();

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

        else if (this.zoomRectangle != null) {
            boolean hZoom, vZoom;
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                hZoom = this.rangeZoomable;
                vZoom = this.domainZoomable;
            } else {
                hZoom = this.domainZoomable;
                vZoom = this.rangeZoomable;
            }

            final boolean zoomTrigger1 = hZoom
                    && Math.abs(e.getX() - this.zoomPoint.getX()) >= this.zoomTriggerDistance;
            final boolean zoomTrigger2 = vZoom
                    && Math.abs(e.getY() - this.zoomPoint.getY()) >= this.zoomTriggerDistance;
            if (zoomTrigger1 || zoomTrigger2) {
                if ((hZoom && (e.getX() < this.zoomPoint.getX())) || (vZoom && (e.getY() < this.zoomPoint.getY()))) {
                    restoreAutoBounds();
                } else {
                    double x, y, w, h;
                    final Rectangle2D screenDataArea = getScreenDataArea((int) this.zoomPoint.getX(),
                            (int) this.zoomPoint.getY());
                    final double maxX = screenDataArea.getMaxX();
                    final double maxY = screenDataArea.getMaxY();
                    // for mouseReleased event, (horizontalZoom || verticalZoom)
                    // will be true, so we can just test for either being false;
                    // otherwise both are true
                    if (!vZoom) {
                        x = this.zoomPoint.getX();
                        y = screenDataArea.getMinY();
                        w = Math.min(this.zoomRectangle.getWidth(), maxX - this.zoomPoint.getX());
                        h = screenDataArea.getHeight();
                    } else if (!hZoom) {
                        x = screenDataArea.getMinX();
                        y = this.zoomPoint.getY();
                        w = screenDataArea.getWidth();
                        h = Math.min(this.zoomRectangle.getHeight(), maxY - this.zoomPoint.getY());
                    } else {
                        x = this.zoomPoint.getX();
                        y = this.zoomPoint.getY();
                        w = Math.min(this.zoomRectangle.getWidth(), maxX - this.zoomPoint.getX());
                        h = Math.min(this.zoomRectangle.getHeight(), maxY - this.zoomPoint.getY());
                    }
                    final Rectangle2D zoomArea = new Rectangle2D.Double(x, y, w, h);
                    zoom(zoomArea);
                }
                this.zoomPoint = null;
                this.zoomRectangle = null;
            } else {
                // erase the zoom rectangle
                final Graphics2D g2 = (Graphics2D) getGraphics();
                if (this.useBuffer) {
                    repaint();
                } else {
                    drawZoomRectangle(g2, true);
                }
                g2.dispose();
                this.zoomPoint = null;
                this.zoomRectangle = null;
            }

        }

        else if (e.isPopupTrigger()) {
            if (this.popup != null) {
                displayPopupMenu(e.getX(), e.getY());
            }
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
        // new entity code...
        final Object[] listeners = this.chartMouseListeners.getListeners(ChartMouseListener.class);
        if (listeners.length == 0) {
            return;
        }

        ChartEntity entity = null;
        if (this.info != null) {
            final EntityCollection entities = this.info.getEntityCollection();
            if (entities != null) {
                entity = entities.getEntity(x, y);
            }
        }
        final ChartMouseEvent chartEvent = new ChartMouseEvent(getChart(), event, entity);
        for (int i = listeners.length - 1; i >= 0; i -= 1) {
            ((ChartMouseListener) listeners[i]).chartMouseClicked(chartEvent);
        }

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

        final Object[] listeners = this.chartMouseListeners.getListeners(ChartMouseListener.class);
        if (listeners.length == 0) {
            return;
        }
        final Insets insets = getInsets();
        final int x = (int) ((e.getX() - insets.left) / this.scaleX);
        final int y = (int) ((e.getY() - insets.top) / this.scaleY);

        ChartEntity entity = null;
        if (this.info != null) {
            final EntityCollection entities = this.info.getEntityCollection();
            if (entities != null) {
                entity = entities.getEntity(x, y);
            }
        }

        // we can only generate events if the panel's chart is not null
        // (see bug report 1556951)
        if (this.chart != null) {
            final ChartMouseEvent event = new ChartMouseEvent(getChart(), e, entity);
            for (int i = listeners.length - 1; i >= 0; i -= 1) {
                ((ChartMouseListener) listeners[i]).chartMouseMoved(event);
            }
        }

    }

    /**
     * Zooms in on an anchor point (specified in screen coordinate space).
     *
     * @param x
     *            the x value (in screen coordinates).
     * @param y
     *            the y value (in screen coordinates).
     */
    public void zoomInBoth(final double x, final double y) {
        final Plot plot = this.chart.getPlot();
        if (plot == null) {
            return;
        }
        // here we tweak the notify flag on the plot so that only
        // one notification happens even though we update multiple
        // axes...
        final boolean savedNotify = plot.isNotify();
        plot.setNotify(false);
        zoomInDomain(x, y);
        zoomInRange(x, y);
        plot.setNotify(savedNotify);
    }

    /**
     * Decreases the length of the domain axis, centered about the given coordinate on the screen. The length of the
     * domain axis is reduced by the value of {@link #getZoomInFactor()}.
     *
     * @param x
     *            the x coordinate (in screen coordinates).
     * @param y
     *            the y-coordinate (in screen coordinates).
     */
    public void zoomInDomain(final double x, final double y) {
        final Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            final boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            final Zoomable z = (Zoomable) plot;
            z.zoomDomainAxes(this.zoomInFactor, this.info.getPlotInfo(),
                    translateScreenToJava2D(new Point((int) x, (int) y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Decreases the length of the range axis, centered about the given coordinate on the screen. The length of the
     * range axis is reduced by the value of {@link #getZoomInFactor()}.
     *
     * @param x
     *            the x-coordinate (in screen coordinates).
     * @param y
     *            the y coordinate (in screen coordinates).
     */
    public void zoomInRange(final double x, final double y) {
        final Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            final boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            final Zoomable z = (Zoomable) plot;
            z.zoomRangeAxes(this.zoomInFactor, this.info.getPlotInfo(),
                    translateScreenToJava2D(new Point((int) x, (int) y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Zooms out on an anchor point (specified in screen coordinate space).
     *
     * @param x
     *            the x value (in screen coordinates).
     * @param y
     *            the y value (in screen coordinates).
     */
    public void zoomOutBoth(final double x, final double y) {
        final Plot plot = this.chart.getPlot();
        if (plot == null) {
            return;
        }
        // here we tweak the notify flag on the plot so that only
        // one notification happens even though we update multiple
        // axes...
        final boolean savedNotify = plot.isNotify();
        plot.setNotify(false);
        zoomOutDomain(x, y);
        zoomOutRange(x, y);
        plot.setNotify(savedNotify);
    }

    /**
     * Increases the length of the domain axis, centered about the given coordinate on the screen. The length of the
     * domain axis is increased by the value of {@link #getZoomOutFactor()}.
     *
     * @param x
     *            the x coordinate (in screen coordinates).
     * @param y
     *            the y-coordinate (in screen coordinates).
     */
    public void zoomOutDomain(final double x, final double y) {
        final Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            final boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            final Zoomable z = (Zoomable) plot;
            z.zoomDomainAxes(this.zoomOutFactor, this.info.getPlotInfo(),
                    translateScreenToJava2D(new Point((int) x, (int) y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Increases the length the range axis, centered about the given coordinate on the screen. The length of the range
     * axis is increased by the value of {@link #getZoomOutFactor()}.
     *
     * @param x
     *            the x coordinate (in screen coordinates).
     * @param y
     *            the y-coordinate (in screen coordinates).
     */
    public void zoomOutRange(final double x, final double y) {
        final Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            final boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            final Zoomable z = (Zoomable) plot;
            z.zoomRangeAxes(this.zoomOutFactor, this.info.getPlotInfo(),
                    translateScreenToJava2D(new Point((int) x, (int) y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Zooms in on a selected region.
     *
     * @param selection
     *            the selected region.
     */
    public void zoom(final Rectangle2D selection) {

        // get the origin of the zoom selection in the Java2D space used for
        // drawing the chart (that is, before any scaling to fit the panel)
        final Point2D selectOrigin = translateScreenToJava2D(
                new Point((int) Math.ceil(selection.getX()), (int) Math.ceil(selection.getY())));
        final PlotRenderingInfo plotInfo = this.info.getPlotInfo();
        final Rectangle2D scaledDataArea = getScreenDataArea((int) selection.getCenterX(),
                (int) selection.getCenterY());
        if ((selection.getHeight() > 0) && (selection.getWidth() > 0)) {

            final double hLower = (selection.getMinX() - scaledDataArea.getMinX()) / scaledDataArea.getWidth();
            final double hUpper = (selection.getMaxX() - scaledDataArea.getMinX()) / scaledDataArea.getWidth();
            final double vLower = (scaledDataArea.getMaxY() - selection.getMaxY()) / scaledDataArea.getHeight();
            final double vUpper = (scaledDataArea.getMaxY() - selection.getMinY()) / scaledDataArea.getHeight();

            final Plot p = this.chart.getPlot();
            if (p instanceof Zoomable) {
                // here we tweak the notify flag on the plot so that only
                // one notification happens even though we update multiple
                // axes...
                final boolean savedNotify = p.isNotify();
                p.setNotify(false);
                final Zoomable z = (Zoomable) p;
                if (z.getOrientation() == PlotOrientation.HORIZONTAL) {
                    z.zoomDomainAxes(vLower, vUpper, plotInfo, selectOrigin);
                    z.zoomRangeAxes(hLower, hUpper, plotInfo, selectOrigin);
                } else {
                    z.zoomDomainAxes(hLower, hUpper, plotInfo, selectOrigin);
                    z.zoomRangeAxes(vLower, vUpper, plotInfo, selectOrigin);
                }
                p.setNotify(savedNotify);
            }

        }

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
            final Point2D zp = (this.zoomPoint != null ? this.zoomPoint : new Point());
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
            final Point2D zp = (this.zoomPoint != null ? this.zoomPoint : new Point());
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
     * Returns the zoom in factor.
     *
     * @return The zoom in factor.
     *
     * @see #setZoomInFactor(double)
     */
    public double getZoomInFactor() {
        return this.zoomInFactor;
    }

    /**
     * Sets the zoom in factor.
     *
     * @param factor
     *            the factor.
     *
     * @see #getZoomInFactor()
     */
    public void setZoomInFactor(final double factor) {
        this.zoomInFactor = factor;
    }

    /**
     * Returns the zoom out factor.
     *
     * @return The zoom out factor.
     *
     * @see #setZoomOutFactor(double)
     */
    public double getZoomOutFactor() {
        return this.zoomOutFactor;
    }

    /**
     * Sets the zoom out factor.
     *
     * @param factor
     *            the factor.
     *
     * @see #getZoomOutFactor()
     */
    public void setZoomOutFactor(final double factor) {
        this.zoomOutFactor = factor;
    }

    /**
     * Draws zoom rectangle (if present). The drawing is performed in XOR mode, therefore when this method is called
     * twice in a row, the second call will completely restore the state of the canvas.
     *
     * @param g2
     *            the graphics device.
     * @param xor
     *            use XOR for drawing?
     */
    private void drawZoomRectangle(final Graphics2D g2, final boolean xor) {
        if (this.zoomRectangle != null) {
            if (xor) {
                // Set XOR mode to draw the zoom rectangle
                g2.setXORMode(Color.gray);
            }
            if (this.fillZoomRectangle) {
                g2.setPaint(this.zoomFillPaint);
                g2.fill(this.zoomRectangle);
            } else {
                g2.setPaint(this.zoomOutlinePaint);
                g2.draw(this.zoomRectangle);
            }
            if (xor) {
                // Reset to the default 'overwrite' mode
                g2.setPaintMode();
            }
        }
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

    /**
     * Copies the current chart to the system clipboard.
     * 
     * @since 1.0.13
     */
    public void doCopy() {
        final Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final Insets insets = getInsets();
        final int w = getWidth() - insets.left - insets.right;
        final int h = getHeight() - insets.top - insets.bottom;
        final ChartTransferable selection = new ChartTransferable(this.chart, w, h, getMinimumDrawWidth(),
                getMinimumDrawHeight(), getMaximumDrawWidth(), getMaximumDrawHeight(), true);
        systemClipboard.setContents(selection, null);
    }

    /**
     * Opens a file chooser and gives the user an opportunity to save the chart in PNG format.
     *
     * @throws IOException
     *             if there is an I/O error.
     */
    public void doSaveAs() throws IOException {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(this.defaultDirectoryForSaveAs);
        final FileNameExtensionFilter filter = new FileNameExtensionFilter(
                localizationResources.getString("PNG_Image_Files"), "png");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);

        final int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            if (isEnforceFileExtensions()) {
                if (!filename.endsWith(".png")) {
                    filename = filename + ".png";
                }
            }
            ChartUtils.saveChartAsPNG(new File(filename), this.chart, getWidth(), getHeight());
        }
    }

    /**
     * Saves the chart in SVG format (a filechooser will be displayed so that the user can specify the filename). Note
     * that this method only works if the JFreeSVG library is on the classpath...if this library is not present, the
     * method will fail.
     */
    private void saveAsSVG(final File f) throws IOException {
        File file = f;
        if (file == null) {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(this.defaultDirectoryForSaveAs);
            final FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    localizationResources.getString("SVG_Files"), "svg");
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setFileFilter(filter);

            final int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getPath();
                if (isEnforceFileExtensions()) {
                    if (!filename.endsWith(".svg")) {
                        filename = filename + ".svg";
                    }
                }
                file = new File(filename);
                if (file.exists()) {
                    final String fileExists = localizationResources.getString("FILE_EXISTS_CONFIRM_OVERWRITE");
                    final int response = javax.swing.JOptionPane.showConfirmDialog(this, fileExists, "Save As SVG",
                            javax.swing.JOptionPane.OK_CANCEL_OPTION);
                    if (response == javax.swing.JOptionPane.CANCEL_OPTION) {
                        file = null;
                    }
                }
            }
        }

        if (file != null) {
            // use reflection to get the SVG string
            final String svg = generateSVG(getWidth(), getHeight());
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(
                        "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
                writer.write(svg + "\n");
                writer.flush();
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (final IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

        }
    }

    /**
     * Generates a string containing a rendering of the chart in SVG format. This feature is only supported if the
     * JFreeSVG library is included on the classpath.
     * 
     * @return A string containing an SVG element for the current chart, or <code>null</code> if there is a problem with
     *         the method invocation by reflection.
     */
    private String generateSVG(final int width, final int height) {
        final Graphics2D g2 = createSVGGraphics2D(width, height);
        if (g2 == null) {
            throw new IllegalStateException("JFreeSVG library is not present.");
        }
        // we suppress shadow generation, because SVG is a vector format and
        // the shadow effect is applied via bitmap effects...
        g2.setRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION, true);
        String svg = null;
        final Rectangle2D drawArea = new Rectangle2D.Double(0, 0, width, height);
        this.chart.draw(g2, drawArea);
        try {
            final Method m = g2.getClass().getMethod("getSVGElement");
            svg = (String) m.invoke(g2);
        } catch (final NoSuchMethodException e) {
            // null will be returned
        } catch (final SecurityException e) {
            // null will be returned
        } catch (final IllegalAccessException e) {
            // null will be returned
        } catch (final IllegalArgumentException e) {
            // null will be returned
        } catch (final InvocationTargetException e) {
            // null will be returned
        }
        return svg;
    }

    private Graphics2D createSVGGraphics2D(final int w, final int h) {
        try {
            final Class svgGraphics2d = Class.forName("org.jfree.graphics2d.svg.SVGGraphics2D");
            final Constructor ctor = svgGraphics2d.getConstructor(int.class, int.class);
            return (Graphics2D) ctor.newInstance(w, h);
        } catch (final ClassNotFoundException ex) {
            return null;
        } catch (final NoSuchMethodException ex) {
            return null;
        } catch (final SecurityException ex) {
            return null;
        } catch (final InstantiationException ex) {
            return null;
        } catch (final IllegalAccessException ex) {
            return null;
        } catch (final IllegalArgumentException ex) {
            return null;
        } catch (final InvocationTargetException ex) {
            return null;
        }
    }

    /**
     * Saves the chart in PDF format (a filechooser will be displayed so that the user can specify the filename). Note
     * that this method only works if the OrsonPDF library is on the classpath...if this library is not present, the
     * method will fail.
     */
    private void saveAsPDF(final File f) {
        File file = f;
        if (file == null) {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(this.defaultDirectoryForSaveAs);
            final FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    localizationResources.getString("PDF_Files"), "pdf");
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setFileFilter(filter);

            final int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getPath();
                if (isEnforceFileExtensions()) {
                    if (!filename.endsWith(".pdf")) {
                        filename = filename + ".pdf";
                    }
                }
                file = new File(filename);
                if (file.exists()) {
                    final String fileExists = localizationResources.getString("FILE_EXISTS_CONFIRM_OVERWRITE");
                    final int response = javax.swing.JOptionPane.showConfirmDialog(this, fileExists, "Save As PDF",
                            javax.swing.JOptionPane.OK_CANCEL_OPTION);
                    if (response == javax.swing.JOptionPane.CANCEL_OPTION) {
                        file = null;
                    }
                }
            }
        }

        if (file != null) {
            writeAsPDF(file, getWidth(), getHeight());
        }
    }

    /**
     * Returns <code>true</code> if OrsonPDF is on the classpath, and <code>false</code> otherwise. The OrsonPDF library
     * can be found at http://www.object-refinery.com/pdf/
     * 
     * @return A boolean.
     */
    private boolean isOrsonPDFAvailable() {
        Class pdfDocumentClass = null;
        try {
            pdfDocumentClass = Class.forName("com.orsonpdf.PDFDocument");
        } catch (final ClassNotFoundException e) {
            // pdfDocument class will be null so the function will return false
        }
        return (pdfDocumentClass != null);
    }

    /**
     * Writes the current chart to the specified file in PDF format. This will only work when the OrsonPDF library is
     * found on the classpath. Reflection is used to ensure there is no compile-time dependency on OrsonPDF (which is
     * non-free software).
     * 
     * @param file
     *            the output file (<code>null</code> not permitted).
     * @param w
     *            the chart width.
     * @param h
     *            the chart height.
     */
    private void writeAsPDF(final File file, final int w, final int h) {
        if (!isOrsonPDFAvailable()) {
            throw new IllegalStateException("OrsonPDF is not present on the classpath.");
        }
        Args.nullNotPermitted(file, "file");
        try {
            final Class pdfDocClass = Class.forName("com.orsonpdf.PDFDocument");
            final Object pdfDoc = pdfDocClass.newInstance();
            final Method m = pdfDocClass.getMethod("createPage", Rectangle2D.class);
            final Rectangle2D rect = new Rectangle(w, h);
            final Object page = m.invoke(pdfDoc, rect);
            final Method m2 = page.getClass().getMethod("getGraphics2D");
            final Graphics2D g2 = (Graphics2D) m2.invoke(page);
            // we suppress shadow generation, because PDF is a vector format and
            // the shadow effect is applied via bitmap effects...
            g2.setRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION, true);
            final Rectangle2D drawArea = new Rectangle2D.Double(0, 0, w, h);
            this.chart.draw(g2, drawArea);
            final Method m3 = pdfDocClass.getMethod("writeToFile", File.class);
            m3.invoke(pdfDoc, file);
        } catch (final ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (final InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (final IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (final NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        } catch (final SecurityException ex) {
            throw new RuntimeException(ex);
        } catch (final IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (final InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates a print job for the chart.
     */
    public void createChartPrintJob() {
        final PrinterJob job = PrinterJob.getPrinterJob();
        final PageFormat pf = job.defaultPage();
        final PageFormat pf2 = job.pageDialog(pf);
        if (pf2 != pf) {
            job.setPrintable(this, pf2);
            if (job.printDialog()) {
                try {
                    job.print();
                } catch (final PrinterException e) {
                    javax.swing.JOptionPane.showMessageDialog(this, e);
                }
            }
        }
    }

    /**
     * Prints the chart on a single page.
     *
     * @param g
     *            the graphics context.
     * @param pf
     *            the page format to use.
     * @param pageIndex
     *            the index of the page. If not <code>0</code>, nothing gets print.
     *
     * @return The result of printing.
     */
    @Override
    public int print(final Graphics g, final PageFormat pf, final int pageIndex) {

        if (pageIndex != 0) {
            return NO_SUCH_PAGE;
        }
        final Graphics2D g2 = (Graphics2D) g;
        final double x = pf.getImageableX();
        final double y = pf.getImageableY();
        final double w = pf.getImageableWidth();
        final double h = pf.getImageableHeight();
        this.chart.draw(g2, new Rectangle2D.Double(x, y, w, h), this.anchor, null);
        return PAGE_EXISTS;

    }

    /**
     * Adds a listener to the list of objects listening for chart mouse events.
     *
     * @param listener
     *            the listener (<code>null</code> not permitted).
     */
    public void addChartMouseListener(final ChartMouseListener listener) {
        Args.nullNotPermitted(listener, "listener");
        this.chartMouseListeners.add(ChartMouseListener.class, listener);
    }

    /**
     * Removes a listener from the list of objects listening for chart mouse events.
     *
     * @param listener
     *            the listener.
     */
    public void removeChartMouseListener(final ChartMouseListener listener) {
        this.chartMouseListeners.remove(ChartMouseListener.class, listener);
    }

    /**
     * Returns an array of the listeners of the given type registered with the panel.
     *
     * @param listenerType
     *            the listener type.
     *
     * @return An array of listeners.
     */
    @Override
    public EventListener[] getListeners(final Class listenerType) {
        if (listenerType == ChartMouseListener.class) {
            // fetch listeners from local storage
            return this.chartMouseListeners.getListeners(listenerType);
        } else {
            return super.getListeners(listenerType);
        }
    }

    /**
     * Creates a popup menu for the panel.
     *
     * @param properties
     *            include a menu item for the chart property editor.
     * @param copy
     *            include a menu item for copying to the clipboard.
     * @param save
     *            include a menu item for saving the chart.
     * @param print
     *            include a menu item for printing the chart.
     * @param zoom
     *            include menu items for zooming.
     *
     * @return The popup menu.
     *
     * @since 1.0.13
     */
    protected JPopupMenu createPopupMenu() {

        final JPopupMenu result = new JPopupMenu(localizationResources.getString("Chart") + ":");

        //copy
        final JMenuItem copyItem = new JMenuItem(localizationResources.getString("Copy"));
        copyItem.setActionCommand(COPY_COMMAND);
        copyItem.addActionListener(this);
        result.add(copyItem);

        //save
        result.addSeparator();
        final JMenu saveSubMenu = new JMenu(localizationResources.getString("Save_as"));
        final JMenuItem pngItem = new JMenuItem(localizationResources.getString("PNG..."));
        pngItem.setActionCommand("SAVE_AS_PNG");
        pngItem.addActionListener(this);
        saveSubMenu.add(pngItem);

        if (createSVGGraphics2D(10, 10) != null) {
            final JMenuItem svgItem = new JMenuItem(localizationResources.getString("SVG..."));
            svgItem.setActionCommand("SAVE_AS_SVG");
            svgItem.addActionListener(this);
            saveSubMenu.add(svgItem);
        }

        if (isOrsonPDFAvailable()) {
            final JMenuItem pdfItem = new JMenuItem(localizationResources.getString("PDF..."));
            pdfItem.setActionCommand("SAVE_AS_PDF");
            pdfItem.addActionListener(this);
            saveSubMenu.add(pdfItem);
        }
        result.add(saveSubMenu);

        return result;

    }

    /**
     * The idea is to modify the zooming options depending on the type of chart being displayed by the panel.
     *
     * @param x
     *            horizontal position of the popup.
     * @param y
     *            vertical position of the popup.
     */
    protected void displayPopupMenu(final int x, final int y) {

        if (this.popup == null) {
            return;
        }

        // go through each zoom menu item and decide whether or not to
        // enable it...
        boolean isDomainZoomable = false;
        boolean isRangeZoomable = false;
        final Plot plot = (this.chart != null ? this.chart.getPlot() : null);
        if (plot instanceof Zoomable) {
            final Zoomable z = (Zoomable) plot;
            isDomainZoomable = z.isDomainZoomable();
            isRangeZoomable = z.isRangeZoomable();
        }

        if (this.zoomInDomainMenuItem != null) {
            this.zoomInDomainMenuItem.setEnabled(isDomainZoomable);
        }
        if (this.zoomOutDomainMenuItem != null) {
            this.zoomOutDomainMenuItem.setEnabled(isDomainZoomable);
        }
        if (this.zoomResetDomainMenuItem != null) {
            this.zoomResetDomainMenuItem.setEnabled(isDomainZoomable);
        }

        if (this.zoomInRangeMenuItem != null) {
            this.zoomInRangeMenuItem.setEnabled(isRangeZoomable);
        }
        if (this.zoomOutRangeMenuItem != null) {
            this.zoomOutRangeMenuItem.setEnabled(isRangeZoomable);
        }

        if (this.zoomResetRangeMenuItem != null) {
            this.zoomResetRangeMenuItem.setEnabled(isRangeZoomable);
        }

        if (this.zoomInBothMenuItem != null) {
            this.zoomInBothMenuItem.setEnabled(isDomainZoomable && isRangeZoomable);
        }
        if (this.zoomOutBothMenuItem != null) {
            this.zoomOutBothMenuItem.setEnabled(isDomainZoomable && isRangeZoomable);
        }
        if (this.zoomResetBothMenuItem != null) {
            this.zoomResetBothMenuItem.setEnabled(isDomainZoomable && isRangeZoomable);
        }

        this.popup.show(this, x, y);

    }

    /**
     * Updates the UI for a LookAndFeel change.
     */
    @Override
    public void updateUI() {
        // here we need to update the UI for the popup menu, if the panel
        // has one...
        if (this.popup != null) {
            SwingUtilities.updateComponentTreeUI(this.popup);
        }
        super.updateUI();
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
        SerialUtils.writePaint(this.zoomFillPaint, stream);
        SerialUtils.writePaint(this.zoomOutlinePaint, stream);
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
        this.zoomFillPaint = SerialUtils.readPaint(stream);
        this.zoomOutlinePaint = SerialUtils.readPaint(stream);

        // we create a new but empty chartMouseListeners list
        this.chartMouseListeners = new EventListenerList();

        // register as a listener with sub-components...
        if (this.chart != null) {
            this.chart.addChangeListener(this);
        }

    }

}
